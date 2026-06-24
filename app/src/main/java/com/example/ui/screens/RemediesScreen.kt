package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuraSkinViewModel

@Composable
fun RemediesScreen(viewModel: AuraSkinViewModel) {
    val activeReport by viewModel.activeReport.collectAsState()
    val isDarkModeOverride by viewModel.isDarkMode.collectAsState(initial = null)
    val useDarkTheme = isDarkModeOverride ?: isSystemInDarkTheme()
    val report = activeReport

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Remedies Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(SoftLavender.copy(alpha = 0.3f), Color.Transparent)))
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🧪 DIY Hair & Scalp Kitchen",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Trichologist-approved kitchen potions to instantly soothe your active hair & scalp concerns! 🥗",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { viewModel.toggleDarkMode(useDarkTheme) },
                        modifier = Modifier.testTag("dark_mode_toggle_remedies")
                    ) {
                        Icon(
                            imageVector = if (useDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
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
                        color = SlateMuted
                    )
                }
            }
        } else {
            item {
                Text(
                    text = " RECOMMENDED FOR YOUR SCALP CONCERNS",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateMuted,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            items(report.homeRemedies) { remedy ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("remedy_card_${remedy.name}"),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, SoftLavender),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(PastelGreen, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Spa, contentDescription = null, tint = PastelGreen.darken())
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = remedy.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = SlateCharcoal
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Ingredients list
                        Text(
                            text = "REQUIRED KITCHEN INGREDIENTS:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SlateMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        remedy.ingredients.forEach { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Eco, contentDescription = null, tint = PastelGreen.darken(), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = item, style = MaterialTheme.typography.bodySmall, color = SlateCharcoal)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Instructions
                        Text(
                            text = "PREPARATION & APPLICATION:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SlateMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = remedy.instructions,
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateCharcoal,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Benefits
                        Text(
                            text = "EXPECTED HAIR/SCALP BENEFITS:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SlateMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = remedy.expectedBenefits,
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateMuted,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Safety Precautions
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PastelPink.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = ErrorCoral, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "SAFETY PRECAUTION:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateCharcoal
                                    )
                                    Text(
                                        text = remedy.precautions,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SlateCharcoal,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
