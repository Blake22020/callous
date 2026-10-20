package com.goggins.discipline.data.productivity

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.productivityStore by preferencesDataStore("callous_productivity")

data class DailyProgress(
    val date: String,
    val work: String = "",
    val discipline: String = "",
    val respect: String = "",
    val workDone: Boolean = false,
    val disciplineDone: Boolean = false,
    val respectDone: Boolean = false,
    val focusStartedAtMillis: Long = 0L,
    val focusMinutes: Int = 0,
    val focusSessions: Int = 0,
    val proof: String = ""
) {
    val commitmentsDone: Int get() = listOf(workDone, disciplineDone, respectDone).count { it }
    val commitmentsPlanned: Int get() = listOf(work, discipline, respect).count { it.isNotBlank() }
}

data class ProductivitySnapshot(val days: List<DailyProgress> = emptyList()) {
    fun forDate(date: String, defaultRespect: String = ""): DailyProgress =
        days.firstOrNull { it.date == date } ?: DailyProgress(date = date, respect = defaultRespect)
}

class ProductivityManager(private val context: Context) {
    private val recordsKey = stringPreferencesKey("daily_records")

    val snapshot: Flow<ProductivitySnapshot> = context.productivityStore.data
        .map { preferences -> ProductivitySnapshot(decode(preferences[recordsKey].orEmpty())) }

    suspend fun ensureDay(date: String, defaultRespect: String) {
        updateDay(date, defaultRespect) { it }
    }

    suspend fun clear() {
        context.productivityStore.edit { it.clear() }
    }

    suspend fun updateDay(
        date: String,
        defaultRespect: String = "",
        update: (DailyProgress) -> DailyProgress
    ): DailyProgress {
        var updatedDay = DailyProgress(date = date, respect = defaultRespect)
        context.productivityStore.edit { preferences ->
            val days = decode(preferences[recordsKey].orEmpty()).associateBy { it.date }.toMutableMap()
            val current = days[date] ?: DailyProgress(date = date, respect = defaultRespect)
            updatedDay = update(current)
            days[date] = updatedDay
            val recent = days.values.sortedBy { it.date }.takeLast(120)
            preferences[recordsKey] = encode(recent)
        }
        return updatedDay
    }

    private fun encode(days: List<DailyProgress>): String = JSONArray().apply {
        days.forEach { day ->
            put(JSONObject().apply {
                put("date", day.date)
                put("work", day.work)
                put("discipline", day.discipline)
                put("respect", day.respect)
                put("workDone", day.workDone)
                put("disciplineDone", day.disciplineDone)
                put("respectDone", day.respectDone)
                put("focusStartedAtMillis", day.focusStartedAtMillis)
                put("focusMinutes", day.focusMinutes)
                put("focusSessions", day.focusSessions)
                put("proof", day.proof)
            })
        }
    }.toString()

    private fun decode(json: String): List<DailyProgress> = runCatching {
        val array = JSONArray(json)
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            val date = item.optString("date")
            if (date.isBlank()) return@mapNotNull null
            DailyProgress(
                date = date,
                work = item.optString("work"),
                discipline = item.optString("discipline"),
                respect = item.optString("respect"),
                workDone = item.optBoolean("workDone"),
                disciplineDone = item.optBoolean("disciplineDone"),
                respectDone = item.optBoolean("respectDone"),
                focusStartedAtMillis = item.optLong("focusStartedAtMillis"),
                focusMinutes = item.optInt("focusMinutes"),
                focusSessions = item.optInt("focusSessions"),
                proof = item.optString("proof")
            )
        }
    }.getOrDefault(emptyList())
}
