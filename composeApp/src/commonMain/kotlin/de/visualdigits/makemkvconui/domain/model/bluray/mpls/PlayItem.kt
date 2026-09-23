package de.visualdigits.makemkvconui.domain.model.bluray.mpls

data class PlayItem(
    val length: Int,
    val clipInformationFileName: String,
    val clipCodecIdentifier: String,
    val isMultiAngle: Boolean,
    val connectionCondition: List<Int>,
    val refToSTCID: Int,
    val inTime: Long,
    val outTime: Long,
    val uoMaskTable: Long,
    val playItemRandomAccessFlag: Boolean,
    val stillMode: Int,
    val stillTime: Int?,
    val stnTable: STNTable
)
