package de.visualdigits.makemkvconui.domain.util

import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private const val KILOMETERS_PER_HOUR = 1.852

/**
 * Formats this distanz in meters in human-readable form (i.e. "350 m" or "4.2 km").
 */
fun Double.formatDistance(): String {
    return if (this < 1000.0) {
        "${this.roundToInt()} m"
    } else {
        val km = this / 1000.0
        val roundedKm = (km * 10).roundToInt() / 10.0
        "$roundedKm km"
    }
}

private val P_DOUBLE = "\\d+(?:\\.\\d+)?".toRegex()

fun String.parseDistance(): Double {
    val s = this.trim().lowercase()
    val match = P_DOUBLE.find(s)
    val value = match?.value?.toDouble() ?: 0.0
    return if (s.endsWith("km")) {
        value * 1000.0
    } else {
        value
    }
}

/**
 * Formats this distanz in meters in human-readable form (i.e. "350 m" or "4.2 km").
 */
fun Double.toKmh(): Double {
    val kmh = this  * KILOMETERS_PER_HOUR
    return (kmh * 10).roundToInt() / 10.0
}

fun Duration.formatTime(): String {
    var duration = this
    val days = this.inWholeDays
    duration -= days.days
    val hours = duration.inWholeHours
    duration -= hours.hours
    val minutes = duration.inWholeMinutes
    duration -= minutes.minutes
    val seconds = duration.inWholeSeconds

    val sdays = days.toString()
    val shours = hours.toString().padStart(2, '0')
    val sminutes = minutes.toString().padStart(2, '0')
    val sseconds = seconds.toString().padStart(2, '0')
    val string = "$sdays $shours:$sminutes:$sseconds"

    return string.ifEmpty { "0s" }
}
