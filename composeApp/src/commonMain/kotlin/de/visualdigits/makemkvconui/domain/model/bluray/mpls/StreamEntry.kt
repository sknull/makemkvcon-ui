package de.visualdigits.makemkvconui.domain.model.bluray.mpls

data class StreamEntry(
    val length: Int,
    val streamType: Int?,
    val refToStreamPID: String?,
    val refToSubPathID: Int?,
    val refToSubClipID: Int?
)
