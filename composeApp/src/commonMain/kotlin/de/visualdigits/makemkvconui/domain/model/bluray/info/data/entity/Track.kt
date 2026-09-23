package de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity

data class Track(
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
    val titleId: String
) : Entity
