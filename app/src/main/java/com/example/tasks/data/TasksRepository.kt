package com.example.tasks.data

import kotlinx.coroutines.flow.Flow

class TasksRepository(
    private val taskDao: TaskDao,
    private val workspaceDao: WorkspaceDao,
    private val checklistDao: ChecklistDao,
    private val habitDao: HabitDao
) {
    val allTasks: Flow<List<TaskWithChecklist>> = taskDao.getTasks()
    val allWorkspaces: Flow<List<Workspace>> = workspaceDao.getAllWorkspaces()
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()

    fun getTasksByWorkspace(workspaceId: Int): Flow<List<TaskWithChecklist>> {
        return taskDao.getTasksByWorkspace(workspaceId)
    }

    suspend fun insert(task: Task): Long {
        return taskDao.insertTask(task)
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }
    
    suspend fun updateTaskStatus(taskId: Int, isCompleted: Boolean) {
        taskDao.updateTaskStatus(taskId, isCompleted)
    }

    suspend fun insertWorkspace(workspace: Workspace) {
        workspaceDao.insert(workspace)
    }

    suspend fun deleteWorkspace(workspace: Workspace) {
        workspaceDao.delete(workspace)
    }
    
    fun getChecklistForTask(taskId: Int): Flow<List<ChecklistItem>> {
        return checklistDao.getItemsForTask(taskId)
    }

    suspend fun insertChecklistItem(item: ChecklistItem) {
        checklistDao.insert(item)
    }

    suspend fun updateChecklistItem(item: ChecklistItem) {
        checklistDao.update(item)
    }

    suspend fun deleteChecklistItem(item: ChecklistItem) {
        checklistDao.delete(item)
    }
    
    suspend fun deleteChecklistByTask(taskId: Int) {
        checklistDao.deleteByTaskId(taskId)
    }

    // Habits
    suspend fun insertHabit(habit: Habit) {
        habitDao.insertHabit(habit)
    }

    suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit)
    }

    fun getHistoryForHabit(habitId: String): Flow<List<HabitHistory>> {
        return habitDao.getHistoryForHabit(habitId)
    }

    suspend fun insertHabitHistory(history: HabitHistory) {
        habitDao.insertHistory(history)
    }

    suspend fun deleteHabitHistory(habitId: String, date: String) {
        habitDao.deleteHistory(habitId, date)
    }
}
