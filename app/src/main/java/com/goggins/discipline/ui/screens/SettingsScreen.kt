package com.goggins.discipline.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.goggins.discipline.data.content.VerbalAssault
import com.goggins.discipline.data.settings.AppSettings
import com.goggins.discipline.data.settings.ProfanityLevel
import com.goggins.discipline.ui.GogginsViewModel
import com.goggins.discipline.ui.components.DotMatrix
import com.goggins.discipline.ui.components.GlyphHeader
import com.goggins.discipline.ui.components.NothingCard
import com.goggins.discipline.ui.theme.NothingRed

@Composable
fun SettingsScreen(vm: GogginsViewModel, s: AppSettings, onBack: () -> Unit) {
    var nameDraft by remember(s.userName) { mutableStateOf(s.userName) }
    var showWipe by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        run {
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Назад", tint = MaterialTheme.colorScheme.onBackground)
                }
                GlyphHeader("НАСТРОЙКИ · SETUP", "v1.0")
            }
            DotMatrix()
        }

        run {
            NothingCard {
                Text("БОЕЦ · FIGHTER", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = nameDraft, onValueChange = { nameDraft = it },
                    label = { Text("Твоё имя") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { vm.setName(nameDraft.trim()) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.background
                    ),
                    shape = MaterialTheme.shapes.small
                ) { Text("СОХРАНИТЬ", style = MaterialTheme.typography.labelLarge) }
            }
        }

        run {
            NothingCard {
                Text("МАТ · PROFANITY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(10.dp))
                Text(
                    "Гоггинс умеет орать. Выбери, насколько жёстко он будет с тобой разговаривать. Уважаем чувствительных: CLEAN — вообще без мата.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(14.dp))
                ProfanityLevel.entries.forEach { level ->
                    val selected = s.profanity == level
                    Row(
                        Modifier.fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .background(if (selected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)
                            .clickable { vm.setProfanity(level) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                (if (selected) "◉ " else "○ ") + level.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (selected) NothingRed else MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(level.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
                // Живой пример
                Text("ПРЕДПРОСМОТР", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(
                    "«${VerbalAssault.insult(s.profanity, seed = 2, userName = s.userName, habitTitle = "тренировку")}»",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        run {
            NothingCard {
                SettingRow("ТЁМНАЯ ТЕМА · NOTHING BLACK", s.darkTheme) { vm.setDark(it) }
                SettingRow("НАПОМИНАНИЕ В 06:00", s.reminderEnabled) {
                    vm.setReminder(it, s.reminderHour, s.reminderMinute)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Напоминание использует выбранный уровень. Текст может быть виден на экране блокировки; отключи напоминания, если это не подходит.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        run {
            NothingCard {
                Text("ОПАСНАЯ ЗОНА", style = MaterialTheme.typography.labelSmall, color = NothingRed)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Стереть все привычки, прогресс и настройки. Гоггинс бы не одобрил слабость, но решение твоё.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = { showWipe = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NothingRed, contentColor = Color.White),
                    shape = MaterialTheme.shapes.small
                ) { Text("СТЕРЕТЬ ВСЁ", style = MaterialTheme.typography.labelLarge) }
            }
        }

        run {
            Text(
                "CALLOUS · СДЕЛАНО С БОЛЬЮ.\nЦитаты: D. Goggins «Can't Hurt Me», «Never Finished».\nSTAY HARD.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(96.dp))
        }
    }

    if (showWipe) {
        AlertDialog(
            onDismissRequest = { showWipe = false },
            title = { Text("Точно стереть?", style = MaterialTheme.typography.titleLarge) },
            text = { Text("Это действие необратимо. Слабый голос говорит «да». Что скажешь ты?", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = { vm.wipeAll(); showWipe = false; onBack() }) {
                    Text("СТЕРЕТЬ", style = MaterialTheme.typography.labelLarge, color = NothingRed)
                }
            },
            dismissButton = { TextButton(onClick = { showWipe = false }) { Text("ОТМЕНА", style = MaterialTheme.typography.labelLarge) } }
        )
    }
}

@Composable
private fun SettingRow(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked, onCheckedChange = onChange,
            colors = SwitchDefaults.colors(checkedThumbColor = NothingRed, checkedTrackColor = NothingRed.copy(alpha = 0.35f))
        )
    }
}
