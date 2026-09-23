package de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity

data class Disc(
    val mediaTypeName: String,
    val titleName: String,
    val languageCode: String,
    val languageName: String,
    val information: String,
    val htmlHeader: String,
    val volumeName: String,
    val volumeId: Int,
) : Entity
