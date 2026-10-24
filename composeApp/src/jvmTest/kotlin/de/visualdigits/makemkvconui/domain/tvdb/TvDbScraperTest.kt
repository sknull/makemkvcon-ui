package de.visualdigits.makemkvconui.domain.tvdb

import de.visualdigits.makemkvconui.di.platformModule
import de.visualdigits.makemkvconui.di.sharedModule
import de.visualdigits.makemkvconui.domain.model.tvdb.Series
import de.visualdigits.makemkvconui.domain.util.TvDbScraper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.io.File

class TvDbScraperTest : KoinTest {

    private val tvDbScraper: TvDbScraper by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(sharedModule, platformModule)
    }

    @Test
    fun testScrape() {
        runBlocking {
            val targetDirectory = File("./src/commonMain/composeResources/files").canonicalPath

            val seriesList = listOf(
//                "star-trek",
//                "star-trek-continues",
//                "star-trek-the-next-generation",
//                "star-trek-deep-space-nine",
                "star-trek-voyager",
//                "star-trek-enterprise",
//                "star-trek-discovery",
//                "star-trek-picard",
//                "star-trek-strange-new-worlds",

//                "stargate-sg-1",
//                "stargate-atlantis",
//                "stargate-universe",
//
//                "star-wars-clone-wars",
//                "star-wars-rebels",

//                "miami-vice"
            )
            seriesList.forEach { seriesId ->
                val series = tvDbScraper.scrapeSeries(seriesId)
                println(series)
//                series.encodeToFile(File(targetDirectory, "$seriesId.json"))
            }
        }
    }

    @Test
    fun testDecode() {
        Series.writeNfoFiles(
            seriesJsonFile = File("./src/commonMain/composeResources/files/star-trek-voyager.json"),
            rootDirectory = "V:\\Videos\\Science Fiction\\Star Trek\\1995_Star Trek Voyager"
        )
    }
}
