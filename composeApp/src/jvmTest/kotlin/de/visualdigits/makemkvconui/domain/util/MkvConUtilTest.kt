package de.visualdigits.makemkvconui.domain.util

import co.touchlab.kermit.Logger
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.Data
import io.ktor.util.cio.readChannel
import io.ktor.utils.io.asSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.io.buffered
import kotlinx.io.readLine
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import java.io.File
import kotlin.time.Duration.Companion.seconds

class MkvConUtilTest {

    @DisabledIfEnvironmentVariable(named = "CI", matches = "true", disabledReason = "Needs local optical drive")
    @Test
    fun renameFilesFromDisk() {
        val targetDirectory = File("E:\\Video\\Star Trek\\Strange New Worlds\\Season 3")
        val disc = readDisc()
        disc?.tracks
            ?.sortedBy { it.mplsName }
            ?.forEachIndexed { index, track ->
                val sourceFile = File(targetDirectory, track.outputFileName)
                val targetFileName = File(targetDirectory, "${disc.volumeName}_${index.toString().padStart(3, '0')}_${track.mplsName.substringBeforeLast('.')}_${track.outputFileName}")
                if (sourceFile.exists()) {
                    println("Renaming $sourceFile [$${track.duration}] -> $targetFileName")
                    sourceFile.renameTo(targetFileName)
                }
            }
    }

    @DisabledIfEnvironmentVariable(named = "CI", matches = "true", disabledReason = "Needs local resources")
    @Test
    fun renameFilesFromFile() {
        val targetDirectory = File("\\\\Bluray\\e\\Video\\Star Trek\\Strange New Worlds\\Season 3")
        val disc = readDisc(File("\\\\Bluray\\c\\Users\\sknull\\messages.txt"))
        disc?.tracks
            ?.sortedBy { it.mplsName }
            ?.forEachIndexed { index, track ->
                val sourceFile = File(targetDirectory, track.outputFileName)
                val targetFileName = File(targetDirectory, "${disc.volumeName}_${index.toString().padStart(3, '0')}_${track.mplsName.substringBeforeLast('.')}_${track.outputFileName}")
                if (sourceFile.exists()) {
                    println("Renaming $sourceFile [$${track.duration}] -> $targetFileName")
                    sourceFile.renameTo(targetFileName)
                }
            }
    }

    @Test
    fun testReadMessages() {
        val messages = readMessages(File(ClassLoader.getSystemResource("bluray/report.csv").toURI()))
        println(messages.joinToString("\n"))
    }

    @Test
    fun printDisc() {
        val disc = readDisc(File(ClassLoader.getSystemResource("bluray/makemkv_messages.csv").toURI()))
        println(disc)
    }

    @Test
    fun printProgress() {
        val messages = readRawData(File(ClassLoader.getSystemResource("bluray/progress.csv").toURI()))
        println(messages.joinToString("\n"))
    }
}
