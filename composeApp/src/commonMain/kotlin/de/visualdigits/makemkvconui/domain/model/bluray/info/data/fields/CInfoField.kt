package de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields

enum class CInfoField(
    val id: Int
) : Field {

    MediaTypeName(1),
    TitleName(2),
    LanguageCode(28),
    LanguageName(29),
    Information(30),
    HtmlHeader(31),
    VolumeName(32),
    VolumeId(33),

    Unknown(9999)
    ;

    companion object {

        fun fromId(id: Int): CInfoField = entries.find { id == it.id } ?: CInfoField.Unknown
    }
}
