package de.visualdigits.makemkvconui.domain.model.tvdb

import de.visualdigits.makemkvconui.domain.model.kodi.Actor
import de.visualdigits.makemkvconui.domain.model.kodi.EpisodeDetails
import de.visualdigits.makemkvconui.domain.model.kodi.UniqueId
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths

@Serializable
data class Series(
    val source: String = "",
    val id: String = "",
    val seriesTitle: String = "",
    val seriesDescription: I18nDescription = I18nDescription(),
    val metaData: Map<String, String> = mapOf(),
    val castAndCrew: CastAndCrew = CastAndCrew(),
    val seasons: Map<String, List<Season>> = mapOf()
) {
    companion object {
        val jsonMapper = Json {
            prettyPrint = true
            explicitNulls = false
        }

        private val invalidChars = listOf('\\', '/', ':', '*', '"', '?', '<', '>', '|')

        private fun String.replaceInvalidChars(): String {
            return this.map { if (invalidChars.contains(it)) " " else it }.joinToString("")
        }

        fun decodeFromFile(file: File): Series {
            return jsonMapper.decodeFromString<Series>(file.readText())
        }

        /**
         * Writes kodi compatible nfo files for all episodes within the given [seriesJsonFile] file.
         * The expected folder structure is <[rootDirectory]>/Sesaon <n>/Episoden/<seriesName>_S<season>_E<episode>_<episodeName>.
         * Afterward a little finetuning of names may be necessary due to different handling of multipart episodes.
         */
        fun writeNfoFiles(seriesJsonFile: File, rootDirectory: String) {
            val series = Series.decodeFromFile(seriesJsonFile)
            series.seasons["seasons-alternate"]?.forEach { season ->
                season.episodeList.forEachIndexed { index, episode ->
                    val seasonNumber = season.season.toString()
                    val episodeNumber = (index + 1).toString()
                    val targetFile = Paths.get(
                        rootDirectory,
                        "Season $seasonNumber",
                        "Episoden",
                        "Star Trek Yoyager_S${seasonNumber.padStart(2, '0')}_E${
                            episodeNumber.padStart(
                                2,
                                '0'
                            )
                        }_${episode.name.replaceInvalidChars()}.nfo"
                    ).toFile()
                    val episodeDetails = EpisodeDetails(
                        title = episode.name,
                        showTitle = series.seriesTitle,
                        season = season.season.toString(),
                        episode = episodeNumber,
                        plot = episode.episodeDescription.translations["eng"]?.text ?: "",
                        uniqueId = UniqueId(type = episode.source, id = episode.id),
                        runTime = episode.runtime.toString(),
                        director = episode.castAndCrew.directors.joinToString(", ") { it.name },
                        actors = episode.castAndCrew.actors.map {
                            Actor(
                                name = it.name,
                                role = it.playsAs,
                                thumb = it.thumbUrl
                            )
                        } + episode.castAndCrew.writers.map {
                            Actor(
                                name = it.name,
                                role = "Writer",
                                thumb = it.thumbUrl
                            )
                        }
                    )
                    println(targetFile)
                    targetFile.writeText(episodeDetails.toString())
                }
            }
        }
    }

    override fun toString(): String = encodeToString()

    fun encodeToString(): String {
        return jsonMapper.encodeToString(this)
    }

    fun encodeToFile(file: File) {
        file.writeText(jsonMapper.encodeToString(this))
    }
}
