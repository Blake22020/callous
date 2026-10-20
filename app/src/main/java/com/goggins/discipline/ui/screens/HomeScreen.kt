package com.goggins.discipline.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goggins.discipline.data.content.Quotes
import com.goggins.discipline.data.content.VerbalAssault
import com.goggins.discipline.data.settings.AppSettings
import com.goggins.discipline.ui.GogginsViewModel
import com.goggins.discipline.ui.HabitUi
import com.goggins.discipline.ui.ProgressRing
import com.goggins.discipline.ui.components.DotMatrix
import com.goggins.discipline.ui.components.GlyphHeader
import com.goggins.discipline.ui.components.NothingCard
import com.goggins.discipline.ui.components.ShakeBox
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
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(pad).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            run {
                Row(Modifier.fillMaxWidth().padding(top = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "CALLOUS",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 3.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            if (settings.userName.isBlank()) "STAY HARD." else "STAY HARD, ${settings.userName.uppercase()}.",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "$dateStr · НИКТО НЕ ПРИДЁТ ТЕБЯ СПАСАТЬ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, "Настройки", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
                Spacer(Modifier.height(6.dp))
                DotMatrix()
            }

            run {
                NothingCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProgressRing(progress)
                        Spacer(Modifier.width(18.dp))
                        Column(Modifier.weight(1f)) {
                            val done = list.count { it.done }
                            Text(
                                VerbalAssault.homeMessage(settings.profanity, done, list.size, settings.userName),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                "[$done/${list.size}] ЗАКРЫТО",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(10.dp))
                            // Настоящий Material-прогресс вместо ASCII-бара.
                            LinearProgressIndicator(
                                progress = { progress.coerceIn(0f, 1f) },
                                color = NothingRed,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth().height(4.dp)
                            )
                        }
                    }
                }
            }

            run {
                NothingCard {
                    Text("ЦИТАТА ДНЯ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("«${dailyQuote.text}»", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(4.dp))
                    Text("— ${dailyQuote.source}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            run {
                GlyphHeader("СЕГОДНЯ · TODAY", "${list.count { it.done }}/${list.size}")
            }

            if (list.isEmpty()) {
                run {
                    NothingCard {
                        Text(
                            "Пусто. Гоггинс презрительно смотрит на эту пустоту.\nНажми + и добавь первую привычку: бег, отжимания, чтение, холодный душ.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            list.forEach { h ->
                HabitRow(h, onToggle = { vm.toggle(h, settings.profanity, settings.userName) }, onDelete = { vm.deleteHabit(h.habit) })
            }

            run {
                GlyphHeader("ЗЕРКАЛО · ACCOUNTABILITY", "ЧЕСТНО")
                NothingCard {
                    BasicTextField(
                        value = noteDraft,
                        onValueChange = { noteDraft = it },
                        modifier = Modifier.fillMaxWidth().height(96.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                        decorationBox = { inner ->
                            Box {
                                if (noteDraft.isEmpty()) Text(
                                    "Где сегодня сдался? Где победил? Пиши правду...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                inner()
                            }
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { vm.saveNote(noteDraft) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.background
                        ),
                        shape = MaterialTheme.shapes.small
                    ) { Text("ЗАПИСАТЬ", style = MaterialTheme.typography.labelLarge) }
                }
                Spacer(Modifier.height(96.dp))
            }
        }
    }

    if (showAdd) {
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("Новая привычка", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTitle, onValueChange = { newTitle = it },
                        label = { Text("Название: бег 5км") }, singleLine = true
                    )
                    Text("ГЛИФ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("◼", "●", "▲", "◆", "✕", "☰", "⚡", "⛓").forEach { g ->
                            Box(
                                Modifier.size(40.dp)
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .background(if (newIcon == g) NothingRed else MaterialTheme.colorScheme.surface)
                                    .clickable { newIcon = g },
                                contentAlignment = Alignment.Center
                            ) { Text(g, fontSize = 18.sp, color = if (newIcon == g) Color.White else MaterialTheme.colorScheme.onBackground) }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.addHabit(newTitle, newIcon, settings.profanity, settings.userName)
                    newTitle = ""; newIcon = "◼"; showAdd = false
                }) { Text("ДОБАВИТЬ", style = MaterialTheme.typography.labelLarge, color = NothingRed) }
            },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("ОТМЕНА", style = MaterialTheme.typography.labelLarge) } }
        )
    }

    // Вердикт Гоггинса
    if (verdict != null) {
        AlertDialog(
            onDismissRequest = { vm.dismissVerdict() },
            title = {
                Text(
                    if (verdictBad) "Гоггинс недоволен." else "Гоггинс кивает.",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (verdictBad) NothingRed else MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                ShakeBox(shakeKey) {
                    Text(verdict!!, style = MaterialTheme.typography.bodyLarge)
                }
            },
            confirmButton = {
                TextButton(onClick = { vm.dismissVerdict() }) {
                    Text(if (verdictBad) "ПОНЯЛ. ИСПРАВЛЮСЬ." else "STAY HARD.", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }
}

@Composable
private fun HabitRow(h: HabitUi, onToggle: () -> Unit, onDelete: () -> Unit) {
    val scale = if (h.done) 1f else 0.94f
    var confirmDel by remember { mutableStateOf(false) }

    Row(
        Modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nothing-чекбокс: тонкий квадрат, при выполнении — красный крест.
        Box(
            Modifier.size(26.dp).scale(scale)
                .border(
                    if (h.done) 2.dp else 1.5.dp,
                    if (h.done) NothingRed else MaterialTheme.colorScheme.outline,
                    MaterialTheme.shapes.extraSmall
                ),
            contentAlignment = Alignment.Center
        ) {
            if (h.done) Icon(
                Icons.Default.Close, contentDescription = "выполнено",
                tint = NothingRed, modifier = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                h.habit.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (h.done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onBackground
            )
            Text(
                "STREAK · ${h.streak} ${if (h.streak == 1) "ДЕНЬ" else "ДН."}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = { confirmDel = true }) {
            Icon(Icons.Default.Delete, "Удалить", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    if (confirmDel) {
        AlertDialog(
            onDismissRequest = { confirmDel = false },
            title = { Text("Удалить?", style = MaterialTheme.typography.titleLarge) },
            text = { Text("«${h.habit.title}» будет стёрта. Гоггинс такое не одобряет, но это твоя жизнь.", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = { onDelete(); confirmDel = false }) {
                    Text("УДАЛИТЬ", style = MaterialTheme.typography.labelLarge, color = NothingRed)
                }
            },
            dismissButton = { TextButton(onClick = { confirmDel = false }) { Text("ОСТАВИТЬ", style = MaterialTheme.typography.labelLarge) } }
        )
    }
}
