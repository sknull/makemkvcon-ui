package de.visualdigits.makemkvconui.domain.model.settings

import androidx.compose.runtime.Immutable
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.EnumFieldDescriptor
import de.visualdigits.common.domain.model.configuration.FileFieldDescriptor
import de.visualdigits.common.domain.model.ui.FileMode
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.label_language
import de.visualdigits.compose.resources.label_targetDirectory
import de.visualdigits.makemkvconui.domain.model.type.Language
import java.io.File

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
                options = { _, _ -> Language.options },
                keyFactory = Language,
                default = Language.EN
            ),

            FileFieldDescriptor(
                key = SK.targetDirectory,
                label = UiText.StringResourceId(Res.string.label_targetDirectory),
                fileMode = FileMode.DIRECTORIES_ONLY,
                valid = { _, value ->
                    value?.let { v -> if ((v as? File)?.exists() == true) Severity.Info else Severity.Error } ?: Severity.Error
                }
            ),
        )
    }

    override fun createInstance(newValues: Map<SK, Any?>): Settings {
        return Settings().initialize(DESCRIPTORS, newValues)
    }
}
