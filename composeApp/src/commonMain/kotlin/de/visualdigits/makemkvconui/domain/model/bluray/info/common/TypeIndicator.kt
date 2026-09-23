package de.visualdigits.makemkvconui.domain.model.bluray.info.common

enum class TypeIndicator(
    val id: Int
) {

    String(0),
    Int(1),
    Long(2),
    Double(3),
    Boolean(4),
    ConstString(5),
    Binary(10),
    Block_Sourceinformation(6119),
    Block_Titleinformation(6120),
    Block_Trackinformation(6121),
    MediaType_Video(6201),
    MediaType_Audio(6202),
    MediaType_Subtitles(6203),
    FormatFlag(5088),
    Unknown(9999)
    ;

    companion object {

        fun fromId(id: Int): TypeIndicator = entries.find { id == it.id } ?: TypeIndicator.Unknown
    }
}
