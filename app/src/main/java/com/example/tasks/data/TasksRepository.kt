package com.example.tasks.data

import kotlinx.coroutines.flow.Flow

class TasksRepository(
    private val taskDao: TaskDao,
    private val workspaceDao: WorkspaceDao
) {
    val allTasks: Flow<List<Task>> = taskDao.getTasks()
    val allWorkspaces: Flow<List<Workspace>> = workspaceDao.getAllWorkspaces()

    fun getTasksByWorkspace(workspaceId: Int): Flow<List<Task>> {
        return taskDao.getTasksByWorkspace(workspaceId)
    }

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun insertWorkspace(workspace: Workspace) {
        workspaceDao.insert(workspace)
    }

    suspend fun deleteWorkspace(workspace: Workspace) {
        workspaceDao.delete(workspace)
    }
}
