package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanCore
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.Typography
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HolographicOrb(
    isProcessing: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_rot")

    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isProcessing) 3500 else 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_rot"
    )

    val innerRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isProcessing) 2400 else 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_rot"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isProcessing) 600 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_pulse"
    )

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveform_phase"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
                .testTag("sara_holographic_orb"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(136.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val baseRadius = size.width * 0.44f

                // Outer Telemetry Ring with Segments
                rotate(degrees = outerRotation, pivot = center) {
                    drawCircle(
                        color = Color(0x2200E5FF),
                        radius = baseRadius,
                        center = center,
                        style = Stroke(width = 1.5f)
                    )

                    // Arc segments
                    drawArc(
                        color = CyanCore,
                        startAngle = 15f,
                        sweepAngle = 60f,
                        useCenter = false,
                        topLeft = Offset(center.x - baseRadius, center.y - baseRadius),
                        size = Size(baseRadius * 2, baseRadius * 2),
                        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = CyanAccent,
                        startAngle = 135f,
                        sweepAngle = 80f,
                        useCenter = false,
                        topLeft = Offset(center.x - baseRadius, center.y - baseRadius),
                        size = Size(baseRadius * 2, baseRadius * 2),
                        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = NeonIndigo,
                        startAngle = 255f,
                        sweepAngle = 70f,
                        useCenter = false,
                        topLeft = Offset(center.x - baseRadius, center.y - baseRadius),
                        size = Size(baseRadius * 2, baseRadius * 2),
                        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                    )

                    // Tick marks
                    for (i in 0 until 12) {
                        val angle = (i * 30 * PI / 180).toFloat()
                        val r1 = baseRadius - 5f
                        val r2 = baseRadius + 4f
                        val p1 = Offset(center.x + r1 * cos(angle), center.y + r1 * sin(angle))
                        val p2 = Offset(center.x + r2 * cos(angle), center.y + r2 * sin(angle))
                        drawLine(
                            color = if (i % 3 == 0) CyanCore else Color(0x6600E5FF),
                            start = p1,
                            end = p2,
                            strokeWidth = if (i % 3 == 0) 2f else 1f
                        )
                    }
                }

                // Inner Counter-Rotating Ring
                val innerRadius = baseRadius * 0.72f
                rotate(degrees = innerRotation, pivot = center) {
                    drawCircle(
                        color = Color(0x3338BDF8),
                        radius = innerRadius,
                        center = center,
                        style = Stroke(width = 1.2f)
                    )

                    drawArc(
                        color = Color(0xAA00E5FF),
                        startAngle = 0f,
                        sweepAngle = 45f,
                        useCenter = false,
                        topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                        size = Size(innerRadius * 2, innerRadius * 2),
                        style = Stroke(width = 2f, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = Color(0xAA6366F1),
                        startAngle = 180f,
                        sweepAngle = 45f,
                        useCenter = false,
                        topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                        size = Size(innerRadius * 2, innerRadius * 2),
                        style = Stroke(width = 2f, cap = StrokeCap.Round)
                    )
                }

                // Audio Reactive / Processing Spikes
                if (isProcessing) {
                    for (i in 0 until 24) {
                        val angle = (i * 15 * PI / 180).toFloat()
                        val waveOffset = (sin(angle * 3 + wavePhase) + 1f) * 12f
                        val r1 = innerRadius + 2f
                        val r2 = innerRadius + 6f + waveOffset
                        val p1 = Offset(center.x + r1 * cos(angle), center.y + r1 * sin(angle))
                        val p2 = Offset(center.x + r2 * cos(angle), center.y + r2 * sin(angle))
                        drawLine(
                            color = Color(0xCC00E5FF),
                            start = p1,
                            end = p2,
                            strokeWidth = 2f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // Central Luminous Glowing Core
                val coreRadius = baseRadius * 0.42f * pulseScale
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            CyanCore,
                            Color(0x8800E5FF),
                            Color(0x2200E5FF),
                            Color.Transparent
                        ),
                        center = center,
                        radius = coreRadius * 1.5f
                    ),
                    radius = coreRadius * 1.5f,
                    center = center
                )

                // Diamond Neural Reticle in Center
                val dSize = 10f * pulseScale
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(center.x, center.y - dSize)
                    lineTo(center.x + dSize, center.y)
                    lineTo(center.x, center.y + dSize)
                    lineTo(center.x - dSize, center.y)
                    close()
                }
                drawPath(path, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isProcessing) "SARA // PROCESSING DIRECTIVE..." else "SARA AI CORE // LINKED",
            style = Typography.labelMedium,
            color = if (isProcessing) CyanCore else EmeraldOnline
        )
    }
}
