package com.example.tasks.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "checklist_items",
    foreignKeys = [ForeignKey(
        entity = Task::class,
        parentColumns = ["id"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("taskId")]
)
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskId: Int,
    val text: String,
    val isCompleted: Boolean = false
)
