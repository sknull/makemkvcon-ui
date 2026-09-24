package de.visualdigits.makemkvconui.domain.util

import de.visualdigits.makemkvconui.domain.model.bluray.info.common.TypeIndicator
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.Data
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.DiscData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.StreamData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.TrackCountData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.TrackData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.drive.DriveData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.drive.DriveFlags
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Disc
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Entity
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Stream
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Track
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields.CInfoField
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields.SInfoField
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields.TInfoField
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.message.MessageData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.message.MessageSeverity
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.message.MessageType
import de.visualdigits.makemkvconui.domain.model.bluray.progress.ProgressCode
import de.visualdigits.makemkvconui.domain.model.bluray.progress.ProgressCurrentTitle
import de.visualdigits.makemkvconui.domain.model.bluray.progress.ProgressData
import de.visualdigits.makemkvconui.domain.model.bluray.progress.ProgressTotalTitle
import de.visualdigits.makemkvconui.domain.model.bluray.progress.ProgressValue
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.reflect.KClass


/**
 * Reads the disc information from the given disc index.
 * The returned disc conatins a list of tracks, wher each
 * track contains a list of streams.
 */
fun readDisc(discIndex: Int = 0): Disc? {
    return readDisc(readData(discIndex))
}

/**
 * Reads the disc information from the given file.
 * The returned disc conatins a list of tracks, wher each
 * track contains a list of streams.
 */
fun readDisc(file: File): Disc? {
    return readDisc(file.readLines())
}

/**
 * Reads the disc information from the given list of lines.
 * The returned disc conatins a list of tracks, wher each
 * track contains a list of streams.
 */
fun readDisc(lines: List<String>): Disc? {
    return readDisc(readData(lines))
}

fun readRawData(file: File): List<Data> {
    return file.readLines().mapNotNull { line ->
        readRawDataLine(line)
    }
}

/**
 * Extracts the disc information from the given data and assembles it with
 * the found track and stream information.
 */
@Suppress("Unchecked_Cast")
private fun readDisc(data: Map<KClass<out Data>, Entity>): Disc? {
    val disc = (data[DiscData::class] as? Disc)?.copy(
        tracks = (data[TrackData::class] as? Map<Int, Track>)?.values
            ?.toList()
            ?.map { track ->
                track.copy(
                    streams = (data[StreamData::class] as? Map<Int, List<Stream>>)?.get(track.titleId) ?: listOf()
                )
            }
            ?: listOf()
    )

    return disc
}

/**
 * Reads from the given disc index using makemkvcon and
 * converts the raw data lines into a list useful data objects representing the domain objects
 * Disc, Track, Stream, Drive, TrackCount and Message
 */
private fun readData(discIndex: Int = 0): Map<KClass<out Data>, Entity> {
    val process = ProcessBuilder("makemkvcon64", "-r", "info", "disc:$discIndex").start()
    val lines = BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
        reader.readLines()
    }
    process.waitFor()

    return readData(lines)
}

/**
 * Converts the raw data lines into a list useful data objects representing the domain objects
 * Disc, Track, Stream, Drive, TrackCount and Message
 */
@Suppress("Unchecked_Cast")
private fun readData(lines: List<String>): Map<KClass<out Data>, Entity> {
    val data = lines
        .mapNotNull { line -> readRawDataLine(line) }
        .groupBy { it::class }
        .map { (clazz, entries) ->
            when (clazz) {
                DiscData::class -> {
                    val fieldMap = (entries as List<DiscData>).associateBy { it.field }.toMap()
                    Pair(
                        clazz, Disc(
                            mediaTypeName = fieldMap[CInfoField.MediaTypeName]?.value ?: "",
                            titleName = fieldMap[CInfoField.TitleName]?.value ?: "",
                            languageCode = fieldMap[CInfoField.LanguageCode]?.value ?: "",
                            languageName = fieldMap[CInfoField.LanguageName]?.value ?: "",
                            information = fieldMap[CInfoField.Information]?.value ?: "",
                            htmlHeader = fieldMap[CInfoField.HtmlHeader]?.value ?: "",
                            volumeName = fieldMap[CInfoField.VolumeName]?.value ?: "",
                            volumeId = fieldMap[CInfoField.VolumeId]?.value?.toInt() ?: -1
                        )
                    )
                }

                TrackData::class -> Pair(
                    clazz, entries
                    .groupBy { (it as TrackData).titleId }
                    .map { (titleId, entries) ->
                        val fieldMap = (entries as List<TrackData>).associateBy { it.field }.toMap()
                        Pair(
                            titleId, Track(
                                titleId = fieldMap.values.first().titleId,
                            titleName = fieldMap[TInfoField.TitleName]?.value ?: "",
                            chaptersCount = fieldMap[TInfoField.ChaptersCount]?.value?.toInt() ?: 0,
                            duration = fieldMap[TInfoField.Duration]?.value ?: "",
                            sizeStr = fieldMap[TInfoField.SizeStr]?.value ?: "",
                            sizeLong = fieldMap[TInfoField.SizeLong]?.value?.toLong() ?: 0,
                            mplsName = fieldMap[TInfoField.MplsName]?.value ?: "",
                            segmentCount = fieldMap[TInfoField.SegmentCount]?.value?.toInt() ?: 0,
                            segmentMap = fieldMap[TInfoField.SegmentMap]?.value?.split(",")?.map { it.toInt() }
                                ?: listOf(),
                            outputFileName = fieldMap[TInfoField.OutputFileName]?.value ?: "",
                            languageCode = fieldMap[TInfoField.LanguageCode]?.value ?: "",
                            languageName = fieldMap[TInfoField.LanguageName]?.value ?: "",
                            information = fieldMap[TInfoField.Information]?.value ?: "",
                            htmlHeader = fieldMap[TInfoField.HtmlHeader]?.value ?: ""
                        ))
                    }.toMap()
                )

                StreamData::class -> Pair(
                    clazz, entries
                    .groupBy { (it as StreamData).titleId }
                    .map { (titleId, entries) ->
                        Pair(
                            titleId, entries
                                .groupBy { (it as StreamData).streamId }
                                .map { (titleId, entries) ->
                                    val fieldMap = (entries as List<StreamData>).associateBy { it.field }.toMap()
                                    Pair(
                                        titleId, Stream(
                                            streamId = fieldMap.values.first().streamId,
                                            streamType = fieldMap[SInfoField.StreamType]?.value ?: "",
                                            codecId = fieldMap[SInfoField.CodecId]?.value ?: "",
                                            codecShort = fieldMap[SInfoField.CodecShort]?.value ?: "",
                                            codecLong = fieldMap[SInfoField.CodecLong]?.value ?: "",
                                            videoResolution = fieldMap[SInfoField.VideoResolution]?.value ?: "",
                                            videoAspect = fieldMap[SInfoField.VideoAspect]?.value ?: "",
                                            videoFrameRate = fieldMap[SInfoField.VideoFrameRate]?.value ?: "",
                                            bitDepth = fieldMap[SInfoField.BitDepth]?.value?.toInt() ?: 0,
                                            languageCode = fieldMap[SInfoField.LanguageCode]?.value ?: "",
                                            languageName = fieldMap[SInfoField.LanguageName]?.value ?: "",
                                            streamInfo = fieldMap[SInfoField.StreamInfo]?.value ?: "",
                                            htmlHeader = fieldMap[SInfoField.HtmlHeader]?.value ?: "",
                                            streamFlags = fieldMap[SInfoField.StreamFlags]?.value?.toInt() ?: 0,
                                            metadataKey = fieldMap[SInfoField.MetadataKey]?.value ?: "",
                                            conversionType = fieldMap[SInfoField.ConversionType]?.value ?: ""
                                        )
                                    )
                                }
                        )
                    }.toMap()
                )

                else -> Pair(clazz, entries)
            }
        }.toMap()
    return data as Map<KClass<out Data>, Entity>
}

/**
 * The core parser which converts the given raw mkvcon message line into a data object.
 */
fun readRawDataLine(line: String): Data? {
    val allParts = splitLine(line)
    return when (allParts[0]) {
        //
        // info command
        //
        "TINFO" -> TrackData(
            titleId = allParts[1].toInt(),
            field = TInfoField.fromId(allParts[2].toInt()),
            typeIndicator = TypeIndicator.fromId(allParts[3].toInt()),
            value = allParts[4]
        )

        "SINFO" -> StreamData(
            titleId = allParts[1].toInt(),
            streamId = allParts[2].toInt(),
            field = SInfoField.fromId(allParts[3].toInt()),
            typeIndicator = TypeIndicator.fromId(allParts[4].toInt()),
            value = allParts[5]
        )

        "CINFO" -> DiscData(
            field = CInfoField.fromId(allParts[1].toInt()),
            typeIndicator = TypeIndicator.fromId(allParts[2].toInt()),
            value = allParts[3]
        )

        else -> null
    }
}

fun readTrackCountDataLine(line: String): TrackCountData? {
    val allParts = splitLine(line)
    return if (allParts[0] == "TCOUNT") TrackCountData(
        trackCount = allParts[1].toInt()
    )
    else null
}

fun readDriveDataLine(line: String): DriveData? {
    val allParts = splitLine(line)
    return if (allParts[0] == "DRV") DriveData(
        index = allParts[1].toInt(),
        visible = allParts[2].toInt(),
        enabled = allParts[3].toInt(),
        flags = DriveFlags.fromId(allParts[4].toInt()),
        driveName = allParts[5],
        discName = allParts[6],
        devicePath = allParts[7]
    )
    else null
}

fun readMessages(file: File): List<MessageData> {
    return file.readLines()
        .map { splitLine(it)}
        .filter { it.first() == "MSG" }
        .map { allParts ->
            MessageData(
                messageType = MessageType.fromId(allParts[1].toInt()),
                flags = MessageSeverity.fromId(allParts[2].toInt()),
                argsCount = allParts[3].toInt(),
                localizedText = allParts[4],
                formatString = allParts[5],
                args = allParts.drop(6)
            )
        }
}

fun readMessageDataLine(line: String): MessageData? {
    val allParts = splitLine(line)
    return if (allParts[0] == "MSG") MessageData(
        messageType = MessageType.fromId(allParts[1].toInt()),
        flags = MessageSeverity.fromId(allParts[2].toInt()),
        argsCount = allParts[3].toInt(),
        localizedText = allParts[4],
        formatString = allParts[5],
        args = allParts.drop(6)
    )
    else null
}

fun readProgressValueDataLine(line: String): ProgressValue? {
    val allParts = splitLine(line)
    return if (allParts[0] == "PRGV") {
        ProgressValue(
            currentVal = allParts[1].toInt(),
            totalVal = allParts[2].toInt(),
            maxVal = allParts[3].toInt(),
        )
    } else null
}

fun readProgressTotalTitleDataLine(line: String): ProgressTotalTitle? {
    val allParts = splitLine(line)
    return if (allParts[0] == "PRGT") {
        ProgressTotalTitle(
            code = ProgressCode.fromId(allParts[1].toInt()),
            typeIndicator = TypeIndicator.fromId(allParts[2].toInt()),
            value = allParts[3]
        )
    } else null
}

fun readProgressCurrentTitleDataLine(line: String): ProgressCurrentTitle? {
    val allParts = splitLine(line)
    return if (allParts[0] == "PRGC") {
        ProgressCurrentTitle(
            code = ProgressCode.fromId(allParts[1].toInt()),
            typeIndicator = TypeIndicator.fromId(allParts[2].toInt()),
            value = allParts[3]
        )
    } else null
}

private fun splitLine(line: String): List<String> {
    val parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
    val allParts = (parts[0].split(":") + parts.drop(1)).map { it.removeSurrounding("\"") }
    return allParts
}

