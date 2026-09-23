package de.visualdigits.makemkvconui.domain.model.bluray.mpls

data class ExtDataEntry(
    val extDataType: Int,
    val extDataVersion: Int,
    val extDataStartAddress: Long,
    val extDataLength: Long
)
