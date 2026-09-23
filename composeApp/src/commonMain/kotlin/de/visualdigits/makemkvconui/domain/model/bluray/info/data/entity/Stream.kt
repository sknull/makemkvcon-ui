package de.visualdigits.makemkvconui.domain.model.bluray.info.data.entity

data class Stream(
    val streamType: String,
    val codecId: String,
    val codecShort: String,
    val codecLong: String,
    val videoResolution: String,
    val videoAspect: String,
    val videoFrameRate: String,
    val bitDepth: Int,
    val languageCode: String,
    val languageName: String,
    val streamInfo: String,
    val htmlHeader: String,
    val streamFlags: Int,
    val metadataKey: String,
    val conversionType: String,
) : Entity
