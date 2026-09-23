package de.visualdigits.makemkvconui.domain.model.bluray.mpls

data class PlayListMarks(
    val length: Long,
    val numberOfPlayListMarks: Int,
    val playListMarks: List<PlayListMarkItem>
)
