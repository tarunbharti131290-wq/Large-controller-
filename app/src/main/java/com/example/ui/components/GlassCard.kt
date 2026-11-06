package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanCore
import com.example.ui.theme.HoloBorder
import com.example.ui.theme.HoloSurface80

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderColor: Color = HoloBorder,
    glowEffect: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    val borderStroke = if (glowEffect) {
        BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    CyanCore.copy(alpha = 0.8f),
                    HoloBorder,
                    CyanCore.copy(alpha = 0.3f)
                )
            )
        )
    } else {
        BorderStroke(width = 1.dp, color = borderColor)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(HoloSurface80)
            .border(borderStroke, shape)
            .padding(14.dp)
    ) {
        content()
    }
}
