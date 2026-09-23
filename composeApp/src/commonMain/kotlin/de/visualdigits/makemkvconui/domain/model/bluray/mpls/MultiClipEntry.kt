package de.visualdigits.makemkvconui.domain.model.bluray.mpls

data class MultiClipEntry(
    val clipInformationFileName: String,
    val clipCodecIdentifier: String,
    val refToSTCID: Int
)
