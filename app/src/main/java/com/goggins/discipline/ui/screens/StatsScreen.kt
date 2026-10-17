package com.goggins.discipline.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goggins.discipline.ui.GogginsViewModel
import com.goggins.discipline.ui.components.DotMatrix
import com.goggins.discipline.ui.components.GlyphHeader
import com.goggins.discipline.ui.components.NothingCard
import com.goggins.discipline.ui.theme.NothingRed

@Composable
fun StatsScreen(vm: GogginsViewModel) {
    val list by vm.ui.collectAsState()
    val progress by vm.progress.collectAsState()
    val all by vm.allCompletions.collectAsState()
    val heat = GogginsViewModel.heatmap(all, list.size, 28)
    val best = list.maxOfOrNull { it.streak } ?: 0
    val totalClosings = all.size

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        run {
            Spacer(Modifier.height(12.dp))
            GlyphHeader("СТАТИСТИКА · DATA", "NO LIES")
            DotMatrix()
        }
        run {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatBox("СТРИК МАКС", "$best ДН.", Modifier.weight(1f))
                StatBox("ПРИВЫЧЕК", "${list.size}", Modifier.weight(1f))
                StatBox("СЕГОДНЯ", "${(progress * 100).toInt()}%", Modifier.weight(1f))
            }
        }
        run {
            NothingCard {
                Text("ВСЕГО ЗАКРЫТИЙ · $totalClosings", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Text(
                    if (best >= 7) "Неделя без пропусков. Гоггинс кивает."
                    else if (totalClosings == 0) "Ноль. Зеркало всё видело. Начни сегодня."
                    else "Продолжай. Стрик строится днями, а не настроением.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        run {
            NothingCard {
                Text("ТЕПЛОВАЯ КАРТА · 28 ДНЕЙ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                if (heat.isEmpty() || list.isEmpty()) {
                    Text("Нет данных. Начни отмечать привычки — карта заполнится.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        heat.chunked(7).forEach { week ->
                            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                week.forEach { (_, v) ->
                                    Box(
                                        Modifier.size(30.dp).background(
                                            when {
                                                v >= 1f -> NothingRed
                                                v >= 0.6f -> Color(0xFF7A1015)
                                                v >= 0.3f -> Color(0xFF3A3A3A)
                                                v > 0f -> Color(0xFF222222)
                                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                            },
                                            MaterialTheme.shapes.extraSmall
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    // Легенда цветовыми чипами вместо ASCII-символов.
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.size(10.dp).background(NothingRed, MaterialTheme.shapes.extraSmall))
                        Text("ПРОВАЛ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(8.dp))
                        Box(Modifier.size(10.dp).background(Color(0xFF3A3A3A), MaterialTheme.shapes.extraSmall))
                        Text("ЧАСТИЧНО", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(8.dp))
                        Box(Modifier.size(10.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), MaterialTheme.shapes.extraSmall))
                        Text("STAY HARD", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        run {
            GlyphHeader("СТРИКИ · STREAKS", "${list.size} ШТ.")
            if (list.isEmpty()) {
                NothingCard { Text("Пока пусто. Стрик строится днями без пропусков.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                list.sortedByDescending { it.streak }.forEach { h ->
                    NothingCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(h.habit.icon, fontSize = 18.sp)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(h.habit.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                                Spacer(Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { (h.streak / 30f).coerceIn(0f, 1f) },
                                    color = NothingRed,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth().height(4.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text("${h.streak}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
            Spacer(Modifier.height(96.dp))
        }
    }
}

@Composable
private fun StatBox(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier.background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
    }
}
