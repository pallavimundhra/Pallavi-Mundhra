package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.res.painterResource
import com.example.R
import coil.compose.rememberAsyncImagePainter
import com.example.data.SkinAnalysisEntity
import com.example.data.SkinReport
import com.example.ui.theme.*
import com.example.ui.viewmodel.AnalysisUiState
import com.example.ui.viewmodel.AuraSkinViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnalyzeScreen(viewModel: AuraSkinViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val activeReport by viewModel.activeReport.collectAsState()
    val history by viewModel.analysesHistory.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var activeDemoIndex by remember { mutableStateOf<Int?>(null) }

    val demoProfiles = listOf(
        "Oily Scalp & Dandruff Active" to Brush.verticalGradient(listOf(PastelPink, SoftLavender)),
        "Dry Scalp & Hair Thinning Risk" to Brush.verticalGradient(listOf(IceBlue, SoftLavender)),
        "Curly Hair & Severe Frizz/Damage" to Brush.verticalGradient(listOf(PastelGreen, SoftLavender)),
        "Normal / Healthy Standard" to Brush.verticalGradient(listOf(PureWhite, IceBlue))
    )

    // Activity launcher for choosing an image from gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            activeDemoIndex = null
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                // Convert to software config to ensure compatibility with Gemini
                val softwareBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                selectedBitmap = softwareBitmap
                viewModel.triggerSkinAnalysis(softwareBitmap, "User Uploaded Hair Photo")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Core header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(BabyBlue.copy(alpha = 0.3f), Color.Transparent)))
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon_rootsnoop_1782272932532),
                        contentDescription = "RootSnoop Logo",
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, BabyBlue, RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "🕵️‍♂️ RootSnoop",
                            style = MaterialTheme.typography.headlineMedium,
                            color = SlateCharcoal,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Snooping out oily roots, dry scalps & weak hair strands! 👀",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Action controls (Pick image or Demo presets)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main Photo Upload Action Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_card")
                        .clickable { imagePickerLauncher.launch("image/*") },
                    border = BorderStroke(1.5.dp, BabyBlue.copy(alpha = 0.6f)),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (selectedImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = "User Scalp/Hair",
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, BabyBlue, CircleShape)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Hair Photo Uploaded! Tap to change.",
                                style = MaterialTheme.typography.labelMedium,
                                color = BabyBlueDark,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(IceBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Camera Icon",
                                    tint = BabyBlueDark,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Upload Scalp/Hair Photo",
                                style = MaterialTheme.typography.titleMedium,
                                color = SlateCharcoal,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Supports JPG & PNG of your hair/scalp from device storage",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Demo preset header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f), color = SoftLavender.copy(alpha = 0.6f))
                    Text(
                        text = " 🔎 SNOOP PRESET HAIR PROFILE ",
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateMuted,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Divider(modifier = Modifier.weight(1f), color = SoftLavender.copy(alpha = 0.6f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Grid list of presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    demoProfiles.take(2).forEachIndexed { index, (label, brush) ->
                        val isPresetActive = activeDemoIndex == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(brush)
                                .border(
                                    width = if (isPresetActive) 2.dp else 1.dp,
                                    color = if (isPresetActive) BabyBlueDark else SoftLavender,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    activeDemoIndex = index
                                    selectedImageUri = null
                                    selectedBitmap = null
                                    viewModel.triggerSkinAnalysis(null, label)
                                }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateCharcoal,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    demoProfiles.drop(2).forEachIndexed { index, (label, brush) ->
                        val actualIndex = index + 2
                        val isPresetActive = activeDemoIndex == actualIndex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(brush)
                                .border(
                                    width = if (isPresetActive) 2.dp else 1.dp,
                                    color = if (isPresetActive) BabyBlueDark else SoftLavender,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    activeDemoIndex = actualIndex
                                    selectedImageUri = null
                                    selectedBitmap = null
                                    viewModel.triggerSkinAnalysis(null, label)
                                }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateCharcoal,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Live status analyzer renderer
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                when (val state = uiState) {
                    is AnalysisUiState.Analyzing -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, SoftLavender),
                            colors = CardDefaults.cardColors(containerColor = PureWhite)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = BabyBlueDark)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "🕵️‍♂️ RootSnoop is snooping your follicles...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = SlateCharcoal
                                )
                                Text(
                                    text = "Sniffing out dandruff yeast, sebum buildup, and broken cuticles! 🔎",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    is AnalysisUiState.Error -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, ErrorCoral),
                            colors = CardDefaults.cardColors(containerColor = PastelPink.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Error, contentDescription = "Error", tint = ErrorCoral)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = state.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateCharcoal,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }
        }

        // Main Dashboard Success Results
        activeReport?.let { report ->
            item {
                Text(
                    text = " 🎯 SNOOP CASE DOSSIER: FOUND!",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateMuted,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            // Overall Score Arc Indicator
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, SoftLavender),
                    colors = CardDefaults.cardColors(containerColor = PureWhite)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Circular score meter
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .background(IceBlue, CircleShape)
                                .border(2.dp, SoftLavender, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${report.skinScore}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = if (report.skinScore > 80) PastelGreen.darken() else if (report.skinScore > 65) BabyBlueDark else ErrorCoral
                                )
                                Text(
                                    text = "ROOT FORCE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        // Skin Type Details
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(BabyBlue.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${report.skinType} Profile",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Black,
                                        color = SlateCharcoal
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Confidence: ${report.skinTypeScore}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = report.skinTypeDescription,
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateCharcoal,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Concerns list
            item {
                Text(
                    text = " 🚨 TARGETS UNDER INVESTIGATION (${report.concerns.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateMuted,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            items(report.concerns) { concern ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, SoftLavender),
                    colors = CardDefaults.cardColors(containerColor = PureWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            color = when (concern.severity) {
                                                "High" -> ErrorCoral
                                                "Medium" -> BabyBlue
                                                else -> PastelGreen
                                            },
                                            shape = CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = concern.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateCharcoal
                                )
                            }

                            // Severity badge
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = when (concern.severity) {
                                            "High" -> PastelPink
                                            "Medium" -> IceBlue
                                            else -> PastelGreen.copy(alpha = 0.5f)
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${concern.severity} Severity",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateCharcoal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = concern.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateMuted
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        // Progress bar representation
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Intensity Score",
                                style = MaterialTheme.typography.labelSmall,
                                color = SlateMuted,
                                modifier = Modifier.width(90.dp)
                            )
                            LinearProgressIndicator(
                                progress = concern.percentage / 100f,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = when (concern.severity) {
                                    "High" -> ErrorCoral
                                    "Medium" -> BabyBlueDark
                                    else -> PastelGreen.darken()
                                },
                                trackColor = SoftCream
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${concern.percentage}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = SlateCharcoal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Scans History log section (Room database verification)
        if (history.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = " 📁 PAST SNOOP FILES (${history.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Clear All",
                        style = MaterialTheme.typography.labelSmall,
                        color = ErrorCoral,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.clearAllHistory() }
                    )
                }
            }

            items(history) { entity ->
                val dateStr = remember(entity.timestamp) {
                    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                    sdf.format(Date(entity.timestamp))
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { viewModel.selectHistoryReport(entity.report) },
                    border = BorderStroke(1.dp, SoftLavender.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = PureWhite)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = BabyBlueDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${entity.report.skinType} - Crown Score ${entity.report.skinScore}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateCharcoal
                                )
                                Text(
                                    text = "$dateStr (${entity.imageSourceUri})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SlateMuted
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.deleteHistoryItem(entity.id) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = ErrorCoral,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Extension to darken colors
fun Color.darken(): Color {
    return Color(
        red = (this.red * 0.7f).coerceIn(0f, 1f),
        green = (this.green * 0.7f).coerceIn(0f, 1f),
        blue = (this.blue * 0.7f).coerceIn(0f, 1f),
        alpha = this.alpha
    )
}
