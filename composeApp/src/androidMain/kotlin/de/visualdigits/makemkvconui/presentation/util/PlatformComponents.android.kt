package de.visualdigits.makemkvconui.presentation.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import co.touchlab.kermit.Logger
import org.koin.mp.KoinPlatformTools

actual fun openBrowser(url: String) {
    try {
        val currentActivity = KoinPlatformTools.defaultContext().get().getOrNull<Activity>()
        if (currentActivity == null) {
            Logger.e("Could not resolve active Android Activity from Koin.")
            return
        }

        currentActivity.startActivity(Intent(Intent.ACTION_VIEW).apply {
            this.data = url.toUri()
        })
    } catch (e: Exception) {
        Logger.e("Failed to open browser for link", e)
    }
}

actual fun sendEmail(emailAddress: String, subject: String, body: String?) {
    try {
        val currentActivity = KoinPlatformTools.defaultContext().get().getOrNull<Activity>()
        if (currentActivity == null) {
            Logger.w("Could not resolve active Android Activity from Koin.")
            return
        }

        val encodedEmailAddress = emailAddress.urlEncode()
        val encodeSubject = subject.urlEncode()
        val encodeBody = body?.urlEncode()
        val bodyParam = encodeBody?.let { b -> "&body=$b"} ?: ""
        val emailUri = "mailto:$encodedEmailAddress?subject=$encodeSubject$bodyParam"

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = emailUri.toUri()
        }
        currentActivity.startActivity(emailIntent)
    } catch (e: Exception) {
        Logger.e("Failed to open email link emailAddress", e)
    }
}

actual fun String.urlEncode(): String = Uri.encode(this)
