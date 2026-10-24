package de.visualdigits.makemkvconui.domain.model.tvdb

import kotlinx.serialization.Serializable

@Serializable
data class Season(
    val orderType: String,
    val season: Int,
    val from: String,
    val to: String,
    val episodes: Int,
    val episodesUrl: String? = null,
    val episodeList: List<Episode> = listOf()
)
