package com.goggins.discipline.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.goggins.discipline.data.content.PowerLaw
import com.goggins.discipline.data.content.PowerLaws
import com.goggins.discipline.data.content.Quotes
import com.goggins.discipline.data.content.SosLibrary
import com.goggins.discipline.data.settings.AppSettings
import com.goggins.discipline.ui.GogginsViewModel
import com.goggins.discipline.ui.components.NothingCard
import com.goggins.discipline.ui.components.ShakeBox
import com.goggins.discipline.ui.theme.NothingRed
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.delay

@Composable
fun MindsetScreen(vm: GogginsViewModel, settings: AppSettings) {
    val context = LocalContext.current
    var quote by remember { mutableStateOf(Quotes.random()) }
    var sosId by remember { mutableStateOf(SosLibrary.all.first().id) }
    var today by remember { mutableStateOf(LocalDate.now()) }
    var showLawDetails by remember { mutableStateOf(false) }
    var showSos by remember { mutableStateOf(false) }
    var showQuoteArchive by remember { mutableStateOf(false) }
    val sos = SosLibrary.all.firstOrNull { it.id == sosId } ?: SosLibrary.all.first()
    val law = remember(today) { PowerLaws.forDate(today) }
    val lawApplied = settings.appliedPowerLawDate == today.toString()
    val verdict by vm.verdict.collectAsState()
    val shakeKey by vm.shakeKey.collectAsState()

    LaunchedEffect(Unit) {
        while (true) {
            val nextDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            delay((nextDay - System.currentTimeMillis()).coerceAtLeast(1_000L))
            today = LocalDate.now()
            showLawDetails = false
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(Modifier.padding(top = 18.dp)) {
            Text("Разум и влияние", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
            Text("Один закон · одно действие · каждый день", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        PowerLawCard(law, lawApplied, showLawDetails, onToggleDetails = { showLawDetails = !showLawDetails }) {
            vm.markPowerLawApplied(today.toString())
        }

        NothingCard {
            Text("Случайная цитата", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(8.dp))
            Text("«${quote.text}»", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(4.dp))
            Text("— ${quote.source}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { quote = Quotes.random() }) { Text("Другая цитата", color = MaterialTheme.colorScheme.onBackground) }
                TextButton(onClick = {
                    copy(context, "«${quote.text}» — ${quote.source}", "callous")
                }) { Text("Копировать", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            OutlinedButton(onClick = { vm.punchMe(settings.profanity, settings.userName) }, modifier = Modifier.fillMaxWidth()) {
                Text("Дай жёсткий пинок · ${settings.profanity.title}", color = NothingRed)
            }
        }

        NothingCard {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text("SOS", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                    Text("Короткие подсказки для тяжёлого момента", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = { showSos = !showSos }) {
                    Text(if (showSos) "Свернуть" else "Открыть", color = MaterialTheme.colorScheme.onBackground)
                }
            }
            if (showSos) {
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SosLibrary.all.forEach { item ->
                        if (item.id == sosId) {
                            Button(
                                onClick = { sosId = item.id },
                                colors = ButtonDefaults.buttonColors(containerColor = NothingRed, contentColor = Color.White)
                            ) { Text("${item.glyph} ${item.label}") }
                        } else {
                            OutlinedButton(onClick = { sosId = item.id }) { Text("${item.glyph} ${item.label}") }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(sos.title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(8.dp))
                sos.body.forEach { paragraph ->
                    Text(paragraph, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(8.dp))
                }
                TextButton(onClick = { copy(context, sos.title + "\n\n" + sos.body.joinToString("\n\n"), "sos") }) {
                    Text("Копировать подсказку", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        NothingCard {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Архив цитат", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                TextButton(onClick = { showQuoteArchive = !showQuoteArchive }) {
                    Text(if (showQuoteArchive) "Свернуть" else "${Quotes.all.size} цитат", color = MaterialTheme.colorScheme.onBackground)
                }
            }
            if (showQuoteArchive) {
                Quotes.all.forEach { item ->
                    Spacer(Modifier.height(10.dp))
                    Text("«${item.text}»", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                    Text("— ${item.source}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(Modifier.height(72.dp))
    }

    if (verdict != null) {
        AlertDialog(
            onDismissRequest = { vm.dismissVerdict() },
            title = { Text("Гоггинс говорит", style = MaterialTheme.typography.titleLarge, color = NothingRed) },
            text = { ShakeBox(shakeKey) { Text(verdict!!, style = MaterialTheme.typography.bodyLarge) } },
            confirmButton = { TextButton(onClick = { vm.dismissVerdict() }) { Text("Принял") } }
        )
    }
}

@Composable
private fun PowerLawCard(
    law: PowerLaw,
    applied: Boolean,
    showDetails: Boolean,
    onToggleDetails: () -> Unit,
    onApply: () -> Unit
) {
    NothingCard {
        Text("ЗАКОН ${law.number.toString().padStart(2, '0')} / 48", style = MaterialTheme.typography.labelMedium, color = NothingRed)
        Spacer(Modifier.height(6.dp))
        Text(law.title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(10.dp))
        Text(law.idea, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(14.dp))
        Text("Действие на сегодня", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(law.action, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = onApply,
            enabled = !applied,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground, contentColor = MaterialTheme.colorScheme.background)
        ) { Text(if (applied) "Выполнено сегодня" else "Отметить выполнение") }
        TextButton(onClick = onToggleDetails) {
            Text(if (showDetails) "Свернуть разбор" else "Пример, применение и защита", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (showDetails) {
            LawSection("Пример", law.example)
            LawSection("Как применить", law.use)
            LawSection("Как защититься", law.defense)
            Spacer(Modifier.height(4.dp))
            Text("Авторский разбор идей Роберта Грина. Практика закона опирается на доверие и ответственность.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LawSection(title: String, body: String) {
    Spacer(Modifier.height(10.dp))
    Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(3.dp))
    Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
}

private fun copy(context: Context, text: String, label: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
}
