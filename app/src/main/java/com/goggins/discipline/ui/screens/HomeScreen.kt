package com.goggins.discipline.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goggins.discipline.data.content.PowerLaws
import com.goggins.discipline.data.content.VerbalAssault
import com.goggins.discipline.data.settings.AppSettings
import com.goggins.discipline.ui.GogginsViewModel
import com.goggins.discipline.ui.HabitUi
import com.goggins.discipline.ui.components.NothingCard
import com.goggins.discipline.ui.components.ShakeBox
import com.goggins.discipline.ui.theme.NothingRed
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay

private const val FOCUS_DURATION_MILLIS = 25 * 60 * 1000L

@Composable
fun HomeScreen(
    vm: GogginsViewModel,
    settings: AppSettings,
    onSettings: () -> Unit,
    onLaws: () -> Unit
) {
    val list by vm.ui.collectAsState()
    val verdict by vm.verdict.collectAsState()
    val verdictBad by vm.verdictBad.collectAsState()
    val shakeKey by vm.shakeKey.collectAsState()
    val note by vm.noteText.collectAsState()
    val productivity by vm.productivity.collectAsState()

    var today by remember { mutableStateOf(LocalDate.now()) }
    val date = today.toString()
    val law = remember(today) { PowerLaws.forDate(today) }
    val plan = productivity.forDate(date, law.action)
    val yesterday = productivity.days.firstOrNull { it.date == today.minusDays(1).toString() }
    var workDraft by remember(date) { mutableStateOf(plan.work) }
    var disciplineDraft by remember(date) { mutableStateOf(plan.discipline) }
    var respectDraft by remember(date) { mutableStateOf(plan.respect) }
    var proofDraft by remember(date) { mutableStateOf(plan.proof) }
    var noteDraft by remember(note) { mutableStateOf(note) }
    var showHabits by remember { mutableStateOf(false) }
    var showAdd by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newIcon by remember { mutableStateOf("◼") }
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            val nextDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            delay((nextDay - System.currentTimeMillis()).coerceAtLeast(1_000L))
            today = LocalDate.now()
        }
    }
    LaunchedEffect(date) { vm.ensureDailyProgress(date) }
    LaunchedEffect(date, plan.work) { if (workDraft != plan.work) workDraft = plan.work }
    LaunchedEffect(date, plan.discipline) { if (disciplineDraft != plan.discipline) disciplineDraft = plan.discipline }
    LaunchedEffect(date, plan.respect) { if (respectDraft != plan.respect) respectDraft = plan.respect }
    LaunchedEffect(date, plan.proof) { if (proofDraft != plan.proof) proofDraft = plan.proof }
    LaunchedEffect(plan.focusStartedAtMillis) {
        if (plan.focusStartedAtMillis > 0L) {
            while (true) {
                nowMillis = System.currentTimeMillis()
                delay(1_000L)
            }
        }
    }

    Scaffold { padding ->
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("CALLOUS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 2.sp)
                    Spacer(Modifier.height(3.dp))
                    Text("Сегодня", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
                    Text(today.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Настройки", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (yesterday != null && yesterday.commitmentsPlanned > yesterday.commitmentsDone) {
                Text(
                    "Вчерашний план не закрыт. Уменьши шаг и начни снова сегодня.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            NothingCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Три обещания", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
                    Text("${plan.commitmentsDone}/3", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { plan.commitmentsDone / 3f },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = NothingRed,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                CommitmentEditor(
                    title = "Работа",
                    value = workDraft,
                    done = plan.workDone,
                    placeholder = "Главный результат дня",
                    onValueChange = { workDraft = it; vm.saveDailyCommitment(date, "work", it) },
                    onToggle = { vm.toggleDailyCommitment(date, "work") }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                CommitmentEditor(
                    title = "Дисциплина",
                    value = disciplineDraft,
                    done = plan.disciplineDone,
                    placeholder = "Нужный шаг, который хочется отложить",
                    onValueChange = { disciplineDraft = it; vm.saveDailyCommitment(date, "discipline", it) },
                    onToggle = { vm.toggleDailyCommitment(date, "discipline") }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                CommitmentEditor(
                    title = "Уважение",
                    value = respectDraft,
                    done = plan.respectDone,
                    placeholder = "${law.title}: ${law.action}",
                    onValueChange = { respectDraft = it; vm.saveDailyCommitment(date, "respect", it) },
                    onToggle = { vm.toggleDailyCommitment(date, "respect") }
                )
                Spacer(Modifier.height(6.dp))
                TextButton(onClick = onLaws) {
                    Text("Закон дня · ${law.number}/48  →", color = NothingRed)
                }
            }

            NothingCard {
                Text("Фокус", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(3.dp))
                if (plan.focusStartedAtMillis == 0L) {
                    Text("25 минут на одну задачу", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { vm.startFocusSession(date) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground, contentColor = MaterialTheme.colorScheme.background)
                    ) { Text("Начать фокус") }
                } else {
                    val remainingSeconds = ((plan.focusStartedAtMillis + FOCUS_DURATION_MILLIS - nowMillis) / 1_000L).coerceAtLeast(0L)
                    Text(
                        if (remainingSeconds > 0) "${remainingSeconds / 60}:${(remainingSeconds % 60).toString().padStart(2, '0')} осталось"
                        else "25:00 · сессия завершена",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { (1f - remainingSeconds / (FOCUS_DURATION_MILLIS / 1_000f)).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = NothingRed,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { vm.finishFocusSession(date, plan.focusStartedAtMillis) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground, contentColor = MaterialTheme.colorScheme.background)
                    ) { Text("Сохранить фокус") }
                }
                if (plan.focusMinutes > 0) {
                    Spacer(Modifier.height(8.dp))
                    Text("Сегодня: ${plan.focusMinutes} мин · ${plan.focusSessions} сесс.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { showHabits = !showHabits }, modifier = Modifier.weight(1f)) {
                    Text("Привычки · ${list.count { it.done }}/${list.size}", color = MaterialTheme.colorScheme.onBackground)
                }
                if (showHabits) {
                    IconButton(onClick = { showAdd = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить привычку", tint = NothingRed)
                    }
                }
            }
            if (showHabits) {
                if (list.isEmpty()) {
                    Text("Добавь привычки, которые хочешь поддерживать регулярно.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                list.forEach { habit ->
                    HabitRow(
                        habit,
                        onToggle = { vm.toggle(habit, settings.profanity, settings.userName) },
                        onDelete = { vm.deleteHabit(habit.habit) }
                    )
                }
                if (list.isEmpty()) {
                    TextButton(onClick = { showAdd = true }) { Text("Добавить привычку", color = NothingRed) }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Разбор дня", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                Text("Что сделал? Где отступил? Что изменишь завтра?", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(
                    value = noteDraft,
                    onValueChange = { noteDraft = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Запиши честно, без оправданий") },
                    minLines = 3,
                    maxLines = 5
                )
                Button(
                    onClick = { vm.saveNote(noteDraft) },
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground, contentColor = MaterialTheme.colorScheme.background)
                ) { Text("Сохранить разбор") }
                OutlinedTextField(
                    value = proofDraft,
                    onValueChange = { proofDraft = it; vm.saveProof(date, it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Победа, которую запомню") },
                    placeholder = { Text("Один факт, подтверждающий прогресс") },
                    minLines = 2,
                    maxLines = 3
                )
                Spacer(Modifier.height(72.dp))
            }
        }
    }

    if (showAdd) {
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("Новая привычка") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = newTitle, onValueChange = { newTitle = it }, label = { Text("Например, бег 5 км") }, singleLine = true)
                    Text("Значок", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("◼", "●", "▲", "◆", "✕", "☰", "⚡", "⛓").forEach { glyph ->
                            Box(
                                Modifier.size(40.dp).clip(MaterialTheme.shapes.extraSmall)
                                    .background(if (newIcon == glyph) NothingRed else MaterialTheme.colorScheme.surface)
                                    .clickable { newIcon = glyph },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(glyph, fontSize = 18.sp, color = if (newIcon == glyph) Color.White else MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.addHabit(newTitle, newIcon, settings.profanity, settings.userName)
                    newTitle = ""
                    newIcon = "◼"
                    showAdd = false
                }) { Text("Добавить", color = NothingRed) }
            },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("Отмена") } }
        )
    }

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
            text = { ShakeBox(shakeKey) { Text(verdict!!, style = MaterialTheme.typography.bodyLarge) } },
            confirmButton = {
                TextButton(onClick = { vm.dismissVerdict() }) {
                    Text(if (verdictBad) "ПОНЯЛ. ИСПРАВЛЮСЬ." else "STAY HARD.", style = MaterialTheme.typography.labelLarge)
                }
            }
        )
    }
}

@Composable
private fun CommitmentEditor(
    title: String,
    value: String,
    done: Boolean,
    placeholder: String,
    onValueChange: (String) -> Unit,
    onToggle: () -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
            Checkbox(checked = done, onCheckedChange = { onToggle() }, enabled = value.isNotBlank())
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, maxLines = 2) },
            maxLines = 2,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (done) FontWeight.Normal else FontWeight.Medium)
        )
    }
}

@Composable
private fun HabitRow(habit: HabitUi, onToggle: () -> Unit, onDelete: () -> Unit) {
    var confirmDelete by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.surface)
            .clickable { onToggle() }.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(24.dp).border(
                if (habit.done) 2.dp else 1.dp,
                if (habit.done) NothingRed else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(4.dp)
            ),
            contentAlignment = Alignment.Center
        ) {
            if (habit.done) Icon(Icons.Default.Close, contentDescription = "Выполнено", tint = NothingRed, modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(habit.habit.title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground)
            Text("Серия · ${habit.streak} дн.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        IconButton(onClick = { confirmDelete = true }) {
            Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Удалить привычку?") },
            text = { Text("«${habit.habit.title}» и её отметки будут удалены.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); confirmDelete = false }) { Text("Удалить", color = NothingRed) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Отмена") } }
        )
    }
}
