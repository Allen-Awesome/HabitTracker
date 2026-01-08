package com.habit.tracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 参考 VegetableOrderUI 的配色方案
object AppColors {
    // 主色调 - 温暖的金色系
    val gold = Color(0xFFEE8D3C)
    val goldLight = Color(0xFFFEE4A2)
    
    // 背景色
    val ghostWhite = Color(0xFFF5F6F8)
    val paleWhite = Color(0xFFF3F7F9)
    
    // 卡片背景色
    val navajoWhite = Color(0xFFFEE4A2)  // 暖黄
    val water = Color(0xFFDCEDFF)         // 浅蓝
    val lightBlue = Color(0xFFE5F0FC)     // 淡蓝
    val mintCream = Color(0xFFE8F5E9)     // 薄荷绿
    val lavender = Color(0xFFF3E5F5)      // 淡紫
    val peachPuff = Color(0xFFFFE0B2)     // 桃色
    
    // 文字色
    val textPrimary = Color(0xFF222325)
    val textSecondary = Color(0xFF6B7280)
    val textHint = Color(0xFFABACAD)
    
    // 功能色
    val success = Color(0xFF4CAF50)
    val info = Color(0xFF2196F3)
    val warning = Color(0xFFFF9800)
    val error = Color(0xFFF44336)
}

private val LightColorScheme = lightColorScheme(
    primary = AppColors.gold,
    onPrimary = Color.White,
    primaryContainer = AppColors.goldLight,
    onPrimaryContainer = Color(0xFF5D3A00),
    secondary = Color(0xFF5B9BD5),
    onSecondary = Color.White,
    secondaryContainer = AppColors.water,
    background = AppColors.ghostWhite,
    surface = Color.White,
    surfaceVariant = AppColors.paleWhite,
    onBackground = AppColors.textPrimary,
    onSurface = AppColors.textPrimary,
    onSurfaceVariant = AppColors.textSecondary,
    outline = AppColors.textHint,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB74D),
    onPrimary = Color(0xFF3E2723),
    primaryContainer = Color(0xFF5D4037),
    onPrimaryContainer = Color(0xFFFFE0B2),
    secondary = Color(0xFF90CAF9),
    onSecondary = Color(0xFF0D47A1),
    secondaryContainer = Color(0xFF1565C0),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2C2C2C),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF757575),
)

@Composable
fun HabitTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // 关闭动态颜色以使用自定义配色
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) Color(0xFF121212).toArgb() else AppColors.ghostWhite.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
