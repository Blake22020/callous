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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(Modifier.height(12.dp))
            GlyphHeader("СТАТИСТИКА // DATA", "NO LIES")
            DotMatrix()
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatBox("СТРИК МАКС", "$best ДН.", Modifier.weight(1f))
                StatBox("ПРИВЫЧЕК", "${list.size}", Modifier.weight(1f))
                StatBox("СЕГОДНЯ", "${(progress * 100).toInt()}%", Modifier.weight(1f))
            }
        }
        item {
            NothingCard {
                Text("ВСЕГО ЗАКРЫТИЙ: $totalClosings", fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    if (best >= 7) "Неделя без пропусков. Гоггинс кивает."
                    else if (totalClosings == 0) "Ноль. Зеркало всё видело. Начни сегодня."
                    else "Продолжай. Стрик строится днями, а не настроением.",
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 12.sp, color = Color.Gray
                )
            }
        }
        item {
            NothingCard {
                Text("ТЕПЛОВАЯ КАРТА // 28 ДНЕЙ", fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 11.sp, color = NothingRed)
                Spacer(Modifier.height(8.dp))
                if (heat.isEmpty() || list.isEmpty()) {
                    Text("Нет данных. Начни отмечать привычки — карта заполнится.", fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 12.sp)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        heat.chunked(7).forEach { week ->
                            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                week.forEach { (_, v) ->
                                    Box(
                                        Modifier.size(34.dp).background(
                                            when {
                                                v >= 1f -> NothingRed
                                                v >= 0.6f -> Color(0xFF7A1015)
                                                v >= 0.3f -> Color(0xFF3A3A3A)
                                                v > 0f -> Color(0xFF222222)
                                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                            },
                                            RoundedCornerShape(6.dp)
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("░ ПРОВАЛ ▓ ЧАСТИЧНО █ STAY HARD", fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
        item {
            GlyphHeader("СТРИКИ // STREAKS", "${list.size} ШТ.")
            if (list.isEmpty()) {
                NothingCard { Text("Пока пусто. Стрик строится днями без пропусков.", fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 12.sp) }
            } else {
                list.sortedByDescending { it.streak }.forEach { h ->
                    NothingCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(h.habit.icon, fontSize = 18.sp)
                            Spacer(Modifier.padding(4.dp))
                            Column(Modifier.weight(1f)) {
                                Text(h.habit.title.uppercase(), fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                val f = (h.streak / 30f).coerceIn(0.03f, 1f)
                                Box(Modifier.fillMaxWidth().height(6.dp).background(Color.Gray.copy(0.25f), RoundedCornerShape(3.dp))) {
                                    Box(Modifier.fillMaxWidth(f).height(6.dp).background(NothingRed, RoundedCornerShape(3.dp)))
                                }
                            }
                            Text("${h.streak}", fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontWeight = FontWeight.Black, fontSize = 22.sp, color = NothingRed)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
            Spacer(Modifier.height(90.dp))
        }
    }
}

@Composable
private fun StatBox(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 10.sp, color = Color.Gray)
        Text(value, fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontWeight = FontWeight.Black, fontSize = 18.sp)
    }
}
