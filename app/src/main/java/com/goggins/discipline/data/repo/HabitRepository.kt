package com.goggins.discipline.data.repo

import com.goggins.discipline.data.local.AccountabilityNote
import com.goggins.discipline.data.local.AppDatabase
import com.goggins.discipline.data.local.ChallengeProgress
import com.goggins.discipline.data.local.Completion
import com.goggins.discipline.data.local.Habit
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

fun todayKey(): String = LocalDate.now().toString()

class HabitRepository(private val db: AppDatabase) {
    private val dao = db.habitDao()

    fun habits(): Flow<List<Habit>> = dao.observeHabits()
    fun completionsToday(): Flow<List<Completion>> = dao.observeCompletions(todayKey())
    fun allCompletions(): Flow<List<Completion>> = dao.observeAllCompletions()
    fun completionsFor(habitId: Long): Flow<List<Completion>> = dao.observeCompletionsForHabit(habitId)
    fun note(date: String): Flow<AccountabilityNote?> = dao.observeNote(date)
    fun challenges(): Flow<List<ChallengeProgress>> = dao.observeChallenges()

    suspend fun addHabit(title: String, icon: String): Long =
        dao.upsertHabit(Habit(title = title.trim(), icon = icon))

    suspend fun updateHabit(h: Habit) = dao.upsertHabit(h)

    suspend fun deleteHabit(h: Habit) = dao.deleteHabit(h)

    suspend fun toggle(habitId: Long, date: String = todayKey(), makeDone: Boolean) {
        if (makeDone) dao.setCompletion(Completion(habitId, date))
        else dao.clearCompletion(habitId, date)
    }

    suspend fun saveNote(date: String, text: String) =
        dao.saveNote(AccountabilityNote(date, text))

    suspend fun saveChallenge(p: ChallengeProgress) = dao.saveChallengeProgress(p)

    suspend fun wipeAll() {
        dao.clearAllCompletions()
        dao.clearHabits()
    }
}
