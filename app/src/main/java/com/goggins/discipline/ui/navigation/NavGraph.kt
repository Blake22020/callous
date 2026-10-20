package com.goggins.discipline.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.goggins.discipline.ui.GogginsViewModel
import com.goggins.discipline.data.settings.AppSettings
import com.goggins.discipline.ui.screens.ChallengesScreen
import com.goggins.discipline.ui.screens.HomeScreen
import com.goggins.discipline.ui.screens.MindsetScreen
import com.goggins.discipline.ui.screens.SettingsScreen
import com.goggins.discipline.ui.screens.StatsScreen

object Routes {
    const val TODAY = "today"
    const val STATS = "stats"
    const val CHALLENGES = "challenges"
    const val MINDSET = "mindset"
    const val SETTINGS = "settings"
}

@Composable
fun NavGraph(nav: NavHostController, vm: GogginsViewModel, settings: AppSettings) {
    NavHost(nav, startDestination = Routes.TODAY) {
        composable(Routes.TODAY) { HomeScreen(vm, settings, onSettings = { nav.navigate(Routes.SETTINGS) }) }
        composable(Routes.STATS) { StatsScreen(vm) }
        composable(Routes.CHALLENGES) { ChallengesScreen(vm) }
        composable(Routes.MINDSET) { MindsetScreen(vm, settings) }
        composable(Routes.SETTINGS) { SettingsScreen(vm, settings, onBack = { nav.popBackStack() }) }
    }
}
