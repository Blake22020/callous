package com.goggins.discipline.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goggins.discipline.data.content.Quotes
import com.goggins.discipline.data.settings.AppSettings
import com.goggins.discipline.ui.GogginsViewModel
import com.goggins.discipline.ui.HabitUi
import com.goggins.discipline.ui.ProgressRing
import com.goggins.discipline.ui.components.DotMatrix
import com.goggins.discipline.ui.components.GlyphHeader
import com.goggins.discipline.ui.components.NothingCard
import com.goggins.discipline.ui.components.ShakeBox
import com.goggins.discipline.ui.theme.Mono
import com.goggins.discipline.ui.theme.NothingRed
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: GogginsViewModel, settings: AppSettings, onSettings: () -> Unit) {
    val list by vm.ui.collectAsState()
    val progress by vm.progress.collectAsState()
    val verdict by vm.verdict.collectAsState()
    val verdictBad by vm.verdictBad.collectAsState()
    val shakeKey by vm.shakeKey.collectAsState()
    val note by vm.noteText.collectAsState()

    var showAdd by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newIcon by remember { mutableStateOf("◼") }
    var noteDraft by remember(note) { mutableStateOf(note) }

    val dateStr = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) }
    val dailyQuote = remember { Quotes.daily(System.currentTimeMillis() / 86_400_000L) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background
            ) { Icon(Icons.Default.Add, "Добавить привычку") }
        }
    ) { pad ->
        LazyColumn(
            Modifier.fillMaxSize().padding(pad).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "GOGGINS.OS",
                            fontFamily = Mono, fontWeight = FontWeight.Black, fontSize = 13.sp,
                            letterSpacing = 3.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            if (settings.userName.isBlank()) "STAY HARD." else "STAY HARD, ${settings.userName.uppercase()}.",
                            fontFamily = Mono, fontWeight = FontWeight.Black, fontSize = 26.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            "// $dateStr // НИКТО НЕ ПРИДЁТ ТЕБЯ СПАСАТЬ",
                            fontFamily = Mono, fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, "Настройки", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
                DotMatrix()
            }

            item {
                NothingCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProgressRing(progress)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            // Без AnimatedContent: в LazyColumn он схлопывал высоту и текст налезал на соседей.
                            Text(
                                when {
                                    list.isEmpty() -> "НЕТ ПРИВЫЧЕК.\nДОБАВЬ ПЕРВУЮ."
                                    progress >= 1f -> "ВСЁ ЗАКРЫТО.\nТЫ ЗВЕРЬ."
                                    progress >= 0.6f -> "ДАВИ ДАЛЬШЕ.\nОСТАЛОСЬ НЕМНОГО."
                                    progress > 0f -> "НАЧАЛО ЕСТЬ.\nНЕ СМЕЙ ОСТАНАВЛИВАТЬСЯ."
                                    else -> "НОЛЬ.\nЗЕРКАЛО ВСЁ ВИДЕЛО."
                                },
                                fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(Modifier.height(6.dp))
                            val done = list.count { it.done }
                            Text(
                                "[$done/${list.size}] ЗАКРЫТО",
                                fontFamily = Mono, fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(6.dp))
                            // ASCII-бар
                            val filled = (progress * 20).toInt()
                            Text(
                                "█".repeat(filled) + "░".repeat(20 - filled),
                                fontFamily = Mono, fontSize = 11.sp, color = NothingRed
                            )
                        }
                    }
                }
            }

            item {
                NothingCard {
                    Text("◉ ЦИТАТА ДНЯ", fontFamily = Mono, fontSize = 11.sp, color = NothingRed)
                    Spacer(Modifier.height(4.dp))
                    Text("«${dailyQuote.text}»", fontFamily = Mono, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
                    Text("— ${dailyQuote.source}", fontFamily = Mono, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            item {
                GlyphHeader("СЕГОДНЯ // TODAY", "${list.count { it.done }}/${list.size}")
            }

            if (list.isEmpty()) {
                item {
                    NothingCard {
                        Text(
                            "Пусто. Гоггинс презрительно смотрит на эту пустоту.\nНажми + и добавь первую привычку: бег, отжимания, чтение, холодный душ.",
                            fontFamily = Mono, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            items(list, key = { it.habit.id }) { h ->
                HabitRow(h, onToggle = { vm.toggle(h, settings.profanity) }, onDelete = { vm.deleteHabit(h.habit) })
            }

            item {
                GlyphHeader("ЗЕРКАЛО // ACCOUNTABILITY", "ЧЕСТНО")
                NothingCard {
                    BasicTextField(
                        value = noteDraft,
                        onValueChange = { noteDraft = it },
                        modifier = Modifier.fillMaxWidth().height(90.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = Mono, fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        decorationBox = { inner ->
                            if (noteDraft.isEmpty()) Text(
                                "Где сегодня сдался? Где победил? Пиши правду...",
                                fontFamily = Mono, fontSize = 12.sp, color = Color.Gray
                            )
                            inner()
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { vm.saveNote(noteDraft) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.background
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("ЗАПИСАТЬ // SAVE", fontFamily = Mono) }
                }
                Spacer(Modifier.height(90.dp))
            }
        }
    }

    if (showAdd) {
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("НОВАЯ ПРИВЫЧКА", fontFamily = Mono, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTitle, onValueChange = { newTitle = it },
                        label = { Text("Название: бег 5км", fontFamily = Mono) }, singleLine = true
                    )
                    Text("ГЛИФ // ICON", fontFamily = Mono, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("◼", "●", "▲", "◆", "✕", "☰", "⚡", "⛓").forEach { g ->
                            Box(
                                Modifier.size(40.dp)
                                    .border(
                                        1.dp,
                                        if (newIcon == g) NothingRed else MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { newIcon = g },
                                contentAlignment = Alignment.Center
                            ) { Text(g, fontSize = 18.sp) }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.addHabit(newTitle, newIcon)
                    newTitle = ""; newIcon = "◼"; showAdd = false
                }) { Text("ЕБАШ // ADD", fontFamily = Mono, color = NothingRed) }
            },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("ОТМЕНА", fontFamily = Mono) } }
        )
    }

    // Вердикт Гоггинса
    if (verdict != null) {
        AlertDialog(
            onDismissRequest = { vm.dismissVerdict() },
            title = {
                Text(
                    if (verdictBad) "ГОГГИНС НЕДОВОЛЕН." else "ГОГГИНС КИВАЕТ.",
                    fontFamily = Mono, fontWeight = FontWeight.Black,
                    color = if (verdictBad) NothingRed else MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                ShakeBox(shakeKey) {
                    Text(verdict!!, fontFamily = Mono, fontSize = 15.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { vm.dismissVerdict() }) {
                    Text(if (verdictBad) "ПОНЯЛ. ИСПРАВЛЮСЬ." else "STAY HARD.", fontFamily = Mono)
                }
            }
        )
    }
}

@Composable
private fun HabitRow(h: HabitUi, onToggle: () -> Unit, onDelete: () -> Unit) {
    val scale by animateFloatAsState(if (h.done) 1f else 0.92f, spring(Spring.DampingRatioMediumBouncy), label = "chk")
    val bg by animateColorAsState(if (h.done) Color(0xFF1A1A1A) else Color.Transparent, label = "bg")
    var confirmDel by remember { mutableStateOf(false) }

    Row(
        Modifier.fillMaxWidth()
            .background(bg, RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .clickable { onToggle() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nothing-чекбокс: квадрат с X
        Box(
            Modifier.size(34.dp).scale(scale)
                .border(2.dp, if (h.done) NothingRed else MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (h.done) Text("✕", fontFamily = Mono, fontWeight = FontWeight.Black, fontSize = 20.sp, color = NothingRed)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                h.habit.title.uppercase(),
                fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                color = if (h.done) Color.Gray else MaterialTheme.colorScheme.onBackground
            )
            Text(
                "${h.habit.icon} // STREAK: ${h.streak} ${if (h.streak == 1) "ДЕНЬ" else "ДН."}",
                fontFamily = Mono, fontSize = 11.sp,
                color = if (h.streak > 0) NothingRed else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = { confirmDel = true }) {
            Icon(Icons.Default.Delete, "Удалить", tint = Color.Gray)
        }
    }

    if (confirmDel) {
        AlertDialog(
            onDismissRequest = { confirmDel = false },
            title = { Text("УДАЛИТЬ?", fontFamily = Mono, fontWeight = FontWeight.Bold) },
            text = { Text("«${h.habit.title}» будет стёрта. Гоггинс такое не одобряет, но это твоя жизнь.", fontFamily = Mono) },
            confirmButton = {
                TextButton(onClick = { onDelete(); confirmDel = false }) {
                    Text("УДАЛИТЬ", fontFamily = Mono, color = NothingRed)
                }
            },
            dismissButton = { TextButton(onClick = { confirmDel = false }) { Text("ОСТАВИТЬ", fontFamily = Mono) } }
        )
    }
}
