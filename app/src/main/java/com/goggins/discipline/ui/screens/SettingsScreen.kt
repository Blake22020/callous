package com.goggins.discipline.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
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
import com.goggins.discipline.ui.theme.Mono
import com.goggins.discipline.ui.theme.NothingRed

@Composable
fun SettingsScreen(vm: GogginsViewModel, s: AppSettings, onBack: () -> Unit) {
    var nameDraft by remember(s.userName) { mutableStateOf(s.userName) }
    var showWipe by remember { mutableStateOf(false) }

    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Назад", tint = MaterialTheme.colorScheme.onBackground)
                }
                GlyphHeader("НАСТРОЙКИ // SETUP", "v1.0")
            }
            DotMatrix()
        }

        item {
            NothingCard {
                Text("БОЕЦ // FIGHTER", fontFamily = Mono, fontSize = 11.sp, color = NothingRed)
                OutlinedTextField(
                    value = nameDraft, onValueChange = { nameDraft = it },
                    label = { Text("Твоё имя", fontFamily = Mono) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { vm.setName(nameDraft.trim()) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.background
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("СОХРАНИТЬ", fontFamily = Mono) }
            }
        }

        item {
            NothingCard {
                Text("МАТ // PROFANITY", fontFamily = Mono, fontSize = 11.sp, color = NothingRed)
                Text(
                    "Гоггинс умеет орать. Выбери, насколько жёстко он будет с тобой разговаривать. Уважаем чувствительных: CLEAN — вообще без мата.",
                    fontFamily = Mono, fontSize = 12.sp, color = Color.Gray
                )
                Spacer(Modifier.height(8.dp))
                ProfanityLevel.entries.forEach { level ->
                    val selected = s.profanity == level
                    Row(
                        Modifier.fillMaxWidth()
                            .border(
                                1.dp,
                                if (selected) NothingRed else MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { vm.setProfanity(level) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                (if (selected) "◉ " else "○ ") + level.title,
                                fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                                color = if (selected) NothingRed else MaterialTheme.colorScheme.onBackground
                            )
                            Text(level.subtitle, fontFamily = Mono, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }
                // Живой пример
                Text("ПРЕДПРОСМОТР ПИНКА:", fontFamily = Mono, fontSize = 11.sp, color = Color.Gray)
                Text(
                    "«${VerbalAssault.insult(s.profanity, seed = 3)}»",
                    fontFamily = Mono, fontSize = 13.sp, fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            NothingCard {
                SettingRow("ТЁМНАЯ ТЕМА // NOTHING BLACK", s.darkTheme) { vm.setDark(it) }
                SettingRow("НАПОМИНАНИЕ В 06:00", s.reminderEnabled) {
                    vm.setReminder(it, s.reminderHour, s.reminderMinute)
                }
                Text(
                    "Напоминание приходит щадящим (максимум MILD), чтобы не получить бан и не обидеть. Жесть — только внутри приложения.",
                    fontFamily = Mono, fontSize = 11.sp, color = Color.Gray
                )
            }
        }

        item {
            NothingCard {
                Text("ОПАСНАЯ ЗОНА", fontFamily = Mono, fontSize = 11.sp, color = NothingRed)
                Text("Стереть все привычки, прогресс и настройки. Гоггинс бы не одобрил слабость, но решение твоё.", fontFamily = Mono, fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { showWipe = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NothingRed, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("СТЕРЕТЬ ВСЁ", fontFamily = Mono) }
            }
        }

        item {
            Text(
                "GOGGINS.OS v1.0 // СДЕЛАНО С БОЛЬЮ.\nЦитаты: D. Goggins «Can't Hurt Me», «Never Finished».\nSTAY HARD.",
                fontFamily = Mono, fontSize = 10.sp, color = Color.Gray
            )
            Spacer(Modifier.height(90.dp))
        }
    }

    if (showWipe) {
        AlertDialog(
            onDismissRequest = { showWipe = false },
            title = { Text("ТОЧНО СТЕРЕТЬ?", fontFamily = Mono, fontWeight = FontWeight.Bold) },
            text = { Text("Это действие необратимо. Слабый голос говорит «да». Что скажешь ты?", fontFamily = Mono) },
            confirmButton = {
                TextButton(onClick = { vm.wipeAll(); showWipe = false; onBack() }) {
                    Text("СТЕРЕТЬ", fontFamily = Mono, color = NothingRed)
                }
            },
            dismissButton = { TextButton(onClick = { showWipe = false }) { Text("ОТМЕНА", fontFamily = Mono) } }
        )
    }
}

@Composable
private fun SettingRow(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontFamily = Mono, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Switch(
            checked = checked, onCheckedChange = onChange,
            colors = SwitchDefaults.colors(checkedThumbColor = NothingRed)
        )
    }
}
