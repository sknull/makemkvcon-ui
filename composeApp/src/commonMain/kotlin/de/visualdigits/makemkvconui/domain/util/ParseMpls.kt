package de.visualdigits.makemkvconui.domain.util

import de.visualdigits.makemkvconui.domain.model.bluray.mpls.AppInfoPlayList
import de.visualdigits.makemkvconui.domain.model.bluray.mpls.ExtDataEntry
import de.visualdigits.makemkvconui.domain.model.bluray.mpls.ExtensionData
import de.visualdigits.makemkvconui.domain.model.bluray.mpls.Header
import de.visualdigits.makemkvconui.domain.model.bluray.mpls.MultiClipEntry
import de.visualdigits.makemkvconui.domain.model.bluray.mpls.PlayItem
import de.visualdigits.makemkvconui.domain.model.bluray.mpls.PlayList
import de.visualdigits.makemkvconui.domain.model.bluray.mpls.PlayListMarkItem
import de.visualdigits.makemkvconui.domain.model.bluray.mpls.PlayListMarks
import de.visualdigits.makemkvconui.domain.model.bluray.mpls.STNTable
import de.visualdigits.makemkvconui.domain.model.bluray.mpls.STNTableEntry
import de.visualdigits.makemkvconui.domain.model.bluray.mpls.StreamAttributes
import de.visualdigits.makemkvconui.domain.model.bluray.mpls.StreamEntry
import de.visualdigits.makemkvconui.domain.model.bluray.mpls.SubPath
import de.visualdigits.makemkvconui.domain.model.bluray.mpls.SubPlayItem
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class MPLS(file: File) {

    // Lateinit-Variablen für deine ausgelagerten Klassen
    var header: Header
    var appInfoPlayList: AppInfoPlayList
    var playList: PlayList
    var playListMarks: PlayListMarks
    var extensionData: ExtensionData

    init {
        RandomAccessFile(file, "r").use { f ->

            // ====== #
            // Header #
            // ====== #
            val typeIndicator = readString(f, 4)
            val versionNumber = readString(f, 4)
            val playListStartAddress = readUInt(f)
            val playListMarkStartAddress = readUInt(f)
            val extensionDataStartAddress = readUInt(f)
            f.skipBytes(20) // 160 reserved bits

            header = Header(
                typeIndicator,
                versionNumber,
                playListStartAddress,
                playListMarkStartAddress,
                extensionDataStartAddress
            )

            // =============== #
            // AppInfoPlayList #
            // =============== #
            val appLength = readUInt(f)
            f.skipBytes(1) // 8 reserved bits
            val playbackType = f.readUnsignedByte()
            val playbackCount = if (playbackType == 0x02 || playbackType == 0x03) f.readUnsignedShort() else { f.skipBytes(2); null }
            val uoMaskTable = readULong(f)
            val miscFlags = f.readUnsignedShort()

            appInfoPlayList = AppInfoPlayList(appLength, playbackType, playbackCount, uoMaskTable, miscFlags)

            // ======== #
            // PlayList #
            // ======== #
            f.seek(header.playListStartAddress)
            val playListLength = readUInt(f)
            val startPosition = f.filePointer
            f.skipBytes(2) // 16 reserved bits
            val numberOfPlayItems = f.readUnsignedShort()
            val numberOfSubPaths = f.readUnsignedShort()

            val playItems = mutableListOf<PlayItem>()
            (0 until numberOfPlayItems).forEach { _ ->
                playItems.add(getPlayItem(f))
            }

            val subPaths = mutableListOf<SubPath>()
            (0 until numberOfSubPaths).forEach { _ ->
                subPaths.add(getSubPath(f))
            }

            f.seek(startPosition + playListLength)
            playList = PlayList(playListLength, numberOfPlayItems, numberOfSubPaths, playItems, subPaths)

            // ============ #
            // PlayListMark #
            // ============ #
            f.seek(header.playListMarkStartAddress)
            val markLength = readUInt(f)
            val markStartPosition = f.filePointer
            val numberOfPlayListMarks = f.readUnsignedShort()
            val markList = mutableListOf<PlayListMarkItem>()

            (0 until numberOfPlayListMarks).forEach { _ ->
                f.skipBytes(1) // 8 reserved bits
                val markType = f.readUnsignedByte()
                val refToPlayItemID = f.readUnsignedShort()
                val markTimeStamp = readUInt(f)
                val entryESPID = f.readUnsignedShort()
                val duration = readUInt(f)
                markList.add(PlayListMarkItem(markType, refToPlayItemID, markTimeStamp, entryESPID, duration))
            }
            f.seek(markStartPosition + markLength)
            playListMarks = PlayListMarks(markLength, numberOfPlayListMarks, markList)

            // ============= #
            // ExtensionData #
            // ============= #
            var extLength = 0L
            var dataBlockStartAddress: Long? = null
            var numberOfExtDataEntries: Int? = null
            val extDataEntries = mutableListOf<ExtDataEntry>()

            if (header.extensionDataStartAddress != 0L) {
                f.seek(header.extensionDataStartAddress)
                extLength = readUInt(f)
                val extStartPosition = f.filePointer
                if (extLength > 0) {
                    dataBlockStartAddress = readUInt(f)
                    f.skipBytes(3) // 24 reserved bits
                    numberOfExtDataEntries = f.readUnsignedByte()

                    (0 until numberOfExtDataEntries).forEach { _ ->
                        val extDataType = f.readUnsignedShort()
                        val extDataVersion = f.readUnsignedShort()
                        val extDataStart = readUInt(f)
                        val extDataLen = readUInt(f)
                        extDataEntries.add(ExtDataEntry(extDataType, extDataVersion, extDataStart, extDataLen))
                    }
                }
                f.seek(extStartPosition + extLength)
            }
            extensionData = ExtensionData(extLength, dataBlockStartAddress, numberOfExtDataEntries, extDataEntries)
        }
    }

    private fun getSubPath(f: RandomAccessFile): SubPath {
        val length = readUInt(f)
        val startPosition = f.filePointer
        f.skipBytes(1) // 8 reserved bits
        val subPathType = f.readUnsignedByte()
        f.skipBytes(1) // 8 reserved bits
        val b = f.readUnsignedByte()
        val isRepeatSubPath = (b and 0x01) == 1
        f.skipBytes(1) // 8 reserved bits
        val numberOfSubPlayItems = f.readUnsignedByte()

        val subPlayItems = mutableListOf<SubPlayItem>()
        (0 until numberOfSubPlayItems).forEach { _ ->
            subPlayItems.add(getSubPlayItem(f))
        }

        f.seek(startPosition + length)
        return SubPath(length, subPathType, isRepeatSubPath, numberOfSubPlayItems, subPlayItems)
    }

    private fun getSubPlayItem(f: RandomAccessFile): SubPlayItem {
        val length = f.readUnsignedShort()
        val startPosition = f.filePointer
        val clipInformationFileName = readString(f, 5)
        val clipCodecIdentifier = readString(f, 4)
        f.skipBytes(3) // 24 reserved bits
        val b = f.readUnsignedByte()
        val connectionCondition = b and 0x1E
        val isMultiClipEntries = (b and 0x01) == 1
        val refToSTCID = f.readUnsignedByte()
        val inTime = readUInt(f)
        val outTime = readUInt(f)
        val syncPlayItemID = f.readUnsignedShort()
        val syncStartPTS = readUInt(f)

        var numberOfMultiClipEntries: Int? = null
        val multiClipEntries = mutableListOf<MultiClipEntry>()

        if (isMultiClipEntries) {
            numberOfMultiClipEntries = f.readUnsignedByte()
            f.skipBytes(1) // 8 reserved bits
            (0 until numberOfMultiClipEntries).forEach { _ ->
                val cName = readString(f, 5)
                val cCodec = readString(f, 4) // Python liest hier 5 Bytes im inneren Loop, korrigiert auf 5 falls nötig
                val refSTC = f.readUnsignedByte()
                multiClipEntries.add(MultiClipEntry(cName, cCodec, refSTC))
            }
        }

        f.seek(startPosition + length)
        return SubPlayItem(
            length, clipInformationFileName, clipCodecIdentifier, connectionCondition,
            isMultiClipEntries, refToSTCID, inTime, outTime, syncPlayItemID, syncStartPTS,
            numberOfMultiClipEntries, multiClipEntries
        )
    }

    private fun getPlayItem(f: RandomAccessFile): PlayItem {
        val length = f.readUnsignedShort()
        val startPosition = f.filePointer
        val clipInformationFileName = readString(f, 5)
        val clipCodecIdentifier = readString(f, 4)

        val rawBits = ByteArray(2)
        f.readFully(rawBits)
        val tmp = getBits(rawBits)
        val isMultiAngle = tmp[11] == 1 // Index 11 entspricht dem 12. Element aus Python
        val connectionCondition = tmp.subList(12, 16)

        val refToSTCID = f.readUnsignedByte()
        val inTime = readUInt(f)
        val outTime = readUInt(f)
        val uoMaskTable = readULong(f)

        val singleByte = ByteArray(1)
        f.readFully(singleByte)
        val playItemRandomAccessFlag = getBits(singleByte)[0] == 1

        val stillMode = f.readUnsignedByte()
        val stillTime = if (stillMode == 0x01) f.readUnsignedShort() else { f.skipBytes(2); null }

        if (isMultiAngle) {
            error("IsMultiAngle has not been implemented as the specification is not byte-aligned")
        }

        val stnTable = getSTNTable(f)
        f.seek(startPosition + length)

        return PlayItem(
            length, clipInformationFileName, clipCodecIdentifier, isMultiAngle, connectionCondition,
            refToSTCID, inTime, outTime, uoMaskTable, playItemRandomAccessFlag, stillMode, stillTime, stnTable
        )
    }

    private fun getSTNTable(f: RandomAccessFile): STNTable {
        val length = f.readUnsignedShort()
        val startPosition = f.filePointer
        f.skipBytes(2) // 16 reserved bits

        val counts = IntArray(8)
        for (i in 0 until 8) {
            counts[i] = f.readUnsignedByte()
        }
        f.skipBytes(4) // 32 reserved bits

        val entriesMap = mutableMapOf<Int, List<STNTableEntry>>()
        (0 until 8).forEach { i ->
            val list = mutableListOf<STNTableEntry>()
            (0 until counts[i]).forEach { _ ->
                list.add(STNTableEntry(getStreamEntry(f), getStreamAttributes(f)))
            }
            entriesMap[i] = list
        }

        f.seek(startPosition + length)
        return STNTable(
            length, counts[0], counts[1], counts[2], counts[3], counts[4], counts[5], counts[6], counts[7],
            entriesMap[0] ?: emptyList(), entriesMap[1] ?: emptyList(), entriesMap[2] ?: emptyList(),
            entriesMap[3] ?: emptyList(), entriesMap[4] ?: emptyList(), entriesMap[5] ?: emptyList(),
            entriesMap[6] ?: emptyList(), entriesMap[7] ?: emptyList()
        )
    }

    private fun getStreamEntry(f: RandomAccessFile): StreamEntry {
        val length = f.readUnsignedByte()
        val startPosition = f.filePointer
        var streamType: Int? = null
        var refToStreamPID: String? = null
        var refToSubPathID: Int? = null
        var refToSubClipID: Int? = null

        if (length > 0) {
            streamType = f.readUnsignedByte()
            when (streamType) {
                0x01 -> {
                    val pid = f.readUnsignedShort()
                    refToStreamPID = String.format("0x%04x", pid)
                }
                0x02 -> {
                    refToSubPathID = f.readUnsignedByte()
                    refToSubClipID = f.readUnsignedByte()
                    val pid = f.readUnsignedShort()
                    refToStreamPID = String.format("0x%04x", pid)
                }
                0x03, 0x04 -> {
                    refToSubPathID = f.readUnsignedByte()
                    val pid = f.readUnsignedShort()
                    refToStreamPID = String.format("0x%04x", pid)
                }
            }
        }

        f.seek(startPosition + length)
        return StreamEntry(length, streamType, refToStreamPID, refToSubPathID, refToSubClipID)
    }

    private fun getStreamAttributes(f: RandomAccessFile): StreamAttributes {
        val length = f.readUnsignedByte()
        val startPosition = f.filePointer

        var streamCodingType: Int? = null
        var videoFormat: Int? = null
        var frameRate: Int? = null
        var dynamicRangeType: Int? = null
        var colorSpace: Int? = null
        var crFlag: Boolean? = null
        var hdrPlusFlag: Boolean? = null
        var audioFormat: Int? = null
        var sampleRate: Int? = null
        var languageCode: String? = null
        var characterCode: String? = null

        if (length > 0) {
            streamCodingType = f.readUnsignedByte()

            // Video-Attribute
            if (streamCodingType in listOf(0x01, 0x02, 0x1B, 0xEA, 0x24)) {
                val b = f.readUnsignedByte()
                videoFormat = b shr 4
                frameRate = b and 0x0F
            }

            // Erweiterte Video-Attribute (z.B. HEVC / 4K)
            if (streamCodingType == 0x24) {
                val b1 = f.readUnsignedByte()
                dynamicRangeType = b1 shr 4
                colorSpace = b1 and 0x0F

                val b2 = f.readUnsignedByte()
                crFlag = (b2 and 0x80) != 0
                hdrPlusFlag = (b2 and 0x40) != 0
            }

            // Audio-Attribute
            if (streamCodingType in listOf(0x03, 0x04, 0x80, 0x81, 0x82, 0x83, 0x84, 0x85, 0x86, 0xA1, 0xA2)) {
                val b = f.readUnsignedByte()
                audioFormat = b shr 4
                sampleRate = b and 0x0F
                languageCode = readString(f, 3)
            }

            // Untertitel (PG/IG) / Text-Attribute
            if (streamCodingType in listOf(0x90, 0x91)) {
                languageCode = readString(f, 3)
            }

            if (streamCodingType == 0x92) {
                characterCode = readString(f, 1)
                languageCode = readString(f, 3)
            }
        }

        f.seek(startPosition + length)
        return StreamAttributes(
            length, streamCodingType, videoFormat, frameRate, dynamicRangeType,
            colorSpace, crFlag, hdrPlusFlag, audioFormat, sampleRate, languageCode, characterCode
        )
    }

    // ==========================================
    // Hilfsmethoden für Byte- und Bit-Parsing
    // ==========================================

    private fun readString(f: RandomAccessFile, length: Int): String {
        val bytes = ByteArray(length)
        f.readFully(bytes)
        return String(bytes, StandardCharsets.UTF_8)
    }

    private fun readUInt(f: RandomAccessFile): Long {
        val bytes = ByteArray(4)
        f.readFully(bytes)
        return ByteBuffer.wrap(bytes).int.toLong() and 0xFFFFFFFFL
    }

    private fun readULong(f: RandomAccessFile): Long {
        val bytes = ByteArray(8)
        f.readFully(bytes)
        return ByteBuffer.wrap(bytes).long
    }

    private fun getBits(bytes: ByteArray): List<Int> {
        val bitList = mutableListOf<Int>()
        for (b in bytes) {
            val value = b.toInt()
            // Liest die Bits von Bit 0 bis Bit 7 (entspricht Pythons Logik)
            for (i in 0 until 8) {
                bitList.add((value shr i) and 1)
            }
        }
        return bitList
    }
}
