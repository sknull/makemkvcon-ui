package de.visualdigits.makemkvconui.domain.model.bluray.mpls

data class SubPath(
    val length: Long,
    val subPathType: Int,
    val isRepeatSubPath: Boolean,
    val numberOfSubPlayItems: Int,
    val subPlayItems: List<SubPlayItem>
)
