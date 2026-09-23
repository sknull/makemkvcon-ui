package de.visualdigits.makemkvconui.presentation.util

import co.touchlab.kermit.Logger

fun routePlatformLink(uri: String) {
    val rawInput = uri.trim()
        .removeSurrounding("\\\"")
        .removeSurrounding("\"")
        .removeSurrounding("'")
        .trim()

    if (rawInput.startsWith("mailto:", ignoreCase = true) || rawInput.contains("@")) {
        sendEmail(
            emailAddress = rawInput.removePrefix("mailto:"),
            subject = "MakemkvConUi Support"
        )
    } else if (rawInput.startsWith("http://", ignoreCase = true) || rawInput.startsWith("https://", ignoreCase = true)) {
        openBrowser(url = rawInput)
    } else {
        Logger.w("Unknown link format: $rawInput")
    }
}

expect fun openBrowser(url: String)

expect fun sendEmail(emailAddress: String, subject: String, body: String? = null)

expect fun String.urlEncode(): String


