package com.goggins.discipline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
        // Запрос разрешения на уведомления на Android 13+ — молча, без падения.
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 41)
        }
        setContent {
            val settings by vm.settings.collectAsState()
            GogginsTheme(dark = settings.darkTheme) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (!settings.onboarded) Onboarding(vm)
                    else AppShell(vm)
                }
            }
        }
    }
}

@Composable
private fun AppShell(vm: GogginsViewModel) {
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
        Box(Modifier.padding(pad)) { NavGraph(nav, vm) }
    }
}

@Composable
private fun Onboarding(vm: GogginsViewModel) {
    var name by remember { mutableStateOf("") }
    var level by remember { mutableStateOf(ProfanityLevel.MILD) }

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
                "ТРЕКЕР ДИСЦИПЛИНЫ",
                style = MaterialTheme.typography.labelLarge,
                color = NothingRed
            )
            Spacer(Modifier.height(14.dp))
            DotMatrix()
            Spacer(Modifier.height(18.dp))
            Text(
                "Никто не придёт тебя спасать.\nЭто приложение — твоё зеркало ответственности: привычки, стрики, испытания и пинки от Гоггинса.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(22.dp))
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Как звать, боец?") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
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
            Spacer(Modifier.height(16.dp))
        }
        Button(
            onClick = {
                vm.setName(name.trim())
                vm.setProfanity(level)
                vm.setOnboarded()
                if (name.trim().isEmpty().not()) vm.addHabit("Отжаться 50 раз", "◼")
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground, contentColor = MaterialTheme.colorScheme.background)
        ) { Text("НАЧАТЬ", style = MaterialTheme.typography.labelLarge) }
        Spacer(Modifier.height(12.dp))
    }
}
