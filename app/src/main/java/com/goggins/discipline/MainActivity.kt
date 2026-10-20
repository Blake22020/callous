package com.goggins.discipline

import android.os.Bundle
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.goggins.discipline.data.content.VerbalAssault
import com.goggins.discipline.data.settings.AppSettings
import com.goggins.discipline.data.settings.ProfanityLevel
import com.goggins.discipline.ui.GogginsViewModel
import com.goggins.discipline.ui.components.DotMatrix
import com.goggins.discipline.ui.navigation.NavGraph
import com.goggins.discipline.ui.navigation.Routes
import com.goggins.discipline.ui.theme.GogginsTheme
import com.goggins.discipline.ui.theme.NothingRed

class MainActivity : ComponentActivity() {
    private val vm: GogginsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsState by vm.settingsState.collectAsState()
            val settings = settingsState.settings
            val notificationPermission = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { }
            LaunchedEffect(settingsState.loaded, settings.toneConsentAccepted) {
                if (settingsState.loaded && settings.toneConsentAccepted && Build.VERSION.SDK_INT >= 33 &&
                    ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    notificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            GogginsTheme(dark = settings.darkTheme) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when {
                        !settingsState.loaded -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("CALLOUS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        !settings.onboarded || !settings.toneConsentAccepted -> Onboarding(vm, settings)
                        else -> AppShell(vm, settings)
                    }
                }
            }
        }
    }
}

@Composable
private fun AppShell(vm: GogginsViewModel, settings: AppSettings) {
    val nav = rememberNavController()
    val back by nav.currentBackStackEntryAsState()
    val route = back?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                val items = listOf(
                    Triple(Routes.TODAY, "СЕГОДНЯ", Icons.Outlined.CheckCircle),
                    Triple(Routes.STATS, "ДАННЫЕ", Icons.Outlined.BarChart),
                    Triple(Routes.CHALLENGES, "АД", Icons.Outlined.Whatshot),
                    Triple(Routes.MINDSET, "РАЗУМ", Icons.Outlined.Lightbulb),
                )
                items.forEach { (r, label, icon) ->
                    NavigationBarItem(
                        selected = route == r,
                        onClick = { if (route != r) nav.navigate(r) { launchSingleTop = true } },
                        icon = { Icon(icon, contentDescription = label) },
                        label = {
                            Text(label, style = MaterialTheme.typography.labelMedium)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor = MaterialTheme.colorScheme.onBackground,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedIconColor = MaterialTheme.colorScheme.onBackground,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad)) { NavGraph(nav, vm, settings) }
    }
}

@Composable
private fun Onboarding(vm: GogginsViewModel, settings: AppSettings) {
    val firstRun = !settings.onboarded
    var name by remember { mutableStateOf(settings.userName) }
    var level by remember { mutableStateOf(settings.profanity) }
    var acceptedTone by remember(level) { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Контент скроллится, чтобы кнопка продолжения всегда оставалась внизу.
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                "CALLOUS",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "STAY HARD.",
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 40.sp, fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                if (firstRun) "ТРЕКЕР ДИСЦИПЛИНЫ" else "ПОДТВЕРДИ СТИЛЬ ОБЩЕНИЯ",
                style = MaterialTheme.typography.labelLarge,
                color = NothingRed
            )
            Spacer(Modifier.height(14.dp))
            DotMatrix()
            Spacer(Modifier.height(18.dp))
            Text(
                if (firstRun) {
                    "Никто не придёт тебя спасать.\nВыбери, насколько жёстко CALLOUS будет с тобой говорить."
                } else {
                    "Привычки и прогресс останутся. Подтверди уровень общения, чтобы продолжить."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (firstRun) {
                Spacer(Modifier.height(22.dp))
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Как звать, боец?") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "НАСКОЛЬКО ЖЁСТКО С ТОБОЙ ГОВОРИТЬ?",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            ProfanityLevel.entries.forEach { l ->
                val selected = level == l
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(if (selected) NothingRed else MaterialTheme.colorScheme.surface)
                        .clickable { level = l }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        l.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        if (selected) "◉" else "○",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            // Описание — только для выбранного уровня, одной строкой под списком.
            Spacer(Modifier.height(4.dp))
            Text(
                level.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text("ПРЕДПРОСМОТР", style = MaterialTheme.typography.labelSmall, color = NothingRed)
            Spacer(Modifier.height(4.dp))
            Text(
                "«${VerbalAssault.insult(level, seed = 2, userName = name.trim(), habitTitle = "тренировку")}»",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(14.dp))
            Row(
                Modifier.fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surface)
                    .toggleable(
                        value = acceptedTone,
                        role = Role.Checkbox,
                        onValueChange = { acceptedTone = it }
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(checked = acceptedTone, onCheckedChange = null)
                Text(
                    "Я согласен на выбранный уровень мата и прямых оскорблений в приложении и напоминаниях. Уровень можно сменить, напоминания отключить в настройках; текст уведомления может быть виден на экране блокировки.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f).padding(top = 10.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
        }
        Button(
            onClick = {
                vm.completeOnboarding(name.trim(), level)
                if (firstRun && name.trim().isNotEmpty()) {
                    vm.addHabit("Отжаться 50 раз", "◼", level, name.trim(), announce = false)
                }
            },
            enabled = acceptedTone,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground, contentColor = MaterialTheme.colorScheme.background)
        ) { Text(if (firstRun) "СОГЛАСЕН · НАЧАТЬ" else "СОГЛАСЕН · ПРОДОЛЖИТЬ", style = MaterialTheme.typography.labelLarge) }
        Spacer(Modifier.height(12.dp))
    }
}
