package com.example.tasks.data

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithChecklist(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val checklist: List<ChecklistItem>
)
