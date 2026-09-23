package de.visualdigits.makemkvconui.domain.model.bluray.mpls

data class StreamAttributes(
    val length: Int,
    val streamCodingType: Int?,
    val videoFormat: Int?,
    val frameRate: Int?,
    val dynamicRangeType: Int?,
    val colorSpace: Int?,
    val crFlag: Boolean?,
    val hdrPlusFlag: Boolean?,
    val audioFormat: Int?,
    val sampleRate: Int?,
    val languageCode: String?,
    val characterCode: String?
)
