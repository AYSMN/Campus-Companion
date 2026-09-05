package com.example.ui.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalIsDark
import com.example.ui.theme.TimeGradients
import java.util.Calendar

enum class TimeOfDayPeriod {
    MORNING,
    AFTERNOON,
    EVENING,
    NIGHT
}

fun getCurrentTimePeriod(): TimeOfDayPeriod {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> TimeOfDayPeriod.MORNING
        in 12..16 -> TimeOfDayPeriod.AFTERNOON
        in 17..20 -> TimeOfDayPeriod.EVENING
        else -> TimeOfDayPeriod.NIGHT
    }
}

@Composable
fun TimeOfDayBackground(
    modifier: Modifier = Modifier,
    isDark: Boolean = LocalIsDark.current,
    content: @Composable BoxScope.() -> Unit
) {
    val period = remember { getCurrentTimePeriod() }

    val gradientColors = when (period) {
        TimeOfDayPeriod.MORNING -> if (isDark) TimeGradients.MorningDark else TimeGradients.MorningLight
        TimeOfDayPeriod.AFTERNOON -> if (isDark) TimeGradients.AfternoonDark else TimeGradients.AfternoonLight
        TimeOfDayPeriod.EVENING -> if (isDark) TimeGradients.EveningDark else TimeGradients.EveningLight
        TimeOfDayPeriod.NIGHT -> if (isDark) TimeGradients.NightDark else TimeGradients.NightLight
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_offset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = gradientColors
                )
            )
            .drawBehind {
                // Ambient luminous glass glow orbs
                val orb1Color = if (isDark) Color(0x333B82F6) else Color(0x2893C5FD)
                val orb2Color = if (isDark) Color(0x268B5CF6) else Color(0x22F472B6)
                val orb3Color = if (isDark) Color(0x2610B981) else Color(0x226EE7B7)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(orb1Color, Color.Transparent),
                        center = Offset(size.width * 0.85f + animatedOffset * 0.1f, size.height * 0.15f),
                        radius = size.width * 0.6f
                    )
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(orb2Color, Color.Transparent),
                        center = Offset(size.width * 0.15f - animatedOffset * 0.1f, size.height * 0.65f),
                        radius = size.width * 0.7f
                    )
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(orb3Color, Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 0.9f),
                        radius = size.width * 0.5f
                    )
                )
            }
    ) {
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    shape: Shape = RoundedCornerShape(cornerRadius),
    elevation: Dp = 8.dp,
    borderAlpha: Float = 0.35f,
    surfaceAlpha: Float = 0.55f,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = LocalIsDark.current

    val surfaceColor = if (isDark) {
        Color(0xFF1E293B).copy(alpha = surfaceAlpha)
    } else {
        Color(0xFFFFFFFF).copy(alpha = 0.82f)
    }

    val topBorderColor = if (isDark) Color.White.copy(alpha = borderAlpha) else Color.White.copy(alpha = 0.9f)
    val bottomBorderColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0).copy(alpha = 0.5f)

    val borderBrush = Brush.verticalGradient(
        colors = listOf(topBorderColor, bottomBorderColor)
    )

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.4f) else Color(0xFF64748B).copy(alpha = 0.12f),
                spotColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color(0xFF3B82F6).copy(alpha = 0.18f)
            )
            .clip(shape)
            .background(surfaceColor)
            .border(
                border = BorderStroke(1.dp, borderBrush),
                shape = shape
            )
            .then(clickableModifier)
    ) {
        content()
    }
}

@Composable
fun GlassBadge(
    text: String,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    val isDark = LocalIsDark.current
    val bg = accentColor.copy(alpha = if (isDark) 0.22f else 0.14f)
    val border = accentColor.copy(alpha = if (isDark) 0.45f else 0.35f)

    Box(
        modifier = modifier
            .clip(shape)
            .background(bg)
            .border(BorderStroke(1.dp, border), shape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        androidx.compose.material3.Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (isDark) Color.White else accentColor,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        )
    }
}
