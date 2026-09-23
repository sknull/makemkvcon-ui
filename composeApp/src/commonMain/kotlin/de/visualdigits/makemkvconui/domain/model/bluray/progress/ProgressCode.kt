package de.visualdigits.makemkvconui.domain.model.bluray.progress

enum class ProgressCode(
    val id: Int
) {
    ProcessingTitles(3103),
    ProcessingClips(3400),
    ProcessingPlaylists(3401),
    Decrypting(3402),
    OpeningDisc(3404),
    ProcessingBdPlus(3406),
    ScanningDevices(5018),

    Unknown(9999)
    ;

    companion object {

        fun fromId(id: Int): ProgressCode = ProgressCode.entries.find { id == it.id } ?: ProgressCode.Unknown
    }
}
