package com.example.tasks.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @androidx.room.Transaction
    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getTasks(): Flow<List<TaskWithChecklist>>

    @androidx.room.Transaction
    @Query("SELECT * FROM tasks WHERE workspaceId = :workspaceId ORDER BY deadline ASC")
    fun getTasksByWorkspace(workspaceId: Int): Flow<List<TaskWithChecklist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Int, isCompleted: Boolean)
}
