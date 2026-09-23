package de.visualdigits.makemkvconui.presentation.util

import co.touchlab.kermit.Logger
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder

actual fun openBrowser(url: String) {
    try {
        if (!Desktop.isDesktopSupported()) return
        val desktop = Desktop.getDesktop()
        if (desktop.isSupported(Desktop.Action.BROWSE)) {
            desktop.browse(URI(url))
        } else {
            Logger.w("Browsing is not supported")
        }
    } catch (e: Exception) {
        Logger.e("Failed to open browser link", e)
    }
}

actual fun sendEmail(emailAddress: String, subject: String, body: String?) {
    try {
        if (!Desktop.isDesktopSupported()) {
            return
        }
        val desktop = Desktop.getDesktop()

        val encodedEmailAddress = emailAddress.urlEncode()
        val encodeSubject = subject.urlEncode()
        val encodeBody = body?.urlEncode()
        val bodyParam = encodeBody?.let { b -> "&body=$b"} ?: ""
        val emailUri = "mailto:$encodedEmailAddress?subject=$encodeSubject$bodyParam"

        if (desktop.isSupported(Desktop.Action.MAIL)) {
            desktop.mail(URI(emailUri))
        } else {
            Logger.w("Email is not supported")
        }
    } catch (e: Exception) {
        Logger.e("Failed to open email link emailAddress", e)
    }
}

actual fun String.urlEncode(): String = URLEncoder.encode(this, Charsets.UTF_8).replace("+", "%20")
