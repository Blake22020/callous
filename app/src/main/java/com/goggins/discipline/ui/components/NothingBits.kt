package com.goggins.discipline.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goggins.discipline.ui.theme.NothingRed
import kotlinx.coroutines.delay

/** Заголовок секции в духе Nothing: спокойный вес, тонкая подпись, одна красная точка. */
@Composable
fun GlyphHeader(title: String, sub: String = "", modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().padding(top = 18.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = 0.5.sp,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        if (sub.isNotEmpty()) {
            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
        // Единственная красная точка Nothing — акцент, а не заливка.
        Box(Modifier.padding(start = 6.dp).size(5.dp).background(NothingRed, CircleShape))
    }
}

/** Точечная матрица-разделитель — фирменный акцент Nothing, приглушённый. */
@Composable
fun DotMatrix(modifier: Modifier = Modifier, dots: Int = 44) {
    Canvas(modifier.fillMaxWidth().height(9.dp).alpha(0.35f)) {
        val step = size.width / dots
        for (i in 0 until dots) {
            drawCircle(Color.Gray, radius = 1.5f, center = androidx.compose.ui.geometry.Offset(i * step + step / 2, size.height / 2))
        }
    }
}

/**
 * Карточка на тональной поверхности Material 3 — без рамок.
 * Раньше на каждой карточке была 1dp-обводка, из-за чего интерфейс выглядел
 * как сетка коробок; тональный уровень даёт глубину мягче и по-настоящему нулевой.
 */
@Composable
fun NothingCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
            .padding(horizontal = 16.dp, vertical = 16.dp)
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
