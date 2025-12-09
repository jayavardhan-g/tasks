package com.example.tasks.data

import kotlinx.coroutines.flow.Flow

class TasksRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> = taskDao.getTasks()

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }
}
