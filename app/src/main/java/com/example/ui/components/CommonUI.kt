package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassBackground
import com.example.ui.theme.GlassBorder

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(1.dp, GlassBorder, RoundedCornerShape(cornerRadius))
            .shadow(16.dp, RoundedCornerShape(cornerRadius), spotColor = Color(0x33000000))
            .clip(RoundedCornerShape(cornerRadius)),
        colors = CardDefaults.cardColors(containerColor = GlassBackground)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun PulseEffect(
    modifier: Modifier = Modifier,
    color: Color = Color.Red,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .scale(scale)
                .background(color.copy(alpha = alpha), shape = RoundedCornerShape(percent = 50))
        )
        content()
    }
}

fun Modifier.scale(scale: Float) = this.then(
    Modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
)
