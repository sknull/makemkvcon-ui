package de.visualdigits.makemkvconui.domain.model.settings

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.EnumFieldDescriptor
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.label_language
import de.visualdigits.compose.resources.tooltip_language
import de.visualdigits.makemkvconui.domain.model.type.Language

@Immutable
class Settings: AbstractConfiguration<Settings, SK>() {

    init {
        initialize(DESCRIPTORS)
    }

    companion object {
        val DESCRIPTORS = listOf(

            /** The UI language. */
            EnumFieldDescriptor(
                fieldClass = Language::class,
                key = SK.language,
                label = UiText.StringResourceId(Res.string.label_language),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_language),
                options = { _, _ -> Language.options },
                keyFactory = Language,
                default = Language.EN
            ),
        )
    }

    override fun createInstance(newValues: Map<SK, Any?>): Settings {
        return Settings().initialize(DESCRIPTORS, newValues)
    }
}
