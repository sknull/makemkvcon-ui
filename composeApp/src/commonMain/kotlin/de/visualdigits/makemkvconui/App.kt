package de.visualdigits.makemkvconui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import coil3.compose.setSingletonImageLoaderFactory
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.makemkvconui.data.repository.ImageCache
import de.visualdigits.makemkvconui.presentation.model.MakemkvConUiViewModel
import de.visualdigits.makemkvconui.presentation.page.MainPage
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    platformType: PlatformType
) {
    val viewModel = koinViewModel<MakemkvConUiViewModel>()
    val imageCache = koinInject<ImageCache>()

    setSingletonImageLoaderFactory { _ ->
        imageCache.getImageLoader()
    }

    LaunchedEffect(Unit) {
        viewModel.platformType = platformType
    }

    MainPage(
        viewModel = viewModel,
        platformType = platformType
    )
}
