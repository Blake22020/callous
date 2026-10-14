package com.goggins.discipline.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goggins.discipline.ui.theme.NothingRed

/** Кольцо прогресса дня. Анимировано. В центре — процент и подпись 40% RULE. */
@Composable
fun ProgressRing(progress: Float, size: Dp = 148.dp, label: String = "") {
    val animated = progress
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
        val track = MaterialTheme.colorScheme.surfaceVariant
        Canvas(Modifier.size(size)) {
            drawArc(track, -90f, 360f, false, style = Stroke(14f, cap = StrokeCap.Butt))
            drawArc(NothingRed, -90f, 360f * animated.coerceIn(0f, 1f), false, style = Stroke(14f, cap = StrokeCap.Square))
            // белая риска-стрелка сверху
            drawArc(
                androidx.compose.ui.graphics.Color.White, -91.5f, 3f, false,
                style = Stroke(14f, cap = StrokeCap.Butt)
            )
        }
        androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${(animated * 100).toInt()}%",
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontWeight = FontWeight.Black, fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                label.ifEmpty { "40% RULE: ${(animated * 40).toInt()}% ОТ МАКС." },
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
