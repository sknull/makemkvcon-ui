package de.visualdigits.makemkvconui.presentation.style

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.Roboto_Black
import de.visualdigits.compose.resources.Roboto_Bold
import de.visualdigits.compose.resources.Roboto_Regular
import org.jetbrains.compose.resources.Font

@Composable
fun typography(
    textColor: Color,
    sizeFactor: Float
): Typography {
    val fontFamilyRegular = FontFamily(Font(Res.font.Roboto_Regular))
    val fontFamilyBold = FontFamily(Font(Res.font.Roboto_Bold))
    val fontFamilyBlack = FontFamily(Font(Res.font.Roboto_Black))

    return Typography(
        headlineSmall = createTextStyle(
            fontFamily = fontFamilyBlack,
            fontWeight = FontWeight.Black,
            fontSize = 18f,
            lineHeight = 1.5f,
            letterSpacing = 0.2.sp,
            color = textColor,
            sizeFactor = sizeFactor
        ),
        headlineMedium = createTextStyle(
            fontFamily = fontFamilyBlack,
            fontWeight = FontWeight.Black,
            fontSize = 24f,
            lineHeight = 1.5f,
            letterSpacing = 0.2.sp,
            color = textColor,
            sizeFactor = sizeFactor
        ),
        headlineLarge = createTextStyle(
            fontFamily = fontFamilyBlack,
            fontWeight = FontWeight.Black,
            fontSize = 30f,
            lineHeight = 1.5f,
            letterSpacing = 0.2.sp,
            color = textColor,
            sizeFactor = sizeFactor
        ),

        titleSmall = createTextStyle(
            fontFamily = fontFamilyBold,
            fontWeight = FontWeight.Bold,
            fontSize = 14f,
            lineHeight = 1.2f,
            letterSpacing = 0.2.sp,
            color = textColor,
            sizeFactor = sizeFactor
        ),
        titleMedium = createTextStyle(
            fontFamily = fontFamilyBold,
            fontWeight = FontWeight.Bold,
            fontSize = 18f,
            lineHeight = 1.2f,
            letterSpacing = 0.2.sp,
            color = textColor,
            sizeFactor = sizeFactor
        ),
        titleLarge = createTextStyle(
            fontFamily = fontFamilyBold,
            fontWeight = FontWeight.Bold,
            fontSize = 24f,
            lineHeight = 1.2f,
            letterSpacing = 0.2.sp,
            color = textColor,
            sizeFactor = sizeFactor
        ),

        bodySmall = createTextStyle(
            fontFamily = fontFamilyRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 14f,
            lineHeight = 1.2f,
            letterSpacing = 0.2.sp,
            color = textColor,
            sizeFactor = sizeFactor
        ),
        bodyMedium = createTextStyle(
            fontFamily = fontFamilyRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 18f,
            lineHeight = 1.2f,
            letterSpacing = 0.2.sp,
            color = textColor,
            sizeFactor = sizeFactor
        ),
        bodyLarge = createTextStyle(
            fontFamily = fontFamilyRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 24f,
            lineHeight = 1.2f,
            letterSpacing = 0.2.sp,
            color = textColor,
            sizeFactor = sizeFactor
        ),

        displaySmall = createTextStyle(
            fontFamily = fontFamilyRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 14f,
            lineHeight = 1.2f,
            letterSpacing = 0.2.sp,
            color = textColor,
            sizeFactor = sizeFactor
        ),
        displayMedium = createTextStyle(
            fontFamily = fontFamilyRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 18f,
            lineHeight = 1.2f,
            letterSpacing = 0.2.sp,
            color = textColor,
            sizeFactor = sizeFactor
        ),
        displayLarge = createTextStyle(
            fontFamily = fontFamilyRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 24f,
            lineHeight = 1.2f,
            letterSpacing = 0.2.sp,
            color = textColor,
            sizeFactor = sizeFactor
        ),

        labelSmall = createTextStyle(
            fontFamily = fontFamilyBold,
            fontWeight = FontWeight.Bold,
            fontSize = 14f,
            lineHeight = 1.2f,
            letterSpacing = 0.2.sp,
            color = textColor,
            sizeFactor = sizeFactor
        ),
        labelMedium = createTextStyle(
            fontFamily = fontFamilyBold,
            fontWeight = FontWeight.Bold,
            fontSize = 18f,
            lineHeight = 1.2f,
            letterSpacing = 0.2.sp,
            color = textColor,
            sizeFactor = sizeFactor
        ),
        labelLarge = createTextStyle(
            fontFamily = fontFamilyBold,
            fontWeight = FontWeight.Bold,
            fontSize = 24f,
            lineHeight = 1.2f,
            letterSpacing = 0.2.sp,
            color = textColor,
            sizeFactor = sizeFactor
        )
    )
}

private fun createTextStyle(
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    fontSize: Float,
    lineHeight: Float,
    letterSpacing: TextUnit,
    color: Color,
    sizeFactor: Float
): TextStyle {
    val scaledFontSize = fontSize * sizeFactor
    return TextStyle(
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        fontSize = scaledFontSize.sp,
        lineHeight = (scaledFontSize * lineHeight).sp,
        letterSpacing = letterSpacing,
        color = color
    )
}
