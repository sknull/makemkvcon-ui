package de.visualdigits.makemkvconui.domain.model.bluray.mpls

data class ExtensionData(
    val length: Long,
    val dataBlockStartAddress: Long?,
    val numberOfExtDataEntries: Int?,
    val extDataEntries: List<ExtDataEntry>
)
