package com.example.tasks

import android.app.Application
import com.example.tasks.data.TaskDatabase
import com.example.tasks.data.TasksRepository

class TasksApplication : Application() {
    val database by lazy { TaskDatabase.getDatabase(this) }
    val repository by lazy { 
        TasksRepository(
            database.taskDao(), 
            database.workspaceDao(),
            database.checklistDao(),
            database.habitDao()
        ) 
    }
}
