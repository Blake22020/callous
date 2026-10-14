package com.goggins.discipline.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goggins.discipline.ui.theme.Mono
import com.goggins.discipline.ui.theme.NothingRed
import kotlinx.coroutines.delay

/** Заголовок в духе Nothing: моноширинный, капс, красная точка. */
@Composable
fun GlyphHeader(title: String, sub: String = "", modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = "$title",
            fontFamily = Mono,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (sub.isNotEmpty()) {
            Text(
                text = sub,
                fontFamily = Mono,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
        // красная точка Nothing
        Canvas(modifier = Modifier.align(Alignment.TopEnd).padding(top = 4.dp)) {
            drawCircle(NothingRed, radius = 8f)
        }
    }
}

/** Точечная матрица-разделитель в стиле Nothing glyph. */
@Composable
fun DotMatrix(modifier: Modifier = Modifier, dots: Int = 48) {
    Canvas(modifier.fillMaxWidth().height(10.dp).alpha(0.5f)) {
        val step = size.width / dots
        for (i in 0 until dots) {
            drawCircle(Color.Gray, radius = 2f, center = androidx.compose.ui.geometry.Offset(i * step + step / 2, size.height / 2))
        }
    }
}

/** Карточка с тонкой рамкой, как корпус Nothing Phone. */
@Composable
fun NothingCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) { content() }
}

/** Тряска для экрана «наказания». */
suspend fun shakeIt(offset: Animatable<Float, *> ) {
    val seq = listOf(18f, -18f, 12f, -12f, 7f, -7f, 0f)
    for (x in seq) {
        offset.animateTo(x, animationSpec = tween(60))
        delay(30)
    }
}

@Composable
fun ShakeBox(shakeKey: Int, content: @Composable () -> Unit) {
    val off = remember(shakeKey) { Animatable(0f) }
    LaunchedEffect(shakeKey) { if (shakeKey > 0) shakeIt(off) }
    Box(Modifier.graphicsLayer { translationX = off.value }) { content() }
}
