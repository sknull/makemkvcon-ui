package de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields

enum class TInfoField(
    val id: Int
) : Field {

    TitleName(2),
    ChaptersCount(8),
    Duration(9),
    SizeStr(10),
    SizeLong(11),
    MplsName(16),
    SegmentCount(25),
    SegmentMap(26),
    OutputFileName(27),
    LanguageCode(28),
    LanguageName(29),
    Information(30),
    HtmlHeader(31),
    TitleId(33),
    Unknown(9999)
    ;

    companion object {

        fun fromId(id: Int): TInfoField = entries.find { id == it.id } ?: TInfoField.Unknown
    }
}
