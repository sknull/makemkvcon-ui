package de.visualdigits.makemkvconui.domain.model.bluray.mpls

data class PlayList(
    val length: Long,
    val numberOfPlayItems: Int,
    val numberOfSubPaths: Int,
    val playItems: List<PlayItem>,
    val subPaths: List<SubPath>
)
