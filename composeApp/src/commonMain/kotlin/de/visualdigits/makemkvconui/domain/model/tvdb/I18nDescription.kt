package de.visualdigits.makemkvconui.domain.model.tvdb

import kotlinx.serialization.Serializable

@Serializable
data class I18nDescription(
    val translations: Map<String, Description> = mapOf(),
)
