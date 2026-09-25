package de.visualdigits.makemkvconui.presentation.style

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val colorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF2BC9F9), // used for text selection and handles

    secondary = Color(0xFFE1E1E1), // switchbox unchecked track
    onSecondary = Color(0xFF9A9A9A), // switchbox unchecked thumb and border

    background = Background,
    onBackground = ButtonColor,

    surface = ButtonColor, // buttons
    onSurface = SpotColor, // spot color

    surfaceVariant = Color(0xFF797979),
    surfaceContainer = Color.Transparent,
    surfaceContainerLowest = Color(0xFF797979),

    errorContainer = Color(0xffff002a), // delete dialogs
    onErrorContainer = Color(0xFFFFFFFF), // delete dialogs

    outline = SpotColor, // focused border

    primaryFixed = Color(0xAA000000) // terminal
)
