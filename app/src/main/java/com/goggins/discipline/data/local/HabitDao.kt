package com.goggins.discipline.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val icon: String = "◼",
    val targetPerDay: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val archived: Boolean = false
)

@Entity(tableName = "completions", primaryKeys = ["habitId", "date"])
data class Completion(
    val habitId: Long,
    /** yyyy-MM-dd в локальной зоне */
    val date: String,
    val count: Int = 1,
    val doneAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes", primaryKeys = ["date"])
data class AccountabilityNote(
    val date: String,
    val text: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "challenge_progress", primaryKeys = ["challengeId"])
data class ChallengeProgress(
    val challengeId: String,
    val startedAt: Long = System.currentTimeMillis(),
    val daysDone: Int = 0,
    val finished: Boolean = false
)

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE archived = 0 ORDER BY createdAt ASC")
    fun observeHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun habitById(id: Long): Habit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHabit(h: Habit): Long

    @Delete
    suspend fun deleteHabit(h: Habit)

    @Query("UPDATE habits SET archived = 1 WHERE id = :id")
    suspend fun archive(id: Long)

    @Query("SELECT * FROM completions WHERE date = :date")
    fun observeCompletions(date: String): Flow<List<Completion>>

    @Query("SELECT * FROM completions WHERE habitId = :habitId ORDER BY date DESC")
    fun observeCompletionsForHabit(habitId: Long): Flow<List<Completion>>

    @Query("SELECT * FROM completions ORDER BY date DESC LIMIT 5000")
    fun observeAllCompletions(): Flow<List<Completion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCompletion(c: Completion)

    @Query("DELETE FROM completions WHERE habitId = :habitId AND date = :date")
    suspend fun clearCompletion(habitId: Long, date: String)

    @Query("SELECT * FROM notes WHERE date = :date")
    fun observeNote(date: String): Flow<AccountabilityNote?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNote(n: AccountabilityNote)

    @Query("SELECT * FROM challenge_progress")
    fun observeChallenges(): Flow<List<ChallengeProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveChallengeProgress(p: ChallengeProgress)

    @Query("DELETE FROM completions")
    suspend fun clearAllCompletions()

    @Query("DELETE FROM habits")
    suspend fun clearHabits()
}
