package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val SeniorIvoryColorScheme = lightColorScheme(
    primary = TakenGreen,
    onPrimary = OnTakenGreen,
    primaryContainer = TakenGreenBadge,
    onPrimaryContainer = TakenGreenPressed,
    secondary = WaterBluePrimary,
    onSecondary = OnTakenGreen,
    secondaryContainer = WaterCardBg,
    onSecondaryContainer = WarmInk,
    background = IvoryBackground,
    onBackground = WarmInk,
    surface = IvorySurface,
    onSurface = WarmInk,
    surfaceVariant = IvoryMutedSurface,
    onSurfaceVariant = WarmInkSecondary,
    outline = IvorySurfaceBorder
)

private val SeniorShapes = Shapes(
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun SeniorCareTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SeniorIvoryColorScheme,
        typography = SeniorTypography,
        shapes = SeniorShapes,
        content = content
    )
}
