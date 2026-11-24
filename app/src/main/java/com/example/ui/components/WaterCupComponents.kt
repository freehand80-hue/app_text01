package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.WaterBlueFill
import com.example.ui.theme.WaterBlueHighlight
import com.example.ui.theme.WaterBluePrimary
import com.example.ui.theme.WaterCupEmptyBg
import com.example.ui.theme.WaterCupOutline

@Composable
fun WaterCupsGrid(
    waterCups: Int,
    goal: Int = 8,
    modifier: Modifier = Modifier
) {
    val cupsPerRow = 4
    val rows = (goal + cupsPerRow - 1) / cupsPerRow

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        for (rowIndex in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (colIndex in 0 until cupsPerRow) {
                    val cupNumber = rowIndex * cupsPerRow + colIndex + 1
                    if (cupNumber <= goal) {
                        val isFilled = cupNumber <= waterCups
                        val description = if (isFilled) {
                            stringResource(R.string.water_cup_cd_filled, cupNumber)
                        } else {
                            stringResource(R.string.water_cup_cd_empty, cupNumber)
                        }
                        WaterCupIcon(
                            isFilled = isFilled,
                            contentDescription = description,
                            modifier = Modifier.size(width = 52.dp, height = 60.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WaterCupIcon(
    isFilled: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val fillFraction by animateFloatAsState(
        targetValue = if (isFilled) 0.78f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "waterCupFill"
    )

    Canvas(
        modifier = modifier
            .height(60.dp)
            .semantics { this.contentDescription = contentDescription }
    ) {
        val w = size.width
        val h = size.height
        val strokeWidth = 3.dp.toPx()

        val topInset = w * 0.10f
        val bottomInset = w * 0.22f
        val topY = h * 0.08f
        val bottomY = h * 0.92f

        val glassPath = Path().apply {
            moveTo(topInset, topY)
            lineTo(w - topInset, topY)
            lineTo(w - bottomInset, bottomY)
            lineTo(bottomInset, bottomY)
            close()
        }

        // Empty glass background
        drawPath(
            path = glassPath,
            color = WaterCupEmptyBg
        )

        // Animated water fill clipped inside the glass
        if (fillFraction > 0.01f) {
            clipPath(glassPath, clipOp = ClipOp.Intersect) {
                val liquidTop = bottomY - (bottomY - topY) * fillFraction
                drawRect(
                    color = WaterBlueFill,
                    topLeft = Offset(0f, liquidTop),
                    size = androidx.compose.ui.geometry.Size(w, bottomY - liquidTop)
                )
                // Bright meniscus top highlight for visual clarity
                drawRect(
                    color = WaterBlueHighlight,
                    topLeft = Offset(0f, liquidTop),
                    size = androidx.compose.ui.geometry.Size(w, 4.dp.toPx())
                )
                // Subtle vertical glass sheen
                drawLine(
                    color = Color.White.copy(alpha = 0.35f),
                    start = Offset(w * 0.30f, liquidTop + 6.dp.toPx()),
                    end = Offset(w * 0.34f, bottomY - 6.dp.toPx()),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Crisp glass outline
        drawPath(
            path = glassPath,
            color = if (isFilled) WaterBluePrimary else WaterCupOutline,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
