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
import com.goggins.discipline.data.content.VerbalAssault
import com.goggins.discipline.data.settings.AppSettings
import com.goggins.discipline.ui.GogginsViewModel
import com.goggins.discipline.ui.components.DotMatrix
import com.goggins.discipline.ui.components.GlyphHeader
import com.goggins.discipline.ui.components.NothingCard
import com.goggins.discipline.ui.components.ShakeBox
import com.goggins.discipline.ui.theme.NothingRed
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.delay

@Composable
fun MindsetScreen(vm: GogginsViewModel, settings: AppSettings) {
    val ctx = LocalContext.current
    var current by remember { mutableStateOf(Quotes.random()) }
    var sosId by remember { mutableStateOf(SosLibrary.all.first().id) }
    var today by remember { mutableStateOf(LocalDate.now()) }
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
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        run {
            Spacer(Modifier.height(12.dp))
            GlyphHeader("РАЗУМ · MINDSET", "CALLOUS IT")
            DotMatrix()
            PowerLawCard(law, lawApplied) { vm.markPowerLawApplied(today.toString()) }
            NothingCard {
                Column {
                    Text("«${current.text}»", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(8.dp))
                    Text("— ${current.source}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { current = Quotes.random() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.background
                        ),
                        shape = MaterialTheme.shapes.small
                    ) { Text("ЕЩЁ", style = MaterialTheme.typography.labelLarge) }
                    OutlinedButton(
                        onClick = {
                            val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("callous", "«${current.text}» — ${current.source}"))
                        },
                        shape = MaterialTheme.shapes.small
                    ) { Text("КОПИРОВАТЬ", style = MaterialTheme.typography.labelLarge) }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { vm.punchMe(settings.profanity, settings.userName) },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("ДОБЕЙ МЕНЯ", style = MaterialTheme.typography.labelLarge, color = NothingRed) }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Уровень жести: ${settings.profanity.title}. Меняется в настройках.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        run {
            GlyphHeader("SOS · АПТЕЧКА", "ЧИТАЙ ПО СОСТОЯНИЮ")
            NothingCard {
                Text(
                    VerbalAssault.sosIntro(settings.profanity, settings.userName),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(14.dp))
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SosLibrary.all.forEach { s ->
                        val selected = s.id == sosId
                        if (selected) {
                            Button(
                                onClick = { sosId = s.id },
                                colors = ButtonDefaults.buttonColors(containerColor = NothingRed, contentColor = Color.White),
                                shape = MaterialTheme.shapes.small
                            ) { Text("${s.glyph} ${s.label}", style = MaterialTheme.typography.labelLarge) }
                        } else {
                            OutlinedButton(onClick = { sosId = s.id }, shape = MaterialTheme.shapes.small) {
                                Text("${s.glyph} ${s.label}", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
                Text(sos.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(10.dp))
                sos.body.forEach { p ->
                    Text(p, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(10.dp))
                }
                OutlinedButton(
                    onClick = {
                        val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cm.setPrimaryClip(ClipData.newPlainText("sos", sos.title + "\n\n" + sos.body.joinToString("\n\n")))
                    },
                    shape = MaterialTheme.shapes.small
                ) { Text("СКОПИРОВАТЬ ТЕКСТ", style = MaterialTheme.typography.labelLarge) }
            }
        }

        run { GlyphHeader("ВСЕ ЦИТАТЫ · ARCHIVE", "${Quotes.all.size} ШТ.") }
        Quotes.all.forEach { q ->
            NothingCard {
                Text("«${q.text}»", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(6.dp))
                Text("— ${q.source}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        run { Spacer(Modifier.height(96.dp)) }
    }

    if (verdict != null) {
        AlertDialog(
            onDismissRequest = { vm.dismissVerdict() },
            title = { Text("Гоггинс говорит.", style = MaterialTheme.typography.titleLarge, color = NothingRed) },
            text = { ShakeBox(shakeKey) { Text(verdict!!, style = MaterialTheme.typography.bodyLarge) } },
            confirmButton = { TextButton(onClick = { vm.dismissVerdict() }) { Text("ПРИНЯЛ.", style = MaterialTheme.typography.labelLarge) } }
        )
    }
}

@Composable
private fun PowerLawCard(law: PowerLaw, applied: Boolean, onApply: () -> Unit) {
    GlyphHeader("ЗАКОН ДНЯ · ВЛАСТЬ", "${law.number}/48")
    NothingCard {
        Text("ЗАКОН ${law.number.toString().padStart(2, '0')}", style = MaterialTheme.typography.labelSmall, color = NothingRed)
        Spacer(Modifier.height(6.dp))
        Text(law.title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(14.dp))
        LawSection("СУТЬ", law.idea)
        LawSection("ПРИМЕР", law.example)
        LawSection("ПРИМЕНЕНИЕ", law.use)
        LawSection("КАК ЗАЩИТИТЬСЯ", law.defense)
        LawSection("ДЕЙСТВИЕ НА СЕГОДНЯ", law.action)
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onApply,
            enabled = !applied,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Text(
                if (applied) "ВЫПОЛНЕНО СЕГОДНЯ" else "ОТМЕТИТЬ ВЫПОЛНЕНИЕ",
                style = MaterialTheme.typography.labelLarge
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Авторский разбор идей Роберта Грина. Закон меняется по местной дате; цикл — 48 дней.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LawSection(label: String, text: String) {
    Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(3.dp))
    Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(12.dp))
}
