package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.data.Product
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuraSkinViewModel

@Composable
fun ProductsScreen(viewModel: AuraSkinViewModel) {
    val context = LocalContext.current
    val activeReport by viewModel.activeReport.collectAsState()
    val isDarkModeOverride by viewModel.isDarkMode.collectAsState(initial = null)
    val useDarkTheme = isDarkModeOverride ?: isSystemInDarkTheme()

    var activeTier by remember { mutableStateOf(0) } // 0: Budget, 1: Standard, 2: Premium
    val tierLabels = listOf("💰 Budget Plan", "💎 Standard Plan", "✨ Premium Plan")

    val report = activeReport

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Core Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(PastelGreen.copy(alpha = 0.3f), Color.Transparent)))
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🛒 Loot & Snoop Market",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Secrets & hair elixirs curated for your scalp and budget! Direct buy on click. 💸",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { viewModel.toggleDarkMode(useDarkTheme) },
                        modifier = Modifier.testTag("dark_mode_toggle_products")
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
            // Plan Tier Selector
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(IceBlue, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tierLabels.forEachIndexed { index, label ->
                        val isSelected = activeTier == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PureWhite else Color.Transparent)
                                .clickable { activeTier = index }
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

            // Products rendering
            val productsList = when (activeTier) {
                0 -> report.recommendations.budget
                1 -> report.recommendations.standard
                else -> report.recommendations.premium
            }

            if (productsList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No specific products listed for this tier.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateMuted
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = " RECOMMENDED FOR YOU",
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateMuted,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }

                // Horizontal product carousel
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(productsList) { product ->
                            Card(
                                modifier = Modifier
                                    .width(280.dp)
                                    .testTag("product_card_${product.name}"),
                                border = BorderStroke(1.dp, SoftLavender),
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    // Header: Brand & Marketplace
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = product.brand.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            color = BabyBlueDark
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(IceBlue, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = product.marketplace,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = SlateCharcoal
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateCharcoal,
                                        maxLines = 1
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Key ingredients
                                    Text(
                                        text = "Active: ${product.keyIngredients}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SlateMuted,
                                        maxLines = 1
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Price & Suitability Meter
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = product.price,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Black,
                                            color = SlateCharcoal
                                        )

                                        // Suitability pill
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .background(PastelGreen, RoundedCornerShape(12.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Favorite,
                                                contentDescription = null,
                                                tint = PastelGreen.darken(),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${product.suitabilityScore}% Match",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Black,
                                                color = SlateCharcoal
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Buy Portal Button (Real Web Link Intent!)
                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.buyLink))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("buy_button_${product.name}"),
                                        colors = ButtonDefaults.buttonColors(containerColor = BabyBlue),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = SlateCharcoal, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = "Buy on ${product.marketplace}", color = SlateCharcoal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Ingredient Intelligence Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = " INGREDIENT INTELLIGENCE LAB",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateMuted,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            // Beneficial Ingredients List
            item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                border = BorderStroke(1.dp, SoftLavender),
                colors = CardDefaults.cardColors(containerColor = PureWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PastelGreen.darken())
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Beneficial Actives Identified",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SlateCharcoal
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        report.ingredientsSafety.beneficialIngredients.forEach { insight ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = insight.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateCharcoal
                                    )
                                    Text(
                                        text = insight.role,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BabyBlueDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = insight.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateMuted
                                )
                                Divider(modifier = Modifier.padding(top = 6.dp), color = SoftCream)
                            }
                        }
                    }
                }
            }

            // Warnings Ingredients List
            item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                border = BorderStroke(1.dp, SoftLavender),
                colors = CardDefaults.cardColors(containerColor = PureWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorCoral)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Scalp Clogging / Sulfate Warnings",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SlateCharcoal
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        report.ingredientsSafety.harmfulIngredients.forEach { insight ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = insight.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateCharcoal
                                    )
                                    Text(
                                        text = "Avoid",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ErrorCoral,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = insight.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateMuted
                                )
                                Divider(modifier = Modifier.padding(top = 6.dp), color = SoftCream)
                            }
                        }
                    }
                }
            }

            // Cost Optimizer Tiers Comparison table
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = " COST OPTIMIZER SAVINGS PORTAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateMuted,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            items(report.budgetAlternatives) { alternative ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, SoftLavender),
                    colors = CardDefaults.cardColors(containerColor = SoftCream)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Equivalence Match",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = SlateMuted
                            )
                            Box(
                                modifier = Modifier
                                    .background(PastelGreen, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Savings, contentDescription = null, tint = PastelGreen.darken(), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Save ${alternative.saving}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = SlateCharcoal
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "PREMIUM PORTFOLIO", style = MaterialTheme.typography.labelSmall, color = SlateMuted)
                                Text(text = alternative.premiumProduct, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SlateCharcoal)
                            }
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = BabyBlueDark, modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "BUDGET DUPE", style = MaterialTheme.typography.labelSmall, color = BabyBlueDark, fontWeight = FontWeight.Bold)
                                Text(text = alternative.budgetProduct, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SlateCharcoal)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Trichological Reason: ${alternative.reason}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateMuted
                        )
                    }
                }
            }
        }
    }
}
