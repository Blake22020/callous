package com.goggins.discipline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
                    Triple(Routes.TODAY, "СЕГОДНЯ", "◼"),
                    Triple(Routes.STATS, "ДАННЫЕ", "▓"),
                    Triple(Routes.CHALLENGES, "АД", "☠"),
                    Triple(Routes.MINDSET, "РАЗУМ", "◉"),
                )
                items.forEach { (r, label, glyph) ->
                    NavigationBarItem(
                        selected = route == r,
                        onClick = { if (route != r) nav.navigate(r) { launchSingleTop = true } },
                        icon = { Text(glyph, fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 16.sp, color = if (route == r) NothingRed else Color.Gray) },
                        label = { Text(label, fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 9.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor = NothingRed,
                            indicatorColor = Color.Transparent
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
        Spacer(Modifier.height(40.dp))
        Text("CALLOUS.OS", fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 4.sp, color = Color.Gray)
        Text("STAY HARD.", fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontWeight = FontWeight.Black, fontSize = 44.sp)
        Text("ТРЕКЕР ДИСЦИПЛИНЫ", fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 13.sp, color = NothingRed)
        DotMatrix()
        Spacer(Modifier.height(16.dp))
        Text(
            "Никто не придёт тебя спасать.\nЭто приложение — твоё зеркало ответственности: привычки, стрики, испытания и пинки от Гоггинса.",
            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 13.sp
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text("Как звать, боец?", fontFamily = MaterialTheme.typography.bodyLarge.fontFamily) },
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Text("НАСКОЛЬКО ЖЁСТКО С ТОБОЙ ГОВОРИТЬ?", fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 11.sp, color = Color.Gray)
        ProfanityLevel.entries.forEach { l ->
            Button(
                onClick = { level = l },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (level == l) NothingRed else MaterialTheme.colorScheme.surface,
                    contentColor = if (level == l) Color.White else MaterialTheme.colorScheme.onSurface
                )
            ) { Text(l.title, fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 12.sp) }
        }
        Text(level.subtitle, fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 11.sp, color = Color.Gray)
        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                vm.setName(name.trim())
                vm.setProfanity(level)
                vm.setOnboarded()
                if (name.trim().isEmpty().not()) vm.addHabit("Отжаться 50 раз", "◼")
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground, contentColor = MaterialTheme.colorScheme.background)
        ) { Text("НАЧАТЬ СТРАДАТЬ // GO", fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp))
    }
}
