package com.meet.project.oneclickshop.ui.theme

import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

object ThemeColor {
    val Primary = Color(0xFFF2EAD3)
    val PrimaryLight = Color(0xFFF9F6EB)
    val Secondary = Color(0xFF3F2305)
}

val LightColors = lightColors(
    primary = ThemeColor.Primary,
    secondary = ThemeColor.Secondary,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    primaryVariant = ThemeColor.PrimaryLight,
    secondaryVariant = ThemeColor.Secondary,
)