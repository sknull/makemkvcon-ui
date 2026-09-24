package de.visualdigits.makemkvconui.domain.util

import de.visualdigits.makemkvconui.domain.model.bluray.info.data.Data
import de.visualdigits.makemkvconui.domain.util.DataParser.parseData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.DiscData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.StreamData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.TrackData
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Disc
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Stream
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Track
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields.CInfoField
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields.SInfoField
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields.TInfoField
import java.io.BufferedReader
import java.io.InputStreamReader


fun readDisc(discIndex: Int = 0): Disc? {
    val process = ProcessBuilder("makemkvcon64", "-r", "info", "disc:$discIndex").start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))

    val lines = reader.readLines()
    process.waitFor()

    return readDisc(lines)
}

@Suppress("Unchecked_Cast")
fun readDisc(lines: List<String>): Disc? {
    var data = lines.map { line ->
        val parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
        parseData((parts[0].split(":") + parts.drop(1)).map { it.removeSurrounding("\"") })
    }
    val grouped = data
        .groupBy { it::class }
        .map { (clazz, entries) ->
            when (clazz) {
                DiscData::class -> {
                    val fieldMap = (entries as List<DiscData>).associateBy { it.field }.toMap()
                    Pair(clazz, Disc(
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
                TrackData::class -> Pair(clazz, entries
                    .groupBy { (it as TrackData).titleId }
                    .map { (titleId, entries) ->
                        val fieldMap = (entries as List<TrackData>).associateBy { it.field }.toMap()
                        Pair(titleId, Track(
                            titleId = fieldMap.values.first().titleId,
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
                            htmlHeader = fieldMap[TInfoField.HtmlHeader]?.value ?: ""
                        ))
                    }.toMap()
                )
                StreamData::class -> Pair(clazz, entries
                    .groupBy { (it as StreamData).titleId }
                    .map { (titleId, entries) -> Pair(titleId, entries
                        .groupBy { (it as StreamData).streamId }
                        .map { (titleId, entries) ->
                            val fieldMap = (entries as List<StreamData>).associateBy { it.field }.toMap()
                            Pair(titleId, Stream(
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
                            ))
                        }
                    ) }.toMap()
                )
                else -> Pair(clazz, entries)
            }
        }.toMap()

    val disc = (grouped[DiscData::class] as? Disc)?.copy(
        tracks = (grouped[TrackData::class] as? Map<Int, Track>)?.values
            ?.toList()
            ?.map { track ->
                track.copy(
                    streams = (grouped[StreamData::class] as? Map<Int, List<Stream>>)?.get(track.titleId) ?: listOf()
                )
            }
            ?: listOf()
    )

    return disc
}

fun readProgress(lines: List<String>): List<Data> {
    return lines.map { line ->
        val parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
        parseData((parts[0].split(":") + parts.drop(1)).map { it.removeSurrounding("\"") })
    }
}
