package de.visualdigits.makemkvconui.presentation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.common.domain.model.errorhandling.onError
import de.visualdigits.common.domain.model.errorhandling.onSuccess
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.common.presentation.components.applyAppLanguage
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.common.presentation.model.ScrollIntent
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.error_local_wrong_filetype
import de.visualdigits.generated.AppVersion
import de.visualdigits.makemkvconui.domain.model.errorhandling.toUiText
import de.visualdigits.makemkvconui.domain.model.settings.SK
import de.visualdigits.makemkvconui.domain.model.settings.Settings
import de.visualdigits.makemkvconui.domain.model.type.Language
import de.visualdigits.makemkvconui.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.io.Sink
import kotlinx.io.Source

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class MakemkvConUiViewModel(
    private val settingsRepository: SettingsRepository,
    scope: CoroutineScope
) : ViewModel() {

    val scrollPosition: MutableMap<String, Triple<Int, Int?, ScrollIntent>> = mutableMapOf()
    var platformType: PlatformType = PlatformType.unknown

    private val _state = MutableStateFlow(MakemkvConUiState())
    val state = _state.asStateFlow()

    private val _settings = MutableStateFlow<Settings?>(null)
    val settings = _settings.asStateFlow()

    private val _editedSettings = MutableStateFlow<Settings?>(Settings().initialize(Settings.DESCRIPTORS, mapOf(SK.language to Language.DE)))
    val editedSettings = _editedSettings.asStateFlow()

    init {
        Logger.i("Application version ${AppVersion().version} initializing...")
        loadData()
        Logger.i("Application started")

        onAction(MakemkvConUiAction.OnInitializeTabs(
            tabLabels = listOf(
                "settings" to UiText.DynamicString(""),
                "info" to UiText.DynamicString("")
            )
        ))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun onCommonAction(action: CommonAction) {
        when (action) {
            is CommonAction.OnScrollPositionChange -> {
                action.id?.also { id ->
                    scrollPosition[id] = Triple(action.position, action.offset, action.scrollIntent)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun onAction(action: MakemkvConUiAction) {
        when (action) {

            //
            // Settings
            //
            is MakemkvConUiAction.OnSettingsValueChanged -> {
                _editedSettings.update { current ->
                    current?.copy(
                        key = action.keyValue.descriptor.key as SK,
                        value = action.keyValue.value
                    )
                }
            }

            is MakemkvConUiAction.OnEditSettingsCancelClick -> {
                _settings.value?.get<Language>(SK.language)
                    ?.also { l ->
                        applyAppLanguage(l.localeCode)
                    }
                _state.update {
                    it.copy(
                        isEditingSettings = false,
                        previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                        selectedTabIndex = 0,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }

            is MakemkvConUiAction.OnSaveSettingsClick -> {
                saveSettings(_editedSettings.value)
            }

            is MakemkvConUiAction.OnSettingsImport -> {
                importSettings(action.fileName, action.source)
            }

            is MakemkvConUiAction.OnSettingsExport -> {
                exportSettings(action.fileName, action.sink)
            }

            //
            // Tabs
            //
            is MakemkvConUiAction.OnInitializeTabs -> {
                _state.update { 
                    it.copy(
                        tabLabels = action.tabLabels,
                        tabLabelKeys = action.tabLabels.map { tl -> tl.first },
                        previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                        selectedTabIndex = 0,
                        isEditingSettings = false,
                        isShowInfos = false,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
            is MakemkvConUiAction.OnTabSelected -> {
                if (state.value.tabLabels[action.index].first == "settings") {
                    _editedSettings.value = _settings.value
                }
                _state.update {
                    it.copy(
                        previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                        selectedTabIndex = action.index,
                        isEditingSettings = false,
                        isShowInfos = false,
                        uiMessage = null,
                        uiMessageSeverity = null,
                    )
                }
            }
            is MakemkvConUiAction.OnBackButton -> {
                _state.update {
                    it.copy(
                        selectedTabIndex = it.previousSelectedTabIndexes.lastOrNull() ?: 0,
                        previousSelectedTabIndexes = it.previousSelectedTabIndexes.dropLast(1),
                    )
                }
            }

            //
            // Misc
            //
            is MakemkvConUiAction.OnCollapsibleStateChange -> {
                _state.update {
                    it.copy(
                        collapsibleState = it.collapsibleState + (action.id to action.isExpanded)
                    )
                }
            }

            is MakemkvConUiAction.OnLanguageSelected -> {
                applyAppLanguage(action.language.localeCode)
                _state.update {
                    it.copy(
                        language = action.language
                    )
                }
            }

            is MakemkvConUiAction.OnShowInfosClick -> {
                _state.update {
                    it.copy(
                        isShowInfos = action.isShowInfos,
                        isEditingSettings = false,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
        }
    }

    private fun importSettings(fileName: String, source: Source) = viewModelScope.launch {
        Logger.i("Importing settings")
        if (fileName.endsWith(".json", ignoreCase = true)) {
            val settingsResult = settingsRepository.importSettings(source)
            if (settingsResult is Result.Success) {
                val settings = settingsResult.data
                _settings.update { settings }
                _state.update {
                    it.copy(
                        isEditingSettings = false,
                        previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                        selectedTabIndex = 0,
                        uiMessage = null,
                    )
                }
            } else if (settingsResult is Result.Error) {
                Logger.e("Could not import settings", settingsResult.throwable)
                _state.update {
                    it.copy(
                        uiMessage = settingsResult.error.toUiText(),
                        uiMessageSeverity = Severity.Error,
                        isEditingSettings = false,
                        previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                        selectedTabIndex = 0,
                    )
                }
            }
        } else {
            _state.update {
                it.copy(
                    isEditingSettings = false,
                    previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                    selectedTabIndex = 0,
                    uiMessage = UiText.StringResourceId(Res.string.error_local_wrong_filetype),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun exportSettings(fileName: String, sink: Sink) = viewModelScope.launch {
        Logger.i("Exporting settings")
        if (fileName.endsWith(".json", ignoreCase = true)) {
            val settings = _settings.value
            if(settings != null) {
                settingsRepository.exportSettings(settings, sink)
                    .onSuccess {
                        _state.update {
                            it.copy(
                                uiMessage = null,
                                isEditingSettings = false,
                                previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                                selectedTabIndex = 0,
                            )
                        }
                    }
                    .onError { error, throwable ->
                        Logger.e("Could not export settings", throwable)
                        _state.update {
                            it.copy(
                                uiMessage = error.toUiText(),
                                uiMessageSeverity = Severity.Error,
                                isEditingSettings = false,
                                previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                                selectedTabIndex = 0,
                            )
                        }
                    }
            }
        } else {
            _state.update {
                it.copy(
                    isEditingSettings = false,
                    previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                    selectedTabIndex = 0,
                    uiMessage = UiText.StringResourceId(Res.string.error_local_wrong_filetype),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun loadData() = viewModelScope.launch {
        val result = settingsRepository.getSettings()
        if (result is Result.Success) {
            val settings = result.data
            val finalSettings = if (settings != null) {
                settings
            } else {
                val newValues = mapOf(
                    SK.language to Language.EN,
                )
                val newSettings = Settings().initialize(Settings.DESCRIPTORS, newValues)
                settingsRepository.setSettings(newSettings)
                    .onError { _, throwable ->
                        Logger.e("Could not safe initial settings", throwable)
                    }
                newSettings
            }

            applyAppLanguage(finalSettings.get<Language>(SK.language)?.localeCode?: Language.EN.localeCode)

            _settings.update { finalSettings }
            _state.update {
                it.copy(
                    uiMessage = null,
                    uiMessageSeverity = null
                )
            }
        } else if (result is Result.Error) {
            Logger.e("Could not load data", result.throwable)
            _state.update {
                it.copy(
                    uiMessage = result.error.toUiText(),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun saveSettings(
        settings: Settings?,
    ) = viewModelScope.launch {
        checkNotNull(settings) { "No settings to save" }
        settingsRepository.setSettings(settings)
            .onSuccess {
                val language = settings.get<Language>(SK.language) ?: Language.EN
                applyAppLanguage(language.localeCode)
                _editedSettings.value = null
                _settings.update { settings }
                _state.update {
                    it.copy(
                        isEditingSettings = false,
                        previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                        selectedTabIndex = 0,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
            .onError { error, throwable ->
                Logger.e("Could not save settings", throwable)
                _state.update {
                    it.copy(
                        isEditingSettings = false,
                        previousSelectedTabIndexes = it.previousSelectedTabIndexes + it.selectedTabIndex,
                        selectedTabIndex = 0,
                        uiMessage = error.toUiText(),
                        uiMessageSeverity = Severity.Error
                    )
                }
            }
    }
}
