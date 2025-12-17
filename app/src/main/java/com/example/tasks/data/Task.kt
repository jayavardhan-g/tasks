package com.example.tasks.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Workspace::class,
            parentColumns = ["id"],
            childColumns = ["workspaceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workspaceId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val deadline: Long, // Timestamp
    val isCompleted: Boolean = false,
    val workspaceId: Int? = null,
    val priority: Int = 0, // 0: None, 1: Low, 2: Medium, 3: High
    val tags: String = "" // Comma separated tags
)

data class TaskDraft(
    val title: String = "",
    val description: String = "",
    val deadline: Long? = null,
    val workspaceId: Int? = null,
    val priority: Int = 0,
    val tags: String = ""
)
