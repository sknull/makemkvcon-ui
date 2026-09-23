package de.visualdigits.makemkvconui.domain.model.settings

import de.visualdigits.common.domain.model.configuration.FieldKey

enum class SK : FieldKey<SK> {

    language,
    ;

    companion object {
        fun fromString(value: String): SK? {
            return entries.find { entry -> entry.name == value }
        }
    }
}
