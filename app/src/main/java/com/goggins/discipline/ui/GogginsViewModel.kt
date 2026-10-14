package com.goggins.discipline.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.goggins.discipline.data.content.Challenges
import com.goggins.discipline.data.local.AppDatabase
import com.goggins.discipline.data.local.ChallengeProgress
import com.goggins.discipline.data.local.Completion
import com.goggins.discipline.data.local.Habit
import com.goggins.discipline.data.repo.HabitRepository
import com.goggins.discipline.data.repo.todayKey
import com.goggins.discipline.data.settings.AppSettings
import com.goggins.discipline.data.settings.ProfanityLevel
import com.goggins.discipline.data.settings.SettingsManager
import com.goggins.discipline.workers.ReminderWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.concurrent.TimeUnit

data class HabitUi(val habit: Habit, val done: Boolean, val streak: Int)

class GogginsViewModel(app: Application) : AndroidViewModel(app) {

    private val db = Room.databaseBuilder(app, AppDatabase::class.java, "goggins.db")
        .fallbackToDestructiveMigration().build()
    private val repo = HabitRepository(db)
    private val settingsManager = SettingsManager(app)

    val settings: StateFlow<AppSettings> = settingsManager.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    private val _today = MutableStateFlow<List<Completion>>(emptyList())
    private val _all = MutableStateFlow<List<Completion>>(emptyList())
    private val _note = MutableStateFlow("")
    private val _challenges = MutableStateFlow<List<ChallengeProgress>>(emptyList())

    /** Последний вердикт Гоггинса (похвала/пинок). Показывается диалогом. */
    private val _verdict = MutableStateFlow<String?>(null)
    val verdict = _verdict.asStateFlow()
    private val _verdictBad = MutableStateFlow(false)
    val verdictBad = _verdictBad.asStateFlow()
    private val _shakeKey = MutableStateFlow(0)
    val shakeKey = _shakeKey.asStateFlow()

    val ui: StateFlow<List<HabitUi>> = combine(_habits, _today, _all) { habits, today, all ->
        val doneIds = today.map { it.habitId }.toSet()
        habits.map { h ->
            HabitUi(h, doneIds.contains(h.id), streakFor(h.id, all))
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val todayCompletions = _today.asStateFlow()
    val allCompletions = _all.asStateFlow()
    val noteText = _note.asStateFlow()
    val challengeState = _challenges.asStateFlow()

    val progress: StateFlow<Float> = combine(_habits, _today) { h, t ->
        if (h.isEmpty()) 0f else t.size.toFloat() / h.size.toFloat()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    init {
        viewModelScope.launch { repo.habits().collect { _habits.value = it } }
        viewModelScope.launch { repo.completionsToday().collect { _today.value = it } }
        viewModelScope.launch { repo.allCompletions().collect { _all.value = it } }
        viewModelScope.launch { repo.challenges().collect { _challenges.value = it } }
        viewModelScope.launch {
            repo.note(todayKey()).collect { _note.value = it?.text.orEmpty() }
        }
        viewModelScope.launch {
            settings.collect { s -> scheduleReminder(s.reminderEnabled, s.reminderHour, s.reminderMinute) }
        }
    }

    // ---------- habits ----------
    fun addHabit(title: String, icon: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repo.addHabit(title, icon.ifBlank { "◼" })
            sayGood()
        }
    }

    fun deleteHabit(h: Habit) {
        viewModelScope.launch { repo.deleteHabit(h) }
    }

    fun toggle(h: HabitUi, profanity: ProfanityLevel) {
        viewModelScope.launch {
            val makeDone = !h.done
            repo.toggle(h.habit.id, todayKey(), makeDone)
            if (makeDone) sayGood() else sayBad(profanity)
        }
    }

    fun saveNote(text: String) {
        viewModelScope.launch { repo.saveNote(todayKey(), text) }
    }

    // ---------- verdicts ----------
    private fun sayGood() {
        val pool = listOf(
            "Галочка стоит. Так держать.",
            "Слово сдержано. Это и есть свобода.",
            "Боль прошла. Гордость осталась. STAY HARD."
        )
        _verdictBad.value = false
        _verdict.value = pool.random()
    }

    private fun sayBad(level: ProfanityLevel) {
        _verdictBad.value = true
        _shakeKey.value += 1
        _verdict.value = com.goggins.discipline.data.content.VerbalAssault.insult(level)
    }

    fun punchMe(level: ProfanityLevel) {
        _verdictBad.value = true
        _shakeKey.value += 1
        _verdict.value = com.goggins.discipline.data.content.VerbalAssault.insult(level)
    }

    fun dismissVerdict() { _verdict.value = null }

    // ---------- challenges ----------
    fun toggleChallengeDay(challengeId: String) {
        viewModelScope.launch {
            val total = Challenges.all.firstOrNull { it.id == challengeId }?.days ?: 1
            val cur = _challenges.value.firstOrNull { it.challengeId == challengeId }
            val next = (cur?.daysDone ?: 0) + 1
            repo.saveChallenge(
                ChallengeProgress(challengeId, cur?.startedAt ?: System.currentTimeMillis(), next.coerceAtMost(total), next >= total)
            )
        }
    }

    fun resetChallenge(challengeId: String) {
        viewModelScope.launch { repo.saveChallenge(ChallengeProgress(challengeId, System.currentTimeMillis(), 0, false)) }
    }

    // ---------- settings ----------
    fun setName(v: String) = viewModelScope.launch { settingsManager.setName(v) }
    fun setProfanity(v: ProfanityLevel) = viewModelScope.launch { settingsManager.setProfanity(v) }
    fun setDark(v: Boolean) = viewModelScope.launch { settingsManager.setDark(v) }
    fun setReminder(e: Boolean, h: Int, m: Int) = viewModelScope.launch { settingsManager.setReminder(e, h, m) }
    fun setOnboarded() = viewModelScope.launch { settingsManager.setOnboarded() }

    fun wipeAll() {
        viewModelScope.launch {
            repo.wipeAll()
            settingsManager.resetAll()
        }
    }

    private fun scheduleReminder(enabled: Boolean, h: Int, m: Int) {
        val wm = WorkManager.getInstance(getApplication())
        if (!enabled) {
            wm.cancelUniqueWork(ReminderWorker.TAG)
            return
        }
        val now = java.util.Calendar.getInstance()
        val target = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, h); set(java.util.Calendar.MINUTE, m); set(java.util.Calendar.SECOND, 0)
            if (before(now)) add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        val delayMin = ((target.timeInMillis - now.timeInMillis) / 60000L).coerceAtLeast(5)
        val req = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delayMin, TimeUnit.MINUTES)
            .addTag(ReminderWorker.TAG).build()
        wm.enqueueUniquePeriodicWork(ReminderWorker.TAG, ExistingPeriodicWorkPolicy.UPDATE, req)
    }

    companion object {
        /** Стрик: подряд идущие дни назад от сегодня/вчера. */
        fun streakFor(habitId: Long, all: List<Completion>): Int {
            val dates = all.filter { it.habitId == habitId }.map { it.date }.toSet()
            var streak = 0
            var day: LocalDate = LocalDate.now()
            if (!dates.contains(day.toString())) day = day.minusDays(1)
            while (dates.contains(day.toString())) {
                streak++
                day = day.minusDays(1)
            }
            return streak
        }

        /** Карта последних N дней: дата -> доля выполненного (0..1). Упрощённо по count. */
        fun heatmap(all: List<Completion>, habitCount: Int, days: Int = 28): List<Pair<String, Float>> {
            if (habitCount == 0) return emptyList()
            val byDate = all.groupBy { it.date }
            val out = mutableListOf<Pair<String, Float>>()
            var d = LocalDate.now().minusDays((days - 1).toLong())
            repeat(days) {
                val k = d.toString()
                val v = (byDate[k]?.size ?: 0).toFloat() / habitCount.toFloat()
                out.add(k to v.coerceIn(0f, 1f))
                d = d.plusDays(1)
            }
            return out
        }
    }
}
