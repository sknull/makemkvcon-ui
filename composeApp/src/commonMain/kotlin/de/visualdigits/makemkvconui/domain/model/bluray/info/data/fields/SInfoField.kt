package de.visualdigits.makemkvconui.domain.model.bluray.info.data.fields

enum class SInfoField(
    val id: Int
) : Field {

    StreamType(1),
    CodecId(5),
    CodecShort(6),
    CodecLong(7),
    VideoResolution(19),
    VideoAspect(20),
    VideoFrameRate(21),
    BitDepth(22),
    LanguageCode(28),
    LanguageName(29),
    StreamInfo(30),
    HtmlHeader(31),
    StreamFlags(33),
    MetadataKey(38),
    ConversionType(42),
    Unknown(9999)
    ;

    companion object {

        fun fromId(id: Int): SInfoField = entries.find { id == it.id } ?: SInfoField.Unknown
    }
}
