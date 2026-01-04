package com.example.tasks.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("SELECT * FROM habit_history WHERE habitId = :habitId")
    fun getHistoryForHabit(habitId: String): Flow<List<HabitHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HabitHistory)

    @Query("DELETE FROM habit_history WHERE habitId = :habitId AND date = :date")
    suspend fun deleteHistory(habitId: String, date: String)
}
