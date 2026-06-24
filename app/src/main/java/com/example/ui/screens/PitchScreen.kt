package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.ui.theme.*

@Composable
fun PitchScreen() {
    var selectedSection by remember { mutableStateOf(0) }
    val sections = listOf("Competition Pitch", "System Architecture", "Roadmap & KPIs")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Soft gradient banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BabyBlue.copy(alpha = 0.4f), Color.Transparent)
                    )
                )
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    text = "RootSnoop AI Lab",
                    style = MaterialTheme.typography.titleLarge,
                    color = SlateCharcoal,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Interactive Pitch Deck & Architect Blueprints",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SlateMuted
                )
            }
        }

        // Section Selector Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(IceBlue, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            sections.forEachIndexed { index, title ->
                val isSelected = selectedSection == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) PureWhite else Color.Transparent)
                        .clickable { selectedSection = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) SlateCharcoal else SlateMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Divider(color = SoftLavender.copy(alpha = 0.5f), thickness = 1.dp)

        // Dynamic Section Renderer
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            when (selectedSection) {
                0 -> SlideDeckPitch()
                1 -> SystemArchitectureBlueprints()
                2 -> RoadmapAndKPIs()
            }
        }
    }
}

// --- SUB-SECTIONS ---

@Composable
fun SlideDeckPitch() {
    var activeSlide by remember { mutableStateOf(1) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        // Slide Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "HACKATHON COMPETITION PITCH (3-SLIDE DECK)",
                style = MaterialTheme.typography.labelSmall,
                color = SlateMuted,
                fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(
                    onClick = { if (activeSlide > 1) activeSlide-- },
                    enabled = activeSlide > 1
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Prev Slide")
                }
                Text(
                    text = "Slide $activeSlide / 3",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = SlateCharcoal,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                IconButton(
                    onClick = { if (activeSlide < 3) activeSlide++ },
                    enabled = activeSlide < 3
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Slide")
                }
            }
        }

        // Active Slide Display Card
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, SoftLavender, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            AnimatedContent(
                targetState = activeSlide,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }
                }
            ) { slide ->
                when (slide) {
                    1 -> SlideOne()
                    2 -> SlideTwo()
                    3 -> SlideThree()
                }
            }
        }
    }
}

@Composable
fun SlideOne() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            SuggestionChip(
                label = { Text("SLIDE 1: THE DISRUPTION OPPORTUNITY") },
                onClick = {},
                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = PastelPink.copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "The Haircare Paradox & Personalization Vacuum",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = SlateCharcoal
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "While skincare is overly crowded, the hair & scalp care sector remains completely underserved. Over 74% of consumers face chronic issues like dandruff, hair thinning, or split ends, yet they rely on trial-and-error purchases, random influencer advice, and harsh chemicals that worsen their scalp health.",
                style = MaterialTheme.typography.bodyLarge,
                color = SlateMuted,
                lineHeight = 24.sp
            )
        }

        Column {
            Text(
                text = "THE PROBLEM METRICS",
                style = MaterialTheme.typography.labelSmall,
                color = SlateMuted,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "₹15K+", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = ErrorCoral)
                    Text(text = "Wasted on wrong shampoos & oils", style = MaterialTheme.typography.labelSmall, color = SlateMuted)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "82%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = BabyBlueDark)
                    Text(text = "Suffer from scalp redness or thinning", style = MaterialTheme.typography.labelSmall, color = SlateMuted)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(IceBlue, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = BabyBlueDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "OPPORTUNITY: Demystify trichology using computer vision & transparent chemical audits.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateCharcoal,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SlideTwo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            SuggestionChip(
                label = { Text("SLIDE 2: THE PRODUCT SOLUTION") },
                onClick = {},
                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = SoftLavender)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "RootSnoop: AI Scalp & Hair Detective",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = SlateCharcoal
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "RootSnoop is an ultra-intuitive on-device diagnostic application that turns a single scalp/hair photo into a clinically tailored, 360-degree wellness plan. Powered by Google Gemini, RootSnoop evaluates scalp flakiness, maps localized concerns, schedules custom washing regimens, audits sulfate levels, and recommends budget-friendly alternatives.",
                style = MaterialTheme.typography.bodyMedium,
                color = SlateMuted,
                lineHeight = 20.sp
            )
        }

        Column {
            Text(
                text = "CORE ARCHITECTURE ADVANTAGES",
                style = MaterialTheme.typography.labelSmall,
                color = SlateMuted,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "Gemini AI Trichologist" to Icons.Default.AutoAwesome,
                    "Chemical Safety Guard" to Icons.Default.Security,
                    "Alternative Cost Saver" to Icons.Default.Savings
                ).forEach { (text, icon) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(SoftCream, RoundedCornerShape(8.dp))
                            .border(1.dp, SoftLavender, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(icon, contentDescription = null, tint = BabyBlueDark, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = text, style = MaterialTheme.typography.labelSmall, color = SlateCharcoal, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Text(
            text = "🏆 HACKATHON VALUE PROPOSITION: Saves money, blocks follicle-clogging silicones, and restores self-confidence with transparent, brand-agnostic insights.",
            style = MaterialTheme.typography.bodySmall,
            color = SlateMuted,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SlideThree() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            SuggestionChip(
                label = { Text("SLIDE 3: GO-TO-MARKET & MONETIZATION") },
                onClick = {},
                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = PastelGreen)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Market Penetration & Strategic Revenue Hooks",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = SlateCharcoal
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "We generate revenue frictionlessly through high-intent affiliate referrals (Amazon, Nykaa, Sephora), premium dermatologist booking commissions, and localized anonymous hair-concern analytics licensing to safe hair-care startups.",
                style = MaterialTheme.typography.bodyMedium,
                color = SlateMuted,
                lineHeight = 20.sp
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.dp, SoftLavender),
                colors = CardDefaults.cardColors(containerColor = SoftCream)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Affiliate Referral Commissions", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = SlateCharcoal)
                    Text(text = "10-15% commission from direct product buy links mapped inside the app.", style = MaterialTheme.typography.bodySmall, color = SlateMuted)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.dp, SoftLavender),
                colors = CardDefaults.cardColors(containerColor = SoftCream)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Premium Consultant Gigs", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = SlateCharcoal)
                    Text(text = "₹250 per virtual 1-on-1 trichologist booking and personalized custom formulation.", style = MaterialTheme.typography.bodySmall, color = SlateMuted)
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = ErrorCoral)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "DEPLOYMENT STACK: Lovable UI, Google Gemini (REST Vision API), local Room Database (SQLite on-device backup).",
                style = MaterialTheme.typography.bodySmall,
                color = SlateCharcoal,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SystemArchitectureBlueprints() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "RootSnoop Full Architecture Schema",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SlateCharcoal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Clinical-grade AI pipeline from on-device capture to Google Gemini cloud reasoning.",
                style = MaterialTheme.typography.bodySmall,
                color = SlateMuted
            )
        }

        // ASCII Diagram Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateCharcoal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "DATA FLOW DIAGRAM", style = MaterialTheme.typography.labelSmall, color = SoftLavender, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.DeveloperMode, contentDescription = null, tint = BabyBlue, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = """
  [Camera Scan / Preset Picker]
                  │
                  ▼
  [Jetpack Compose UI (RootSnoop App)] ──► Local Room DB (SQL cache)
                  │
        (HTTPS Post with API Key)
                  │
                  ▼
  [Google Gemini API] ──► Multi-Modal Scalp & Follicle Audit
                  │
        (Validates JSON Response)
                  │
                  ▼
  [RootSnoop VM / State Renderer] ──► Updates Routines & Remedies
                        """.trimIndent(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = PastelGreen,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Database Schema Definition
        item {
            Text(
                text = "SQL Room Database Schema",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = SlateCharcoal
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, SoftLavender),
                colors = CardDefaults.cardColors(containerColor = PureWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "TABLE: skin_analysis", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = BabyBlueDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(
                        "id" to "INTEGER (PRIMARY KEY, AUTOINCREMENT)",
                        "timestamp" to "INTEGER (BigInt epoch of scan date)",
                        "imageSourceUri" to "TEXT (Path or demo hair catalog identifier)",
                        "report" to "TEXT (Moshi serialized SkinReport JSON object with Hair & Scalp metrics)",
                        "notes" to "TEXT (User progress journal entry)"
                    ).forEach { (column, type) ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text(text = "• $column", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SlateCharcoal, modifier = Modifier.weight(1.2f))
                            Text(text = type, style = MaterialTheme.typography.bodySmall, color = SlateMuted, modifier = Modifier.weight(2f))
                        }
                    }
                }
            }
        }

        // REST API Details
        item {
            Text(
                text = "Trichology AI API Integration",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = SlateCharcoal
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, SoftLavender),
                colors = CardDefaults.cardColors(containerColor = PureWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SuggestionChip(label = { Text("POST") }, onClick = {})
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "/v1beta/models/gemini-1.5-flash:generateContent", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = SlateCharcoal)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "API Key Auth: Passed securely as BuildConfig.GEMINI_API_KEY", style = MaterialTheme.typography.bodySmall, color = SlateMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Payload: Multimodal request with base64 compressed JPEG and structured JSON formatting prompt.", style = MaterialTheme.typography.bodySmall, color = SlateMuted)
                }
            }
        }
    }
}

@Composable
fun RoadmapAndKPIs() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Production Roadmap & KPI Framework",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SlateCharcoal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tactical strategy to scale RootSnoop from MVP to market launch.",
                style = MaterialTheme.typography.bodySmall,
                color = SlateMuted
            )
        }

        // Timeline Roadmap Cards
        item {
            Text(text = "PRODUCTION ROADMAP", style = MaterialTheme.typography.labelSmall, color = SlateMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            listOf(
                "Phase 1: Hackathon MVP" to "Jetpack Compose prototype containing full local Room DB simulation + direct REST Gemini model connectivity. COMPLETED.",
                "Phase 2: Lovable & Replit Sync" to "Host Python FastAPI backend on Replit with SQLite cache. Integrate real Affiliate APIs from Nykaa, Sephora, and Amazon Product Advertising API.",
                "Phase 3: Clinical Trials" to "Onboard 50 certified trichologists to validate Gemini diagnostic findings, fine-tuning prompt weights and safety exclusions.",
                "Phase 4: Store Launch" to "Deploy native Android APK on Google Play Store with dynamic App Check security, targeting organic beauty-influencer marketing campaign."
            ).forEach { (phase, desc) ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    border = BorderStroke(1.dp, SoftLavender),
                    colors = CardDefaults.cardColors(containerColor = PureWhite)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            imageVector = if (phase.contains("MVP")) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (phase.contains("MVP")) BabyBlueDark else SlateMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = phase, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = SlateCharcoal)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = SlateMuted, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }

        // KPI framework
        item {
            Text(text = "KPI METRICS TO MEASURE SUCCESS", style = MaterialTheme.typography.labelSmall, color = SlateMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "48%" to "D1 Retention Rate",
                    "₹580" to "Target LTV / User",
                    "21.4%" to "Product Click-Thru"
                ).forEach { (value, metric) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, SoftLavender),
                        colors = CardDefaults.cardColors(containerColor = SoftCream)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = BabyBlueDark)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = metric, style = MaterialTheme.typography.labelSmall, color = SlateCharcoal, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
