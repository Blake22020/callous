package com.goggins.discipline.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.goggins.discipline.data.content.Challenges
import com.goggins.discipline.ui.GogginsViewModel
import com.goggins.discipline.ui.components.NothingCard
import com.goggins.discipline.ui.theme.NothingGreen
import com.goggins.discipline.ui.theme.NothingRed

@Composable
fun ChallengesScreen(vm: GogginsViewModel) {
    val state by vm.challengeState.collectAsState()
    val byId = state.associateBy { it.challengeId }
    var expandedId by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(Modifier.padding(top = 18.dp, bottom = 4.dp)) {
            Text("Вызовы", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
            Text("Выбери один. Отмечай выполненный день.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Challenges.all.forEach { challenge ->
            val progress = byId[challenge.id]?.daysDone ?: 0
            val finished = byId[challenge.id]?.finished == true
            val expanded = expandedId == challenge.id
            NothingCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(challenge.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.height(3.dp))
                        Text("${challenge.days} дней · ${challenge.skulls} уровень", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(if (finished) "Готово" else "$progress / ${challenge.days}", style = MaterialTheme.typography.labelLarge, color = if (finished) NothingGreen else MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { if (challenge.days == 0) 0f else (progress.toFloat() / challenge.days).coerceIn(0f, 1f) },
                    color = if (finished) NothingGreen else NothingRed,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth().height(4.dp)
                )
                TextButton(onClick = { expandedId = if (expanded) null else challenge.id }) {
                    Text(if (expanded) "Скрыть детали" else "Описание и правила", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (expanded) {
                    Text(challenge.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(8.dp))
                    Text("Правило: ${challenge.rule}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { vm.toggleChallengeDay(challenge.id) },
                            enabled = !finished,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onBackground,
                                contentColor = MaterialTheme.colorScheme.background,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = MaterialTheme.shapes.small
                        ) { Text("Отметить день") }
                        OutlinedButton(onClick = { vm.resetChallenge(challenge.id) }, shape = MaterialTheme.shapes.small) {
                            Text("Начать заново")
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(72.dp))
    }
}
