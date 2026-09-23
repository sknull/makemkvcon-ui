package de.visualdigits.makemkvconui.presentation.style

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val MyShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
)

val Shapes.gap: Dp get() = 4.dp

val Shapes.buttonsFlat: Boolean get() = true
