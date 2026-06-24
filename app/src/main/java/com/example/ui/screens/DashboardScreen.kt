package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SkinAnalysisEntity
import com.example.data.SkinReport
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuraSkinViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: AuraSkinViewModel) {
    val activeReport by viewModel.activeReport.collectAsState()
    val completedMorning by viewModel.completedMorningSteps.collectAsState()
    val completedEvening by viewModel.completedEveningSteps.collectAsState()
    val history by viewModel.analysesHistory.collectAsState()
    val isDarkModeOverride by viewModel.isDarkMode.collectAsState(initial = null)
    val useDarkTheme = isDarkModeOverride ?: isSystemInDarkTheme()

    var selectedHistoryEntity by remember { mutableStateOf<SkinAnalysisEntity?>(null) }
    var journalText by remember { mutableStateOf("") }
    var showUpdateBanner by remember { mutableStateOf(false) }

    // Synchronize journal text field when selected history item changes
    LaunchedEffect(selectedHistoryEntity) {
        selectedHistoryEntity?.let {
            journalText = it.notes
        } ?: run {
            if (history.isNotEmpty()) {
                selectedHistoryEntity = history.first()
                journalText = history.first().notes
            }
        }
    }

    // fallback if no items are in history but we have active report
    LaunchedEffect(history) {
        if (selectedHistoryEntity == null && history.isNotEmpty()) {
            selectedHistoryEntity = history.first()
            journalText = history.first().notes
        }
    }

    val report = activeReport

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. Dashboard Welcome Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                PastelPink.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📈 Hair & Scalp Progress",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearAllHistory() },
                            modifier = Modifier.testTag("clear_history_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset History",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        IconButton(
                            onClick = { viewModel.toggleDarkMode(useDarkTheme) },
                            modifier = Modifier.testTag("dark_mode_toggle_dashboard")
                        ) {
                            Icon(
                                imageVector = if (useDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text = "Track your hair improvements, analyze routine checklist adherence, and record daily scalp observations.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 2. Interactive Streak / Checklist Completion Status
        item {
            if (report != null) {
                val totalSteps = report.routines.morning.size + report.routines.evening.size
                val completedSteps = completedMorning.size + completedEvening.size
                val progress = if (totalSteps > 0) completedSteps.toFloat() / totalSteps else 0f
                val percentage = (progress * 100).toInt()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("streak_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(PastelGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✨", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Daily Habit Streak",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "Routine Completion Rate",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "$percentage%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "☀️ Morning: ${completedMorning.size}/${report.routines.morning.size} done",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "🌙 Evening: ${completedEvening.size}/${report.routines.evening.size} done",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (percentage == 100) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(PastelGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🎉 Perfect day! All routines are complete!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateCharcoal,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Score Trend Line Chart
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("trend_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Mane Health Score Trend",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Historical progress curve mapping overall follicle & scalp health",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (history.size < 2) {
                        // Beautiful explanatory mockup chart when there are not enough items
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Timeline,
                                    contentDescription = "Trend graph illustration",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Trend graph starts automatically!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "Analyze your scalp & hair 2 or more times to draw interactive progress curves.",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                    } else {
                        // Plot actual Room database scores
                        val sortedHistory = history.sortedBy { it.timestamp }
                        val scores = sortedHistory.map { it.report.skinScore }
                        val maxScore = 100f
                        val minScore = 0f

                        val primaryColor = MaterialTheme.colorScheme.primary
                        val outlineColor = MaterialTheme.colorScheme.outline

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            val width = size.width
                            val height = size.height
                            val sizeCount = scores.size
                            val stepX = width / (sizeCount - 1).coerceAtLeast(1)

                            // Draw horizontal reference lines (50, 75, 100)
                            val line50Y = height - (50f / maxScore) * height
                            val line75Y = height - (75f / maxScore) * height
                            val line100Y = height - (100f / maxScore) * height

                            drawLine(
                                color = outlineColor.copy(alpha = 0.4f),
                                start = Offset(0f, line50Y),
                                end = Offset(width, line50Y),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawLine(
                                color = outlineColor.copy(alpha = 0.4f),
                                start = Offset(0f, line75Y),
                                end = Offset(width, line75Y),
                                strokeWidth = 1.dp.toPx()
                            )

                            // Generate path points
                            val points = mutableListOf<Offset>()
                            for (i in scores.indices) {
                                val x = i * stepX
                                val normalizedScore = scores[i].toFloat() / maxScore
                                val y = height - (normalizedScore * height)
                                points.add(Offset(x, y))
                            }

                            // Draw gradient area below line
                            if (points.isNotEmpty()) {
                                val fillPath = Path().apply {
                                    moveTo(points.first().x, height)
                                    points.forEach { lineTo(it.x, it.y) }
                                    lineTo(points.last().x, height)
                                    close()
                                }
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            primaryColor.copy(alpha = 0.3f),
                                            Color.Transparent
                                        )
                                    )
                                )
                            }

                            // Draw line
                            if (points.isNotEmpty()) {
                                val strokePath = Path().apply {
                                    moveTo(points.first().x, points.first().y)
                                    for (i in 1 until points.size) {
                                        lineTo(points[i].x, points[i].y)
                                    }
                                }
                                drawPath(
                                    path = strokePath,
                                    color = primaryColor,
                                    style = Stroke(width = 3.dp.toPx())
                                )
                            }

                            // Draw points & labels
                            points.forEachIndexed { index, point ->
                                drawCircle(
                                    color = Color.White,
                                    radius = 6.dp.toPx(),
                                    center = point
                                )
                                drawCircle(
                                    color = primaryColor,
                                    radius = 4.dp.toPx(),
                                    center = point
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Horizontally scrollable list of historical dates & score nodes
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(sortedHistory) { item ->
                                val dateStr = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(item.timestamp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (item.id == selectedHistoryEntity?.id) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (item.id == selectedHistoryEntity?.id) MaterialTheme.colorScheme.primary
                                            else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            selectedHistoryEntity = item
                                            journalText = item.notes
                                        }
                                        .padding(8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = dateStr,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Text(
                                            text = "Mane Score: ${item.report.skinScore}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Skin Concerns Progression (Active list based on current selection or latest)
        val selectedReport = selectedHistoryEntity?.report ?: report
        if (selectedReport != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("concerns_progression_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Scalp & Hair Concerns",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Detected foliage and scalp metrics from snoop scans",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedReport.concerns.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No active concerns reported! Outstanding scalp & hair health. 🌟",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            selectedReport.concerns.forEach { concern ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = concern.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    when (concern.severity.lowercase()) {
                                                        "high" -> ErrorCoral.copy(alpha = 0.2f)
                                                        "medium" -> PastelPink.copy(alpha = 0.5f)
                                                        else -> PastelGreen.copy(alpha = 0.5f)
                                                    },
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = concern.severity,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Black,
                                                color = when (concern.severity.lowercase()) {
                                                    "high" -> Color(0xFFC53030)
                                                    "medium" -> Color(0xFFC05621)
                                                    else -> Color(0xFF2F855A)
                                                }
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        LinearProgressIndicator(
                                            progress = { concern.percentage / 100f },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = when (concern.severity.lowercase()) {
                                                "high" -> ErrorCoral
                                                "medium" -> BabyBlueDark
                                                else -> BabyBlue
                                            },
                                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${concern.percentage}%",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Intermittent Skin Diary Notes persistence section
        if (history.isNotEmpty() && selectedHistoryEntity != null) {
            val entity = selectedHistoryEntity!!
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("journal_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "Journal icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                        Text(
                                            text = "Hair & Scalp Journal",
                                            style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                val format = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault())
                                Text(
                                    text = "Scan from: ${format.format(Date(entity.timestamp))}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = journalText,
                            onValueChange = { journalText = it },
                            placeholder = { Text("Write down sleep hours, stress levels, wash days, or overall hair & scalp state...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .testTag("journal_notes_input"),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.updateAnalysisNotes(entity.id, journalText)
                                showUpdateBanner = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("save_journal_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text(
                                text = "Save Diary Entry",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        AnimatedVisibility(
                            visible = showUpdateBanner,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                                    .background(PastelGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = Color(0xFF2F855A),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Diary entry updated successfully!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateCharcoal,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            LaunchedEffect(showUpdateBanner) {
                                if (showUpdateBanner) {
                                    kotlinx.coroutines.delay(2000)
                                    showUpdateBanner = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
