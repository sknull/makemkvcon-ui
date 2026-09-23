package de.visualdigits.makemkvconui.presentation.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.presentation.components.BindBackHandler
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.components.button.TabButtonRow
import de.visualdigits.common.presentation.components.container.ErrorCard
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_info_24px
import de.visualdigits.compose.resources.icon_settings_24px
import de.visualdigits.makemkvconui.presentation.model.MakemkvConUiAction
import de.visualdigits.makemkvconui.presentation.model.MakemkvConUiViewModel
import de.visualdigits.makemkvconui.presentation.page.settings.SettingsTab
import de.visualdigits.makemkvconui.presentation.style.AppCompositionProvider
import de.visualdigits.makemkvconui.presentation.style.IndicatorColor
import de.visualdigits.makemkvconui.presentation.style.MarineBlue
import de.visualdigits.makemkvconui.presentation.style.MyShapes
import de.visualdigits.makemkvconui.presentation.style.TextColor
import de.visualdigits.makemkvconui.presentation.style.colorScheme
import de.visualdigits.makemkvconui.presentation.style.gap
import de.visualdigits.makemkvconui.presentation.style.typography
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    viewModel: MakemkvConUiViewModel,
    platformType: PlatformType,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BindBackHandler(isEnabled = state.previousSelectedTabIndexes.isNotEmpty()) {
        viewModel.onAction(MakemkvConUiAction.OnBackButton())
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        var screenWidth by remember { mutableStateOf(maxWidth) }
        LaunchedEffect(maxWidth, maxHeight) {
            screenWidth = maxWidth
        }

        val sizeFactor = when {
            maxWidth < 500.dp -> 0.7f
//            screenWidth > 1500.dp -> 1.5f
            else -> 1.0f
        }

        val items = remember {
            linkedMapOf<Pair<String, (@Composable () -> Unit)?>, @Composable () -> Unit>(
                Pair(
                    "settings",
                    @Composable {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap / 2)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.icon_settings_24px),
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                ) to {
                    SettingsTab(
                        viewModel = viewModel,
                        platformType = platformType,
                        onAction = viewModel::onAction
                    )
                },
                Pair(
                    "info",
                    @Composable {
                        Icon(
                            painter = painterResource(Res.drawable.icon_info_24px),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                ) to {
                    InfoTab(
                        platformType = platformType
                    )
                },
            )
        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography(
                textColor = TextColor,
                sizeFactor = sizeFactor
            ),
            shapes = MyShapes
        ) {
            AppCompositionProvider {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .safeDrawingPadding()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        ErrorCard(
                            errorMessage = state.uiMessage,
                            severity = state.uiMessageSeverity,
                            shapeContainer = MaterialTheme.shapes.small
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(MaterialTheme.shapes.gap)
                        ) {
                            TabButtonRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .drawBehind {
                                        val strokeWidth = 2.dp.toPx()
                                        drawLine(
                                            color = MarineBlue,
                                            start = Offset(0f, size.height - strokeWidth / 2),
                                            end = Offset(size.width, size.height - strokeWidth / 2),
                                            strokeWidth = strokeWidth
                                        )
                                    },
                                horizontalArrangement = Arrangement.spacedBy(1.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                selectedTab = { state.selectedTabIndex },
                                items = items
                            ) { content, key, index ->
                                IndicatorButton(
                                    modifier = Modifier
                                        .width(40.dp),
                                    buttonColor = MarineBlue,
                                    textColor = Color.White,
                                    width = Dp.Unspecified,
                                    height = 40.dp,
                                    content = content,
                                    text = state.tabLabels[index].second.asString(),
                                    textStyle = MaterialTheme.typography.titleSmall,
                                    indicatorPosition = Alignment.BottomCenter,
                                    indicatorColor = IndicatorColor,
                                    shape = RoundedCornerShape(
                                        topStart = 4.dp,
                                        topEnd = 4.dp,
                                        bottomStart = 0.dp,
                                        bottomEnd = 0.dp
                                    ),
                                    selected = state.selectedTabIndex == index,
                                    onClick = {
                                        viewModel.onAction(
                                            MakemkvConUiAction.OnTabSelected(index)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
