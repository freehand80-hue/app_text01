package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MedicationTime
import com.example.ui.components.WaterCupsGrid
import com.example.ui.theme.CelebrationBadgeBg
import com.example.ui.theme.CelebrationBg
import com.example.ui.theme.CelebrationBorder
import com.example.ui.theme.CelebrationIconTint
import com.example.ui.theme.DinnerIndigo
import com.example.ui.theme.DinnerIndigoBg
import com.example.ui.theme.IvoryMutedSurface
import com.example.ui.theme.IvorySurface
import com.example.ui.theme.IvorySurfaceBorder
import com.example.ui.theme.LunchSky
import com.example.ui.theme.LunchSkyBg
import com.example.ui.theme.MorningAmber
import com.example.ui.theme.MorningAmberBg
import com.example.ui.theme.OnTakenGreen
import com.example.ui.theme.TakenGreen
import com.example.ui.theme.TakenGreenBadge
import com.example.ui.theme.WarmInk
import com.example.ui.theme.WarmInkSecondary
import com.example.ui.theme.WaterBluePrimary
import com.example.ui.theme.WaterCardBg
import com.example.ui.theme.WaterCardBorder

@Composable
fun DailyCheckScreen(
    uiState: DailyCheckUiState,
    onToggleMedication: (MedicationTime) -> Unit,
    onAddWaterCup: () -> Unit,
    onRemoveWaterCup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                TodayDateHeaderCard(
                    formattedDate = uiState.formattedDate,
                    dayOfWeek = uiState.formattedDayOfWeek
                )

                MedicationSection(
                    morningTaken = uiState.morningTaken,
                    lunchTaken = uiState.lunchTaken,
                    dinnerTaken = uiState.dinnerTaken,
                    takenCount = uiState.takenMedicationCount,
                    onToggleMedication = onToggleMedication
                )

                WaterIntakeSection(
                    waterCups = uiState.waterCups,
                    waterGoal = uiState.waterGoal,
                    onAddWaterCup = onAddWaterCup,
                    onRemoveWaterCup = onRemoveWaterCup
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TodayDateHeaderCard(
    formattedDate: String,
    dayOfWeek: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("today_date_card"),
        shape = RoundedCornerShape(28.dp),
        color = IvorySurface,
        border = BorderStroke(2.dp, IvorySurfaceBorder),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(TakenGreenBadge),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarToday,
                    contentDescription = stringResource(R.string.today_date_label),
                    tint = TakenGreen,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = WarmInk,
                    modifier = Modifier.testTag("today_date_text")
                )
                Text(
                    text = dayOfWeek,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TakenGreen
                )
            }
        }
    }
}

@Composable
private fun MedicationSection(
    morningTaken: Boolean,
    lunchTaken: Boolean,
    dinnerTaken: Boolean,
    takenCount: Int,
    onToggleMedication: (MedicationTime) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = stringResource(R.string.med_section_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = WarmInk
            )
            Text(
                text = if (takenCount == 3) {
                    stringResource(R.string.med_all_done)
                } else {
                    stringResource(R.string.med_summary_format, takenCount)
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (takenCount == 3) TakenGreen else WarmInkSecondary
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            MedicationCheckButton(
                label = stringResource(R.string.med_morning),
                isTaken = morningTaken,
                icon = Icons.Rounded.WbSunny,
                iconTint = MorningAmber,
                iconBg = MorningAmberBg,
                onClick = { onToggleMedication(MedicationTime.MORNING) },
                modifier = Modifier.testTag("morning_med_button")
            )

            MedicationCheckButton(
                label = stringResource(R.string.med_lunch),
                isTaken = lunchTaken,
                icon = Icons.Rounded.LightMode,
                iconTint = LunchSky,
                iconBg = LunchSkyBg,
                onClick = { onToggleMedication(MedicationTime.LUNCH) },
                modifier = Modifier.testTag("lunch_med_button")
            )

            MedicationCheckButton(
                label = stringResource(R.string.med_dinner),
                isTaken = dinnerTaken,
                icon = Icons.Rounded.NightsStay,
                iconTint = DinnerIndigo,
                iconBg = DinnerIndigoBg,
                onClick = { onToggleMedication(MedicationTime.DINNER) },
                modifier = Modifier.testTag("dinner_med_button")
            )
        }

        val allMedicationsTaken = morningTaken && lunchTaken && dinnerTaken
        AnimatedVisibility(
            visible = allMedicationsTaken,
            enter = fadeIn(tween(220)) +
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) +
                expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ),
            exit = fadeOut(tween(160)) + shrinkVertically(tween(180))
        ) {
            MedicationCelebrationBanner()
        }
    }
}

@Composable
private fun MedicationCelebrationBanner() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("med_celebration_banner"),
        shape = RoundedCornerShape(24.dp),
        color = CelebrationBg,
        border = BorderStroke(2.5.dp, CelebrationBorder),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(CelebrationBadgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.EmojiEvents,
                    contentDescription = null,
                    tint = CelebrationIconTint,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = stringResource(R.string.med_celebration_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 26.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = WarmInk,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MedicationCheckButton(
    label: String,
    isTaken: Boolean,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val containerColor by animateColorAsState(
        targetValue = if (isTaken) TakenGreen else IvorySurface,
        animationSpec = tween(durationMillis = 220),
        label = "medButtonBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isTaken) TakenGreen else IvorySurfaceBorder,
        animationSpec = tween(durationMillis = 220),
        label = "medButtonBorder"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isTaken) OnTakenGreen else WarmInk,
        animationSpec = tween(durationMillis = 220),
        label = "medButtonText"
    )

    val cdText = if (isTaken) {
        stringResource(R.string.med_cd_taken, label)
    } else {
        stringResource(R.string.med_cd_pending, label)
    }

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 96.dp)
            .semantics {
                role = Role.Checkbox
                contentDescription = cdText
            },
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        border = BorderStroke(2.dp, borderColor),
        shadowElevation = if (isTaken) 4.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isTaken) Color.White.copy(alpha = 0.22f) else iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isTaken) Icons.Rounded.Check else icon,
                        contentDescription = null,
                        tint = if (isTaken) OnTakenGreen else iconTint,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = label,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = contentColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isTaken) {
                        Icons.Rounded.CheckCircle
                    } else {
                        Icons.Rounded.RadioButtonUnchecked
                    },
                    contentDescription = null,
                    tint = if (isTaken) OnTakenGreen else WarmInkSecondary,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = if (isTaken) {
                        stringResource(R.string.med_status_taken)
                    } else {
                        stringResource(R.string.med_status_pending)
                    },
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isTaken) OnTakenGreen else WarmInkSecondary
                )
            }
        }
    }
}

@Composable
private fun WaterIntakeSection(
    waterCups: Int,
    waterGoal: Int,
    onAddWaterCup: () -> Unit,
    onRemoveWaterCup: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = WaterCardBg,
        border = BorderStroke(2.dp, WaterCardBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.water_section_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = WarmInk
                )

                AnimatedContent(
                    targetState = waterCups,
                    transitionSpec = {
                        (fadeIn(tween(180)) + scaleIn(initialScale = 0.85f))
                            .togetherWith(fadeOut(tween(120)))
                    },
                    label = "waterCountAnim"
                ) { count ->
                    Text(
                        text = stringResource(R.string.water_count_format, count),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = WaterBluePrimary,
                        modifier = Modifier.testTag("water_count_text")
                    )
                }
            }

            Text(
                text = if (waterCups >= waterGoal) {
                    stringResource(R.string.water_goal_reached, waterGoal)
                } else {
                    stringResource(R.string.water_goal_format, waterGoal, waterCups)
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (waterCups >= waterGoal) TakenGreen else WarmInkSecondary
            )

            WaterCupsGrid(
                waterCups = waterCups,
                goal = waterGoal
            )

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAddWaterCup()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 84.dp)
                    .testTag("add_water_button"),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WaterBluePrimary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocalDrink,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.water_add_button),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onRemoveWaterCup()
                },
                enabled = waterCups > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 60.dp)
                    .testTag("undo_water_button"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IvoryMutedSurface,
                    contentColor = WarmInk,
                    disabledContainerColor = IvoryMutedSurface.copy(alpha = 0.55f),
                    disabledContentColor = WarmInkSecondary.copy(alpha = 0.45f)
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.RemoveCircleOutline,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.water_undo_button),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}
