package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val NotoSansKrFamily = FontFamily(
    Font(R.font.noto_sans_kr, FontWeight.Normal),
    Font(R.font.noto_sans_kr, FontWeight.Medium),
    Font(R.font.noto_sans_kr, FontWeight.SemiBold),
    Font(R.font.noto_sans_kr, FontWeight.Bold),
    Font(R.font.noto_sans_kr, FontWeight.ExtraBold)
)

// Senior-friendly typography: every style is 22sp or larger for clear readability
val SeniorTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = NotoSansKrFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 38.sp,
        lineHeight = 46.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = NotoSansKrFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.2).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = NotoSansKrFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 34.sp
    ),
    titleLarge = TextStyle(
        fontFamily = NotoSansKrFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = NotoSansKrFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 30.sp
    ),
    labelLarge = TextStyle(
        fontFamily = NotoSansKrFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    )
)
