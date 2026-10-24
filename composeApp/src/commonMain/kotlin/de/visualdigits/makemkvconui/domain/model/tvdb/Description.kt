package de.visualdigits.makemkvconui.domain.model.tvdb

import kotlinx.serialization.Serializable

@Serializable
data class Description(
    val text: String,
    val aliases: List<String>
)
