package de.visualdigits.makemkvconui.domain.util

import de.visualdigits.makemkvconui.domain.util.DataParser.parseData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.DiscData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.StreamData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.TrackData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Disc
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Entity
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Stream
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Track
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields.CInfoField
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields.SInfoField
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields.TInfoField
import java.io.BufferedReader
import java.io.InputStreamReader


fun readMessages(discIndex: Int = 0): List<Entity> {
    val process = ProcessBuilder("makemkvcon64", "-r", "info", "disc:$discIndex").start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))

    val lines = reader.readLines()
    process.waitFor()

    return readMessages(lines)
}

fun readMessages(lines: List<String>): List<Entity> {
    var data = lines.map { line ->
        val parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
        parseData((parts[0].split(":") + parts.drop(1)).map { it.removeSurrounding("\"") })
    }
    val entries = mutableListOf<Entity>()
    while (data.isNotEmpty()) {
        val currentData = data.first()
        data = data.drop(1)
        when (currentData) {
            is DiscData -> {
                var currentId = currentData.field.id
                val fieldMap = mutableMapOf(currentData.field to currentData)
                while (((data.first() as? DiscData)?.field?.id ?: -1) > currentId) {
                    val nextData = data.first() as DiscData
                    fieldMap[nextData.field] = nextData
                    data = data.drop(1)
                }
                entries.add(Disc(
                    mediaTypeName = fieldMap[CInfoField.MediaTypeName]?.value ?: "",
                    titleName = fieldMap[CInfoField.TitleName]?.value ?: "",
                    languageCode = fieldMap[CInfoField.LanguageCode]?.value ?: "",
                    languageName = fieldMap[CInfoField.LanguageName]?.value ?: "",
                    information = fieldMap[CInfoField.Information]?.value ?: "",
                    htmlHeader = fieldMap[CInfoField.HtmlHeader]?.value ?: "",
                    volumeName = fieldMap[CInfoField.VolumeName]?.value ?: "",
                    volumeId = fieldMap[CInfoField.VolumeId]?.value?.toInt() ?: -1
                ))
            }
            is TrackData -> {
                val firstId = currentData.field.id
                val fieldMap = mutableMapOf(currentData.field to currentData)
                while (((data.first() as? TrackData)?.field?.id ?: -1) > firstId) {
                    val nextData = data.first() as TrackData
                    fieldMap[nextData.field] = nextData
                    data = data.drop(1)
                }
                entries.add(Track(
                    titleName = fieldMap[TInfoField.TitleName]?.value ?: "",
                    chaptersCount = fieldMap[TInfoField.ChaptersCount]?.value?.toInt() ?: 0,
                    duration = fieldMap[TInfoField.Duration]?.value ?: "",
                    sizeStr = fieldMap[TInfoField.SizeStr]?.value ?: "",
                    sizeLong = fieldMap[TInfoField.SizeLong]?.value?.toLong() ?: 0,
                    mplsName = fieldMap[TInfoField.MplsName]?.value ?: "",
                    segmentCount = fieldMap[TInfoField.SegmentCount]?.value?.toInt() ?: 0,
                    segmentMap = fieldMap[TInfoField.SegmentMap]?.value?.split(",")?.map { it.toInt() } ?: listOf(),
                    outputFileName = fieldMap[TInfoField.OutputFileName]?.value ?: "",
                    languageCode = fieldMap[TInfoField.LanguageCode]?.value ?: "",
                    languageName = fieldMap[TInfoField.LanguageName]?.value ?: "",
                    information = fieldMap[TInfoField.Information]?.value ?: "",
                    htmlHeader = fieldMap[TInfoField.HtmlHeader]?.value ?: "",
                    titleId = fieldMap[TInfoField.TitleId]?.value ?: ""
                ))
            }
            is StreamData -> {
                val firstId = currentData.field.id
                val fieldMap = mutableMapOf(currentData.field to currentData)
                while (data.isNotEmpty() && ((data.first() as? StreamData)?.field?.id ?: -1) > firstId) {
                    val nextData = data.first() as StreamData
                    fieldMap[nextData.field] = nextData
                    data = data.drop(1)
                }
                entries.add(Stream(
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
                ))
            }
            else -> entries.add(currentData as Entity)
        }
    }

    return entries
}
