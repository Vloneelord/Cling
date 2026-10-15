package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface

/**
 * Modern Glassmorphic Container component locked to Sahara Night Dark Theme.
 * Features:
 * - Elevated Navy Card Surface (`Color(0xFF111827)`)
 * - Compose blur API (Modifier.blur) for frosted glass diffusion
 * - Crisp 1dp razor-sharp glass highlight border (`BorderStroke` / `Color(0xFF334155)`)
 * - Subtle shadow elevation for layer depth
 */
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    blurRadius: Dp = 16.dp,
    elevation: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    val resolvedBg = backgroundColor ?: CardSurface
    val resolvedBorder = borderColor ?: BorderStroke

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = Color.Black.copy(alpha = 0.6f)
            )
    ) {
        // Frosted blur backdrop layer in Sahara Night tones
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .blur(radius = blurRadius)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xD9111827),
                            Color(0xEB070B14)
                        )
                    )
                )
        )

        // Crisp translucent surface & highlight border
        Surface(
            shape = shape,
            color = resolvedBg,
            border = BorderStroke(1.dp, resolvedBorder),
            modifier = Modifier
        ) {
            content()
        }
    }
}
