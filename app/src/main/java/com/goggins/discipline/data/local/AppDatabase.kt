package com.goggins.discipline.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Habit::class, Completion::class, AccountabilityNote::class, ChallengeProgress::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}
