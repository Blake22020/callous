package com.goggins.discipline.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("goggins_settings")

data class AppSettings(
    val userName: String = "",
    val profanity: ProfanityLevel = ProfanityLevel.MILD,
    val darkTheme: Boolean = true,
    val reminderEnabled: Boolean = true,
    val reminderHour: Int = 6,
    val reminderMinute: Int = 0,
    val onboarded: Boolean = false,
    val toneConsentAccepted: Boolean = false,
    val appliedPowerLawDate: String = ""
)

class SettingsManager(private val context: Context) {

    private object K {
        val NAME = stringPreferencesKey("user_name")
        val PROF = intPreferencesKey("profanity")
        val DARK = booleanPreferencesKey("dark")
        val REM = booleanPreferencesKey("reminder_enabled")
        val RH = intPreferencesKey("reminder_h")
        val RM = intPreferencesKey("reminder_m")
        val ONB = booleanPreferencesKey("onboarded")
        val TONE_CONSENT = booleanPreferencesKey("tone_consent_accepted")
        val APPLIED_POWER_LAW_DATE = stringPreferencesKey("applied_power_law_date")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { p ->
        AppSettings(
            userName = p[K.NAME].orEmpty(),
            profanity = ProfanityLevel.fromOrdinal(p[K.PROF] ?: 1),
            darkTheme = p[K.DARK] ?: true,
            reminderEnabled = p[K.REM] ?: true,
            reminderHour = p[K.RH] ?: 6,
            reminderMinute = p[K.RM] ?: 0,
            onboarded = p[K.ONB] ?: false,
            toneConsentAccepted = p[K.TONE_CONSENT] ?: false,
            appliedPowerLawDate = p[K.APPLIED_POWER_LAW_DATE].orEmpty()
        )
    }

    suspend fun setName(v: String) = context.dataStore.edit { it[K.NAME] = v }
    suspend fun setProfanity(v: ProfanityLevel) = context.dataStore.edit { it[K.PROF] = v.ordinal }
    suspend fun setDark(v: Boolean) = context.dataStore.edit { it[K.DARK] = v }
    suspend fun setReminder(enabled: Boolean, h: Int, m: Int) = context.dataStore.edit {
        it[K.REM] = enabled; it[K.RH] = h; it[K.RM] = m
    }
    suspend fun completeOnboarding(name: String, profanity: ProfanityLevel) = context.dataStore.edit {
        it[K.NAME] = name
        it[K.PROF] = profanity.ordinal
        it[K.ONB] = true
        it[K.TONE_CONSENT] = true
    }
    suspend fun markPowerLawApplied(date: String) = context.dataStore.edit {
        it[K.APPLIED_POWER_LAW_DATE] = date
    }
    suspend fun resetAll() = context.dataStore.edit { it.clear() }
}
