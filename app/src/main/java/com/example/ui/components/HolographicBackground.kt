package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyanCore
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.HoloBorder

@Composable
fun HolographicBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "holo_anim")
    val scanlineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanline"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpace)
    ) {
        // Holographic Cybernetic Grid & Radial Aura
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Top Cyan Luminous Orb Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x2200E5FF),
                        Color(0x0C38BDF8),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.5f, height * 0.15f),
                    radius = width * 0.65f
                ),
                radius = width * 0.65f,
                center = Offset(width * 0.5f, height * 0.15f)
            )

            // Bottom Cyber Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x186366F1),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.5f, height * 0.9f),
                    radius = width * 0.5f
                ),
                radius = width * 0.5f,
                center = Offset(width * 0.5f, height * 0.9f)
            )

            // Grid Mesh
            val step = 48f
            var x = 0f
            while (x <= width) {
                drawLine(
                    color = Color(0x0900E5FF),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += step
            }

            var y = 0f
            while (y <= height) {
                drawLine(
                    color = Color(0x0900E5FF),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += step
            }

            // Sweeping Holographic Scanline
            val scanY = height * scanlineOffset
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x2600E5FF),
                        Color(0x6600E5FF),
                        Color(0x2600E5FF),
                        Color.Transparent
                    )
                ),
                start = Offset(0f, scanY),
                end = Offset(width, scanY),
                strokeWidth = 2f
            )
        }

        content()
    }
}
