package com.goggins.discipline.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.goggins.discipline.data.content.Challenges
import com.goggins.discipline.ui.GogginsViewModel
import com.goggins.discipline.ui.components.DotMatrix
import com.goggins.discipline.ui.components.GlyphHeader
import com.goggins.discipline.ui.components.NothingCard
import com.goggins.discipline.ui.theme.Mono
import com.goggins.discipline.ui.theme.NothingRed

@Composable
fun ChallengesScreen(vm: GogginsViewModel) {
    val state by vm.challengeState.collectAsState()
    val byId = state.associateBy { it.challengeId }

    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Spacer(Modifier.height(12.dp))
            GlyphHeader("ИСПЫТАНИЯ // HELL", "${Challenges.all.size} ШТ.")
            DotMatrix()
            NothingCard {
                Text(
                    "Правила Гоггинса: сорвался — начинай с дня 1. Без переговоров. Отмечай дни кнопкой. Дойди до конца.",
                    fontFamily = Mono, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        items(Challenges.all, key = { it.id }) { c ->
            val done = byId[c.id]?.daysDone ?: 0
            val finished = byId[c.id]?.finished == true
            NothingCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(c.title, fontFamily = Mono, fontWeight = FontWeight.Black, fontSize = 15.sp)
                        Text("☠".repeat(c.skulls) + " // ${c.days} ДН.", fontFamily = Mono, fontSize = 11.sp, color = NothingRed)
                        Spacer(Modifier.height(4.dp))
                        Text(c.description, fontFamily = Mono, fontSize = 12.sp, color = Color.Gray)
                        Spacer(Modifier.height(4.dp))
                        Text("ЗАКОН: ${c.rule}", fontFamily = Mono, fontSize = 11.sp)
                        Spacer(Modifier.height(8.dp))
                        // прогресс-бар
                        val f = if (c.days == 0) 0f else done.toFloat() / c.days
                        androidx.compose.foundation.layout.Box(
                            Modifier.fillMaxWidth().height(8.dp)
                                .background(Color.Gray.copy(0.25f), RoundedCornerShape(4.dp))
                        ) {
                            androidx.compose.foundation.layout.Box(
                                Modifier.fillMaxWidth(f).height(8.dp)
                                    .background(if (finished) Color(0xFF3DDC84) else NothingRed, RoundedCornerShape(4.dp))
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (finished) "ЗАКРЫТО. ТЫ ЗВЕРЬ. STAY HARD."
                            else "ДЕНЬ $done / ${c.days}",
                            fontFamily = Mono, fontSize = 11.sp,
                            color = if (finished) Color(0xFF3DDC84) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { vm.toggleChallengeDay(c.id) },
                        enabled = !finished,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.background,
                            disabledContainerColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("+1 ДЕНЬ", fontFamily = Mono) }
                    OutlinedButton(onClick = { vm.resetChallenge(c.id) }, shape = RoundedCornerShape(8.dp)) {
                        Text("РЕСТАРТ", fontFamily = Mono)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(90.dp)) }
    }
}
