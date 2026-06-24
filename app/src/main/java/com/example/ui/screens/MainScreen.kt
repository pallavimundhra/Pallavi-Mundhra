package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuraSkinViewModel

@Composable
fun MainScreen(viewModel: AuraSkinViewModel) {
    var selectedTab by remember { mutableStateOf(0) }

    val navItems = listOf(
        NavigationItem(
            title = "Diagnostics",
            selectedIcon = Icons.Filled.AutoAwesome,
            unselectedIcon = Icons.Outlined.AutoAwesome,
            testTag = "tab_diagnostics"
        ),
        NavigationItem(
            title = "Routine",
            selectedIcon = Icons.Filled.CalendarMonth,
            unselectedIcon = Icons.Outlined.CalendarMonth,
            testTag = "tab_timeline"
        ),
        NavigationItem(
            title = "Products",
            selectedIcon = Icons.Filled.ShoppingBag,
            unselectedIcon = Icons.Outlined.ShoppingBag,
            testTag = "tab_products"
        ),
        NavigationItem(
            title = "Remedies",
            selectedIcon = Icons.Filled.Eco,
            unselectedIcon = Icons.Outlined.Eco,
            testTag = "tab_remedies"
        ),
        NavigationItem(
            title = "Pitch & Lab",
            selectedIcon = Icons.Filled.MenuBook,
            unselectedIcon = Icons.Outlined.MenuBook,
            testTag = "tab_pitch"
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = PureWhite,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                tint = if (isSelected) BabyBlueDark else SlateMuted
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                                color = if (isSelected) SlateCharcoal else SlateMuted
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = SoftLavender
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> AnalyzeScreen(viewModel)
                1 -> RoutineScreen(viewModel)
                2 -> ProductsScreen(viewModel)
                3 -> RemediesScreen(viewModel)
                4 -> PitchScreen()
            }
        }
    }
}

data class NavigationItem(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)
