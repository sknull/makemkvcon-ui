package de.visualdigits.makemkvconui.domain.model.bluray.mpls

data class AppInfoPlayList(
    val length: Long,
    val playbackType: Int,
    val playbackCount: Int?,
    val uoMaskTable: Long,
    val miscFlags: Int
)
