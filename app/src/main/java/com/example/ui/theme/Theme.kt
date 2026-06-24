package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = BabyBlue,
    onPrimary = SlateCharcoal,
    primaryContainer = IceBlue,
    onPrimaryContainer = BabyBlueDark,
    secondary = SoftLavender,
    onSecondary = SlateCharcoal,
    secondaryContainer = PastelPink,
    onSecondaryContainer = SlateCharcoal,
    tertiary = PastelGreen,
    onTertiary = SlateCharcoal,
    background = SoftCream,
    onBackground = SlateCharcoal,
    surface = PureWhite,
    onSurface = SlateCharcoal,
    surfaceVariant = IceBlue,
    onSurfaceVariant = SlateMuted,
    outline = SoftLavender,
    error = ErrorCoral
)

private val DarkColorScheme = darkColorScheme(
    primary = BabyBlue,
    onPrimary = SlateCharcoal,
    primaryContainer = BabyBlueDarkTheme,
    onPrimaryContainer = BabyBlue,
    secondary = IceBlueDarkTheme,
    onSecondary = PureWhite,
    background = MutedBlueDarkTheme,
    onBackground = PureWhite,
    surface = BabyBlueDarkTheme,
    onSurface = PureWhite,
    surfaceVariant = IceBlueDarkTheme,
    onSurfaceVariant = SoftLavender
)

@Composable
fun AuraSkinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic colors to preserve the custom hand-crafted Baby Blue & White brand identity
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
