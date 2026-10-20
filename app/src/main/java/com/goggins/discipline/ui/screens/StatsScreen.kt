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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goggins.discipline.ui.GogginsViewModel
import com.goggins.discipline.ui.components.NothingCard
import com.goggins.discipline.ui.theme.NothingRed
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun StatsScreen(vm: GogginsViewModel) {
    val habits by vm.ui.collectAsState()
    val progress by vm.progress.collectAsState()
    val completions by vm.allCompletions.collectAsState()
    val productivity by vm.productivity.collectAsState()
    var showStreaks by remember { mutableStateOf(false) }
    val heat = GogginsViewModel.heatmap(completions, habits.size, 28)
    val longestStreak = habits.maxOfOrNull { it.streak } ?: 0
    val totalClosings = completions.size
    val week = productivity.days.filter { runCatching { LocalDate.parse(it.date).isAfter(LocalDate.now().minusDays(7)) }.getOrDefault(false) }
    val commitmentsDone = week.sumOf { it.commitmentsDone }
    val commitmentsPlanned = week.sumOf { it.commitmentsPlanned }
    val focusMinutes = week.sumOf { it.focusMinutes }
    val proofEntries = productivity.days.filter { it.proof.isNotBlank() }.takeLast(7).reversed()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(Modifier.padding(top = 18.dp)) {
            Text("Обзор", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
            Text("Факты за неделю и привычки за 28 дней", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        NothingCard {
            Text("Неделя", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Metric("Обещания", "$commitmentsDone / $commitmentsPlanned", Modifier.weight(1f))
                Metric("Фокус", "$focusMinutes мин", Modifier.weight(1f))
            }
            if (commitmentsPlanned > 0) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { (commitmentsDone.toFloat() / commitmentsPlanned.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = NothingRed,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        if (proofEntries.isNotEmpty()) {
            NothingCard {
                Text("Банк побед", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(4.dp))
                Text("Сохраняй реальные доказательства своего прогресса.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                proofEntries.forEach { day ->
                    Spacer(Modifier.height(12.dp))
                    Text(day.date.toRussianDate(), style = MaterialTheme.typography.labelSmall, color = NothingRed)
                    Spacer(Modifier.height(3.dp))
                    Text(day.proof, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }

        NothingCard {
            Text("Привычки", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                Metric("Серия", "$longestStreak дн.", Modifier.weight(1f))
                Metric("Отметки", "$totalClosings", Modifier.weight(1f))
                Metric("Сегодня", "${(progress * 100).toInt()}%", Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))
            Text("Последние 28 дней", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            if (heat.isEmpty() || habits.isEmpty()) {
                Text("Отмечай привычки, чтобы увидеть историю.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    heat.chunked(7).forEach { weekRow ->
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            weekRow.forEach { (_, value) ->
                                Box(
                                    Modifier.size(30.dp).background(
                                        when {
                                            value >= 1f -> NothingRed
                                            value > 0f -> MaterialTheme.colorScheme.surfaceVariant
                                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f)
                                        },
                                        MaterialTheme.shapes.extraSmall
                                    )
                                )
                            }
                        }
                    }
                }
            }
            if (habits.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                TextButton(onClick = { showStreaks = !showStreaks }) {
                    Text(if (showStreaks) "Скрыть серии" else "Показать серии привычек", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (showStreaks) {
                    habits.sortedByDescending { it.streak }.forEach { habit ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(habit.habit.icon, fontSize = 18.sp)
                            Spacer(Modifier.size(10.dp))
                            Text(habit.habit.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
                            Text("${habit.streak} дн.", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(72.dp))
    }
}

@Composable
private fun Metric(title: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
    }
}

private fun String.toRussianDate(): String = runCatching {
    LocalDate.parse(this).format(DateTimeFormatter.ofPattern("d MMMM", Locale("ru")))
}.getOrDefault(this)
