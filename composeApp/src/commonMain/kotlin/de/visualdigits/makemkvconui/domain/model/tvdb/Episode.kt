package de.visualdigits.makemkvconui.domain.model.tvdb

import kotlinx.serialization.Serializable

@Serializable
data class Episode(
    val source: String,
    val id: String,
    val code: String,
    val name: String,
    val firstAired: String,
    val runtime: Int,
    val episodeDescription: I18nDescription = I18nDescription(),
    val metaData: Map<String, String> = mapOf(),
    val castAndCrew: CastAndCrew = CastAndCrew(),
)
