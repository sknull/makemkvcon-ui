package de.visualdigits.makemkvconui.presentation.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.makemkvconui.domain.model.type.Language

@Stable
data class MakemkvConUiState(

    val language: Language = Language.EN,

    val screenWidth: Dp = Dp.Unspecified,
    val screenHeight: Dp = Dp.Unspecified,

    val previousSelectedTabIndexes: List<Int> = listOf(),
    val selectedTabIndex: Int = 0,
    val tabLabels: List<Pair<String, UiText>> = listOf(),
    val tabLabelKeys: List<String> = listOf(),

    val isShowInfos: Boolean = false,
    val isEditingSettings: Boolean = false,

    val uiMessage: UiText? = null,
    val uiMessageSeverity: Severity? = null,

    val collapsibleState: Map<String, Boolean> = mapOf(),
)
