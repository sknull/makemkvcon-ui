package de.visualdigits.makemkvconui.domain.model.tvdb

import kotlinx.serialization.Serializable

@Serializable
data class CastAndCrew(
    val actors: List<Person> = listOf(),
    val directors: List<Person> = listOf(),
    val writers: List<Person> = listOf()
)
