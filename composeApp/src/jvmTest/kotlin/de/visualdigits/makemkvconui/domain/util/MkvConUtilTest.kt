package de.visualdigits.makemkvconui.domain.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import java.io.File

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
        val lines = File("\\\\Bluray\\c\\Users\\sknull\\messages.txt").readLines()
        val disc = readDisc(lines)
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
    fun printDisc() {
        val lines = File("\\\\Bluray\\c\\Users\\sknull\\messages.txt").readLines()
        val disc = readDisc(lines)
        println(disc)
    }

    @DisabledIfEnvironmentVariable(named = "CI", matches = "true", disabledReason = "Needs local resources")
    @Test
    fun printProgress() {
        val lines = File(ClassLoader.getSystemResource("bluray/progress.csv").toURI()).readLines()
        val messages = readProgress(lines)
        println(messages.joinToString("\n"))
    }
}
