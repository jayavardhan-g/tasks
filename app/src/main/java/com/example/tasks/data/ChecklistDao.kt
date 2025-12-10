package com.example.tasks.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist_items WHERE taskId = :taskId ORDER BY id")
    fun getItemsForTask(taskId: Int): Flow<List<ChecklistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ChecklistItem)

    @Update
    suspend fun update(item: ChecklistItem)

    @Delete
    suspend fun delete(item: ChecklistItem)
    
    @Query("DELETE FROM checklist_items WHERE taskId = :taskId")
    suspend fun deleteByTaskId(taskId: Int)
}
