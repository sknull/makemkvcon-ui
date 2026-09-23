package de.visualdigits.makemkvconui.domain.util

import org.junit.jupiter.api.Test
import java.io.File

class MPLSTest {

    @Test
    fun testMpls() {
//        val rootDirectory = "E:\\Programmierung\\IntelliJ\\KotlinScratch\\src\\test\\resources\\mpls"
//        val tracks = File(rootDirectory)
//            .listFiles { it.name.endsWith(".mpls", ignoreCase = true) }
//            ?.map { file ->
//                val trackNrs = MPLS(file).playList.playItems.map { it.clipInformationFileName.toInt() }.toSet()
//                Pair(trackNrs, file.nameWithoutExtension.toInt())
//            }
////            ?.sortedBy { it.first }
//            ?: listOf()
//
//        println(tracks.joinToString("\n"))

        val file = File(ClassLoader.getSystemResource("bluray/mpls/00230.mpls").toURI())
        val mpls = MPLS(file)

        println(mpls)

    }
}
