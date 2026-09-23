package de.visualdigits.makemkvconui.domain.util

import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Disc
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity.Track
import de.visualdigits.makemkvconui.domain.model.bluray.info.data.message.MessageData
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import java.io.File

class MkvConUtilTest {

    @DisabledIfEnvironmentVariable(named = "CI", matches = "true", disabledReason = "Needs local optical drive")
    @Test
    fun renameFilesFromDisk() {
        val targetDirectory = File("E:\\Video\\Star Trek\\Strange New Worlds\\Season 2")
        val messages = readMessages()
        val disc = messages.filterIsInstance<Disc>().firstOrNull()
        messages.filterIsInstance<Track>()
            .sortedBy { it.mplsName }
            .forEachIndexed { index, track ->
                val sourceFile = File(targetDirectory, track.outputFileName)
                val targetFileName = File(targetDirectory, "${disc?.volumeName}_${index.toString().padStart(3, '0')}_${track.mplsName.substringBeforeLast('.')}_${track.outputFileName}")
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
        val lines = File("\\\\Bluray\\c\\Users\\sknull\\messages.txt").readLines()
        val messages = readMessages(lines)
        val disc = messages.filterIsInstance<Disc>().firstOrNull()
        messages.filterIsInstance<Track>()
            .sortedBy { it.mplsName }
            .forEachIndexed { index, track ->
                val sourceFile = File(targetDirectory, track.outputFileName)
                val targetFileName = File(targetDirectory, "${disc?.volumeName}_${index.toString().padStart(3, '0')}_${track.mplsName.substringBeforeLast('.')}_${track.outputFileName}")
                if (sourceFile.exists()) {
                    println("Renaming $sourceFile [$${track.duration}] -> $targetFileName")
                    sourceFile.renameTo(targetFileName)
                }
            }
    }

    @Test
    fun printMessages() {
        val lines = File(ClassLoader.getSystemResource("bluray/makemkv_messages.csv").toURI()).readLines()
        val data = readMessages(lines)
//        val disc = data.filterIsInstance<Disc>().firstOrNull()
//        println("${disc?.volumeName}.")

//        val drives = data.filterIsInstance<DriveData>().filter { it.driveName.isNotEmpty() }
//        println(drives)

        val messages = data.filterIsInstance<MessageData>()
        println(messages.joinToString("\n"))
    }

    @DisabledIfEnvironmentVariable(named = "CI", matches = "true", disabledReason = "Needs local resources")
    @Test
    fun printProgress() {
        val lines = File("\\\\Bluray\\c\\Users\\sknull\\messages.txt").readLines()
        val messages = readMessages(lines)
        println(messages.joinToString("\n"))
    }
}
