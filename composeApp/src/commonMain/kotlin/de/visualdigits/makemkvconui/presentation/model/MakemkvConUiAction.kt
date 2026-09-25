package de.visualdigits.makemkvconui.presentation.model

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.ui.KeyValue
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.makemkvconui.domain.model.type.Language
import kotlinx.io.Sink
import kotlinx.io.Source

sealed interface MakemkvConUiAction {

    //
    // Settings
    //

    @Immutable
    data class OnSettingsValueChanged(
        val keyValue: KeyValue,
    ): MakemkvConUiAction

    @Immutable
    class OnEditSettingsCancelClick : MakemkvConUiAction

    @Immutable
    data class OnSettingsImport(
        val fileName: String,
        val source: Source
    ): MakemkvConUiAction

    @Immutable
    data class OnSettingsExport(
        val fileName: String,
        val sink: Sink
    ): MakemkvConUiAction

    @Immutable
    class OnSaveSettingsClick : MakemkvConUiAction

    @Immutable
    data class OnShowInfosClick(
        val isShowInfos: Boolean
    ) : MakemkvConUiAction

    //
    // Tabs
    //
    @Immutable
    data class OnTabSelected(
        val index: Int
    ): MakemkvConUiAction

    @Immutable
    data class OnInitializeTabs(
        val tabLabels: List<Pair<String, UiText>>
    ): MakemkvConUiAction

    @Immutable
    class OnBackButton : MakemkvConUiAction

    //
    // makemkvcon
    //
    class OnReadDiscClicked : MakemkvConUiAction

    data class OnCurrentDriveChanged(
        val driveIndex: Int
    ) : MakemkvConUiAction

    //
    // Misc
    //

    @Immutable
    data class OnCollapsibleStateChange(
        val id: String,
        val isExpanded: Boolean
    ): MakemkvConUiAction

    @Immutable
    data class OnLanguageSelected(
        val language: Language,
    ): MakemkvConUiAction
}
