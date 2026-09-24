package de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity

data class Track(
    val titleId: Int,
    val titleName: String,
    val chaptersCount: Int,
    val duration: String,
    val sizeStr: String,
    val sizeLong: Long,
    val mplsName: String,
    val segmentCount: Int,
    val segmentMap: List<Int>,
    val outputFileName: String,
    val languageCode: String,
    val languageName: String,
    val information: String,
    val htmlHeader: String,

    val streams: List<Stream> = listOf()
) : Entity {

    override fun toString(): String {
//        return "[$titleId] $titleName ($languageName) $duration\n- ${streams.joinToString("\n- ")}"
        return "[$titleId / $mplsName] $titleName ($languageName) $duration - $outputFileName"
    }
}
