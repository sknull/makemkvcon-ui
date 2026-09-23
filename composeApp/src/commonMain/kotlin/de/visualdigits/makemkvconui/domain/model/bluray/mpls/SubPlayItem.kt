package de.visualdigits.makemkvconui.domain.model.bluray.mpls

data class SubPlayItem(
    val length: Int,
    val clipInformationFileName: String,
    val clipCodecIdentifier: String,
    val connectionCondition: Int,
    val isMultiClipEntries: Boolean,
    val refToSTCID: Int,
    val inTime: Long,
    val outTime: Long,
    val syncPlayItemID: Int,
    val syncStartPTS: Long,
    val numberOfMultiClipEntries: Int?,
    val multiClipEntries: List<MultiClipEntry>
)
