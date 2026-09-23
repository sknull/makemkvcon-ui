package de.visualdigits.makemkvconui.domain.model.bluray.mpls

data class STNTable(
    val length: Int,
    val numberOfPrimaryVideoStreamEntries: Int = 0,
    val numberOfPrimaryAudioStreamEntries: Int = 0,
    val numberOfPrimaryPGStreamEntries: Int = 0,
    val numberOfPrimaryIGStreamEntries: Int = 0,
    val numberOfSecondaryAudioStreamEntries: Int = 0,
    val numberOfSecondaryVideoStreamEntries: Int = 0,
    val numberOfSecondaryPGStreamEntries: Int = 0,
    val numberOfDVStreamEntries: Int = 0,
    val primaryVideoStreamEntries: List<STNTableEntry> = emptyList(),
    val primaryAudioStreamEntries: List<STNTableEntry> = emptyList(),
    val primaryPGStreamEntries: List<STNTableEntry> = emptyList(),
    val primaryIGStreamEntries: List<STNTableEntry> = emptyList(),
    val secondaryAudioStreamEntries: List<STNTableEntry> = emptyList(),
    val secondaryVideoStreamEntries: List<STNTableEntry> = emptyList(),
    val secondaryPGStreamEntries: List<STNTableEntry> = emptyList(),
    val dvStreamEntries: List<STNTableEntry> = emptyList()
)
