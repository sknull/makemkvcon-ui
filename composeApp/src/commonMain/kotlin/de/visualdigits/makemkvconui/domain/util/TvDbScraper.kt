package de.visualdigits.makemkvconui.domain.util

import co.touchlab.kermit.Logger
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import de.visualdigits.makemkvconui.domain.model.tvdb.CastAndCrew
import de.visualdigits.makemkvconui.domain.model.tvdb.Description
import de.visualdigits.makemkvconui.domain.model.tvdb.Episode
import de.visualdigits.makemkvconui.domain.model.tvdb.I18nDescription
import de.visualdigits.makemkvconui.domain.model.tvdb.Person
import de.visualdigits.makemkvconui.domain.model.tvdb.Season
import de.visualdigits.makemkvconui.domain.model.tvdb.Series
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class TvDbScraper(
    private val httpClient: HttpClient
) {
    companion object {
        private val baseUrl = "https://www.thetvdb.com"

        private val episodesCache: MutableMap<String, Episode> = mutableMapOf()
    }

    suspend fun scrapeSeries(
        seriesId: String
    ): Series {
        Logger.i("Scraping series $seriesId")
        val seriesUrl = "$baseUrl/series/$seriesId"

        return try {
            val htmlSeries = readUrl(seriesUrl)
            val documentSeries = Ksoup.parseBodyFragment(htmlSeries)

            val title = documentSeries.selectFirst("h1[id=series_title]")?.text()?.trim() ?: ""
            val description = scrapeI18nDescription(documentSeries)

            val metaData = scrapeListData(documentSeries, "div[id=general]")
            val castAndCrew = scrapeCastAndCrew(documentSeries)
            val seasons = scrapeSeasons(documentSeries)
                .map { (tag, seasons) ->
                    Pair(tag, seasons.map { season ->
                        Logger.i("Scraping episodes for series $seriesId order type $tag season ${season.season}")
                        try {
                            delay(2.seconds)
                            season.episodesUrl
                                ?.let { readUrl(it) }
                                ?.let { Ksoup.parseBodyFragment(it) }
                                ?.let {
                                    season.copy(episodeList = scrapeSeasonEpisodes(it))
                                }?: season
                        } catch (e: Exception) {
                            Logger.e("Could not scrape episodes for series $seriesId order type $tag season ${season.season}", e)
                            season
                        }
                    })
                }.toMap()

            Series(
                source = "tvdb",
                id = seriesId,
                seriesTitle = title,
                seriesDescription = description,
                metaData = metaData,
                castAndCrew = castAndCrew,
                seasons = seasons
            )
        } catch (e: Exception) {
            Logger.e("Could not scrape series $seriesId", e)
            Series()
        }
    }

    private fun scrapeSeasons(
        document: Document
    ): Map<String, List<Season>> {
        Logger.i("Scraping seasons")
        return document
            .selectFirst("div[id=seasons]")
            ?.select("li")?.associate { li ->
                val a = li.selectFirst("a")
                val tag = a?.attr("href")?.removePrefix("#") ?: ""
                val name = a?.text()?.trim() ?: ""
                Pair(tag, name)
            }
            ?.filter { (tag, _) -> tag.isNotEmpty() }
            ?.mapNotNull { (tag, name) ->
                Pair(tag, document
                    .selectFirst("div[id=$tag]")
                    ?.let { element ->
                        val tableData = scrapeTableData(element)
                        tableData
                            .filter { it["Season"]?.first?.startsWith("season", ignoreCase = true) == true }
                            .map { map ->
                                Season(
                                    orderType = name,
                                    season = map["Season"]?.first?.drop(7)?.substringBefore(' ')?.toInt() ?: 0,
                                    from = map["From"]?.first ?: "",
                                    to = map["To"]?.first ?: "",
                                    episodes = map["Episodes"]?.first?.toInt() ?: 0,
                                    episodesUrl = map["Season"]?.second?.let { makeUrlAbsolute(baseUrl, it) }
                                )
                            }
                    }
                ?: listOf())
            }
            ?.toMap()
            ?: mapOf()
    }

    private fun scrapeCastAndCrew(
        document: Document
    ): CastAndCrew {
        Logger.i("Scraping cast and crew")
        val actors = document
            .selectFirst("div[id=people-actor]")
            ?.select("div[class=thumbnail]")
            ?.map { li ->
                val text = li.selectFirst("small")?.text()?.trim() ?: ""
                val role = text.replace("as ", "")
                Person(
                    name = li.select("h3").text().trim().replace(text, "").trim(),
                    playsAs = if(role.isNotBlank()) role else null,
                    thumbUrl = li.selectFirst("img")?.attr("data-src")
                )
            }
            ?: listOf()
        val directors = scrapePeopleTable(document, "director")
        val writers = scrapePeopleTable(document, "writer")

        val castAndCrew = CastAndCrew(
            actors = actors,
            directors = directors,
            writers = writers
        )
        return castAndCrew
    }

    private fun scrapePeopleTable(
        document: Document,
        id: String
    ): List<Person> {
        Logger.i("Scraping people-$id")
        return document
            .selectFirst("div[id=people-$id]")
            ?.let { element ->
                val tableData = scrapeTableData(element)
                tableData.map { map ->
                    Person(
                        name = map["Name"]?.first ?: "",
                        numberOfEpisodes = map["Number of Episodes"]?.first?.toInt(),
                        dates = map["Dates"]?.first
                    )
                }
            }
            ?: listOf()
    }

    private suspend fun scrapeSeasonEpisodes(
        document: Document,
    ): List<Episode> {
        return document
            .selectFirst("div[id=episodes]")
            ?.let { element ->
                val tableData = scrapeTableData(element)
                tableData
                    .filter { it[""]?.first?.isNotEmpty() == true }
                    .map { map ->
                        scrapeEpisode(map)
                    }
            }
            ?: listOf()
    }

    private suspend fun scrapeEpisode(map: Map<String, Pair<String, String?>>): Episode {
        Logger.i("Scraping episode ${map[""]?.first}")
        val episodeUrl = "$baseUrl${map["Name"]?.second}"

        val episodeId = episodeUrl.substringAfterLast('/')
        var episode = episodesCache[episodeId]
        if (episode != null) return episode

        delay(1.seconds)
        val htmlEpisode = readUrl(episodeUrl)
        val documentEpisode = Ksoup.parseBodyFragment(htmlEpisode)
        val selectFirst = documentEpisode.selectFirst("div[id=castcrew]")
        val persons = selectFirst
            ?.let { element ->
                val episodeTableData = scrapeTableData(element)
                episodeTableData
                    .filter { it.isNotEmpty() }
                    .map { map2 ->
//                        val personUrl = "$baseUrl${map["Name"]?.second}"
//                        delay(500.milliseconds)
//                        val htmlPerson = readUrl(personUrl)
//                        val documentPerson = Ksoup.parseBodyFragment(htmlPerson)
//                        val thumbUrl = documentPerson
//                            .selectFirst("div[id=container]")
//                            ?.selectFirst("img")
//                            ?.attr("src")
                        val playsAs = map2["Role"]?.first
                        Person(
                            name = map2["Name"]?.first ?: "",
                            type = map2["Type"]?.first,
                            playsAs = if (playsAs?.isNotBlank() == true) playsAs else null,
//                            thumbUrl = thumbUrl
                        )
                    }

            } ?: listOf()

        episode = Episode(
            source = "tvdb",
            id = episodeId,
            code = map[""]?.first ?: "",
            name = map["Name"]?.first ?: "",
            firstAired = map["First Aired"]?.first ?: "",
            runtime = map["Runtime"]?.first?.toInt() ?: 0,
            episodeDescription = scrapeI18nDescription(documentEpisode),
            castAndCrew = CastAndCrew(
                actors = persons.filter { it.type != "Writer" && it.type != "Director" },
                directors = persons.filter { it.type == "Director" },
                writers = persons.filter { it.type == "Writer" }
            )
        )
        episodesCache[episodeId] = episode

        return episode
    }

    private fun scrapeI18nDescription(document: Document): I18nDescription {
        return document
            .selectFirst("div[id=translations]")
            ?.let { element ->
                val translations = element
                    .select("div[class=change_translation_text]")
                    .associate { translationDiv ->
                        val language = translationDiv.attr("data-language")
                        val text = translationDiv.selectFirst("p")?.text()?.trim() ?: ""
                        val aliases = translationDiv
                            .selectFirst("ul")
                            ?.select("li")
                            ?.map { it.text().trim() }
                            ?: listOf()
                        Pair(language, Description(text, aliases))
                    }

                I18nDescription(
                    translations = translations,
                )
            } ?: I18nDescription()
    }

    private fun scrapeListData(
        document: Document,
        rootElement: String
    ): Map<String, String> {
        return document
            .selectFirst(rootElement)
            ?.select("li")
            ?.associate { li ->
                Pair(
                    li.select("strong").firstOrNull()?.text()?.trim() ?: "",
                    li.select("span").text().trim()
                )
            }
            ?: mapOf()
    }

    private fun scrapeTableData(
        element: Element
    ): List<Map<String, Pair<String, String?>>> {

        val table = element
            .selectFirst("table")

        if (table == null) return listOf()

        val rows = table.select("tr")
        val keys = rows.firstOrNull()
            ?.select("th")
            ?.map { it.text().trim() }
            ?: listOf()
        Pair(keys, rows.drop(1))

        return rows
            .map { row ->
                val rowData = row
                    .select("td")
                    .map {
                        Pair(
                            it.text().trim(),
                            it.selectFirst("a")?.attr("href")
                        )
                    }
                keys.zip(rowData).toMap()
            }
    }

    /**
     * Helper method to read text from an url using ok http client.
     *
     * @param httpClient The kator http client to use for scraping.
     */
    private suspend fun readUrl(
        url: String
    ): String {
        return try {
            val response = httpClient
                .get(urlString = url)
            response.bodyAsText()
        } catch (e: Exception) {
            Logger.e("Could not read from url '$url' [${e.message}]")
            ""
        }
    }

    /**
     * Helper method to read raw bytes from an url using kator http client.
     *
     * @param httpClient The OkHttpClient to use for scraping.
     */
    private suspend fun readUrlAsRawBytes(
        url: String
    ): ByteArray? {
        return try {
            val response = httpClient
                .get(urlString = url)
            if (response.status.value == 200) {
                Logger.i("read was successful")
                response.readRawBytes()
            } else {
                error("Could not read from url '$url', response was [${response.status.value}] ${response.status.description}")
            }
        } catch (e: Exception) {
            Logger.e("Could not read from url '$url' [${e.message}]", e)
            null
        }
    }
}
