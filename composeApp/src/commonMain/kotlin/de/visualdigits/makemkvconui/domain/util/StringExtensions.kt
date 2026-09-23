package de.visualdigits.makemkvconui.domain.util

import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Entities

private val P_DURATION = "(\\d+?)M(\\d+?)S".toRegex()
private val P_MINUTES = "(\\d+?)M".toRegex()
private val P_SECONDS = "(\\d+?)S".toRegex()
private val WORD_SEPARATORS = " -_#:,;"

private val xmlSettings = Document.OutputSettings().apply {
    escapeMode(Entities.EscapeMode.xhtml) // xhtml entspricht der XML-Logik
}

fun escapeXml(xml: String): String = Entities.escape(xml, xmlSettings)

fun unescapeXml(xml: String): String = Entities.unescape(xml)

fun normalizeXml(xml: String): String = xml
    .replace("&nbsp;", " ")
    .replace("&amp;", "&")
    .replace("&", "&amp;")

/**
 * extract domain for googles favicon service
 */
fun String.getFaviconUrl(sizePx: Int): String {
    val domain = this
        .substringAfter("://")   // Entfernt http:// oder https://
        .substringBefore("/")    // Entfernt alle Pfade am Ende
        .removePrefix("www.")    // Optional, aber macht die Domain sauberer

    return "https://www.google.com/s2/favicons?domain=${domain}&sz=${sizePx}"
}

fun String.notBlank(): String? = this.ifBlank { null }

fun String.capitalizeWords(): String {
    val separator = this.find { c -> WORD_SEPARATORS.contains(c) } ?: ' '
    return this.split(separator).joinToString(separator.toString()) { w -> w.take(1).uppercase() + w.drop(1).lowercase() }
}
