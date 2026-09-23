package de.visualdigits.makemkvconui.domain.model.bluray.mpls

data class PlayListMarkItem(
    val markType: Int,
    val refToPlayItemID: Int,
    val markTimeStamp: Long,
    val entryESPID: Int,
    val duration: Long
)
