package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.data.SkincareRoutineStep
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuraSkinViewModel

@Composable
fun RoutineScreen(viewModel: AuraSkinViewModel) {
    val activeReport by viewModel.activeReport.collectAsState()
    val completedMorning by viewModel.completedMorningSteps.collectAsState()
    val completedEvening by viewModel.completedEveningSteps.collectAsState()
    val completedWeekly by viewModel.completedWeeklySteps.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Morning, 1: Evening, 2: Weekly
    val tabLabels = listOf("☀️ Morning", "🌙 Evening", "🗓️ Weekly Plan")

    val report = activeReport

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Banner Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(PastelPink.copy(alpha = 0.3f), Color.Transparent)))
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Column {
                    Text(
                        text = "⏰ Crown Checklist",
                        style = MaterialTheme.typography.headlineMedium,
                        color = SlateCharcoal,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Personalized hair & scalp battle plan! Tick off each step to level up your crown score. 🚀",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateMuted
                    )
                }
            }
        }

        if (report == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active report. Please run a scan first on the Diagnostics tab.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Combined Progress Tracking Dashboard
            item {
                val totalSteps = report.routines.morning.size + report.routines.evening.size
                val completedSteps = completedMorning.size + completedEvening.size
                val progress = if (totalSteps > 0) completedSteps.toFloat() / totalSteps else 0f
                val percentage = (progress * 100).toInt()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    border = BorderStroke(1.dp, SoftLavender),
                    colors = CardDefaults.cardColors(containerColor = PureWhite)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Today's Routine Adherence",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SlateCharcoal
                                )
                                Text(
                                    text = "$completedSteps of $totalSteps steps completed",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateMuted
                                )
                            }
                            Text(
                                text = "$percentage%",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = BabyBlueDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dynamic Custom Advice Tag based on Adherence
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(IceBlue, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = when {
                                    percentage == 100 -> "🏆 Masterful! Your hair roots thank you for full compliance today."
                                    percentage >= 50 -> "✨ Magnificent effort! Keep it up to lock in crown hydration and strength."
                                    completedSteps > 0 -> "🌱 A fresh start! One gentle scalp rinse is better than none."
                                    else -> "⏳ Time to treat your scalp! Tap into your personalized morning or evening steps below."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateCharcoal,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = BabyBlueDark,
                            trackColor = SoftCream
                        )
                    }
                }
            }

            // Tab toggles
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .background(SoftCream, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabLabels.forEachIndexed { index, label ->
                        val isSelected = activeTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PureWhite else Color.Transparent)
                                .clickable { activeTab = index }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) SlateCharcoal else SlateMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Timeline details list
            val stepsList = when (activeTab) {
                0 -> report.routines.morning
                1 -> report.routines.evening
                else -> report.routines.weekly
            }

            if (stepsList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No care steps configured for this section.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateMuted
                        )
                    }
                }
            } else {
                items(stepsList) { step ->
                    val isCompleted = when (activeTab) {
                        0 -> completedMorning.contains(step.stepNumber)
                        1 -> completedEvening.contains(step.stepNumber)
                        else -> completedWeekly.contains(step.stepNumber)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .testTag("step_${step.stepNumber}")
                            .clickable {
                                when (activeTab) {
                                    0 -> viewModel.toggleMorningStep(step.stepNumber)
                                    1 -> viewModel.toggleEveningStep(step.stepNumber)
                                    else -> viewModel.toggleWeeklyStep(step.stepNumber)
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCompleted) IceBlue.copy(alpha = 0.5f) else PureWhite
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isCompleted) BabyBlue.copy(alpha = 0.5f) else SoftLavender
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Completion check button
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = if (isCompleted) BabyBlueDark else SoftCream,
                                        shape = CircleShape
                                    )
                                    .border(1.5.dp, if (isCompleted) BabyBlueDark else SoftLavender, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Completed",
                                        tint = PureWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${step.stepNumber}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Step Instruction details
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = step.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isCompleted) SlateMuted else SlateCharcoal
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isCompleted) SoftLavender else SoftLavender.copy(alpha = 0.5f),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = step.frequency,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Medium,
                                            color = SlateCharcoal
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Target Product: ${step.productType}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BabyBlueDark,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = step.instruction,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateMuted,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
