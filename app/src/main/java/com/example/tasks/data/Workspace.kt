package com.example.tasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workspaces")
data class Workspace(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: Long = 0xFFCCCCCC, // Default Gray
    val isArchived: Boolean = false
)
