package com.kappdev.wordbook.study_feature.presentation.common.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kappdev.wordbook.theme.ProgressGradient

@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier,
    colors: List<Color> = ProgressGradient,
    trackColor: Brush = SolidColor(MaterialTheme.colorScheme.onSurface.copy(0.16f)),
    strokeWidth: Dp = 4.dp,
    glowRadius: Dp? = 4.dp,
    strokeCap: StrokeCap = StrokeCap.Round
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val scaleX = if (isRtl) -1f else 1f

    val progressAnim by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(720, easing = LinearOutSlowInEasing),
        label = "ProgressChangeAnimation"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "GradientTransition")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "GradientOffset"
    )

    val brush: ShaderBrush = remember(offset) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val step = 1f / colors.size
                val start = step / 2

                val originalSpots = List(colors.size) { start + (step * it) }
                val transformedSpots = originalSpots.map { spot ->
                    val shiftedSpot = (spot + offset)
                    if (shiftedSpot > 1f) shiftedSpot - 1f else shiftedSpot
                }

                val pairs = colors.zip(transformedSpots).sortedBy { it.second }

                val margin = size.width * step
                return LinearGradientShader(
                    colors = pairs.map { it.first },
                    colorStops = pairs.map { it.second },
                    from = Offset(-margin, 0f),
                    to = Offset(size.width + margin, 0f)
                )
            }
        }
    }

    Canvas(modifier.scale(scaleX, 1f)) {
        val width = this.size.width
        val height = this.size.height

        val paint = Paint().apply {
            this.isAntiAlias = true
            this.style = PaintingStyle.Stroke
            this.strokeWidth = strokeWidth.toPx()
            this.strokeCap = strokeCap
            this.shader = brush.createShader(size)
        }

        glowRadius?.let { radius ->
            paint.asFrameworkPaint().apply {
                setShadowLayer(radius.toPx(), 0f, 0f, android.graphics.Color.WHITE)
            }
        }

        drawLine(
            brush = trackColor,
            start =  Offset(0f, height / 2f),
            end = Offset(width, height / 2f),
            cap = strokeCap,
            strokeWidth = strokeWidth.toPx()
        )

        if (progressAnim > 0f) {
            drawIntoCanvas { canvas ->
                canvas.drawLine(
                    p1 = Offset(0f, height / 2f),
                    p2 = Offset(width * progressAnim, height / 2f),
                    paint = paint
                )
            }
        }
    }
}