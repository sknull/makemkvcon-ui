package de.visualdigits.makemkvconui.domain.model.tvdb

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val name: String,
    val type: String? = null,
    val playsAs: String? = null,
    val numberOfEpisodes: Int? = null,
    val dates: String? = null,
    val thumbUrl: String? = null,
)
