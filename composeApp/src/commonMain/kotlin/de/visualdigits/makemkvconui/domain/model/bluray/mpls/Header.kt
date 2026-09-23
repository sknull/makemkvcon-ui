package de.visualdigits.makemkvconui.domain.model.bluray.mpls

// Datenstrukturen (ersetzen die Python-Dictionaries)
data class Header(
    val typeIndicator: String,
    val versionNumber: String,
    val playListStartAddress: Long,
    val playListMarkStartAddress: Long,
    val extensionDataStartAddress: Long
)
