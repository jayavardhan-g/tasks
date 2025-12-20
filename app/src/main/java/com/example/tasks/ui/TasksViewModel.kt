package com.example.tasks.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.tasks.data.Task
import com.example.tasks.data.TasksRepository
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import com.example.tasks.data.Workspace
import com.example.tasks.data.ChecklistItem
import com.example.tasks.data.TaskWithChecklist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.tasks.util.PreferenceManager

class TasksViewModel(
    application: Application,
    private val repository: TasksRepository
) : AndroidViewModel(application) {

    val workspaces: LiveData<List<Workspace>> = repository.allWorkspaces.asLiveData()
    
    private val _currentWorkspaceId = MutableStateFlow(-1)
    val currentWorkspaceId: LiveData<Int> = _currentWorkspaceId.asLiveData()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentMatchIndex = MutableStateFlow(0)
    val currentMatchIndex: StateFlow<Int> = _currentMatchIndex.asStateFlow()

    private val preferenceManager = PreferenceManager(getApplication())
    private val _timelineMode = MutableStateFlow(preferenceManager.getTimelineMode())
    val timelineMode: StateFlow<String> = _timelineMode.asStateFlow()

    // For Workspace Tab (Full)
    val filteredTasks: LiveData<List<com.example.tasks.data.TaskWithChecklist>> = 
        _currentWorkspaceId.flatMapLatest { id ->
            if (id == -1) repository.allTasks else repository.getTasksByWorkspace(id)
        }.asLiveData()

    // For Timeline Tab (Full)
    val globalTasks: LiveData<List<com.example.tasks.data.TaskWithChecklist>> = 
        repository.allTasks.asLiveData()

    // List of task IDs matching the search query in the current context
    val matches: LiveData<List<Int>> = combine(
        _searchQuery,
        _currentWorkspaceId.flatMapLatest { id ->
            if (id == -1) repository.allTasks else repository.getTasksByWorkspace(id)
        }
    ) { query, tasks ->
        if (query.isBlank()) emptyList()
        else tasks.filter {
            it.task.title.contains(query, ignoreCase = true) ||
            it.task.description.contains(query, ignoreCase = true)
        }.map { it.task.id }
    }.asLiveData()

    fun navigateNextMatch() {
        val count = matches.value?.size ?: 0
        if (count > 0) {
            _currentMatchIndex.value = (_currentMatchIndex.value + 1) % count
        }
    }

    fun navigatePreviousMatch() {
        val count = matches.value?.size ?: 0
        if (count > 0) {
            _currentMatchIndex.value = (_currentMatchIndex.value - 1 + count) % count
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _currentMatchIndex.value = 0
    }

    fun toggleTimelineMode() {
        val newMode = if (_timelineMode.value == "DEFAULT") "COLOR" else "DEFAULT"
        _timelineMode.value = newMode
        preferenceManager.setTimelineMode(newMode)
    }

    fun setWorkspace(workspaceId: Int) {
        _currentWorkspaceId.value = workspaceId
    }

    fun insert(task: Task) = viewModelScope.launch {
        val id = repository.insert(task)
        if (task.pinAsNotification && !task.isCompleted) {
             com.example.tasks.util.NotificationHelper.showTaskNotification(getApplication(), task.copy(id = id.toInt()))
        }
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
        val context = getApplication<Application>()
        if (task.pinAsNotification && !task.isCompleted) {
             com.example.tasks.util.NotificationHelper.showTaskNotification(context, task)
        } else {
             com.example.tasks.util.NotificationHelper.cancelTaskNotification(context, task.id)
        }
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
        com.example.tasks.util.NotificationHelper.cancelTaskNotification(getApplication(), task.id)
    }

    fun insertWorkspace(workspace: Workspace) = viewModelScope.launch {
        repository.insertWorkspace(workspace)
    }

    fun deleteWorkspace(workspace: Workspace) = viewModelScope.launch {
        repository.deleteWorkspace(workspace)
    }

    fun toggleArchiveWorkspace(workspace: Workspace) = viewModelScope.launch {
        repository.insertWorkspace(workspace.copy(isArchived = !workspace.isArchived))
    }

    fun updateWorkspace(workspace: Workspace) = viewModelScope.launch {
        repository.insertWorkspace(workspace)
    }

    // Checklist Logic
    fun getChecklist(taskId: Int): LiveData<List<com.example.tasks.data.ChecklistItem>> {
        return repository.getChecklistForTask(taskId).asLiveData()
    }

    fun addChecklistItem(item: com.example.tasks.data.ChecklistItem) = viewModelScope.launch {
        repository.insertChecklistItem(item)
    }

    fun deleteChecklistItem(item: com.example.tasks.data.ChecklistItem) = viewModelScope.launch {
        repository.deleteChecklistItem(item)
    }

    fun toggleChecklistItem(item: com.example.tasks.data.ChecklistItem) = viewModelScope.launch {
        val updatedItem = item.copy(isCompleted = !item.isCompleted)
        repository.updateChecklistItem(updatedItem)
        
        // Auto-complete Task logic
        if (updatedItem.isCompleted) {
             checkTaskCompletion(item.taskId)
        }
    }
    
    private suspend fun checkTaskCompletion(taskId: Int) {
         try {
             val items = repository.getChecklistForTask(taskId).first()
             if (items.isNotEmpty() && items.all { it.isCompleted }) {
                 repository.updateTaskStatus(taskId, true)
                 com.example.tasks.util.NotificationHelper.cancelTaskNotification(getApplication(), taskId)
             }
         } catch (e: Exception) {
             // Handle empty flow or errors
         }
    }

    fun insertTaskWithChecklist(task: Task, items: List<com.example.tasks.data.ChecklistItem>) = viewModelScope.launch {
        val taskId = repository.insert(task) // Ensure insert returns Long
        items.forEach { item ->
            repository.insertChecklistItem(item.copy(taskId = taskId.toInt()))
        }
        
        if (task.pinAsNotification && !task.isCompleted) {
             com.example.tasks.util.NotificationHelper.showTaskNotification(getApplication(), task.copy(id = taskId.toInt()))
        }
    }

    fun updateTaskWithChecklist(task: Task, items: List<com.example.tasks.data.ChecklistItem>) = viewModelScope.launch {
        repository.update(task)
        
        val context = getApplication<Application>()
        if (task.pinAsNotification && !task.isCompleted) {
             com.example.tasks.util.NotificationHelper.showTaskNotification(context, task)
        } else {
             com.example.tasks.util.NotificationHelper.cancelTaskNotification(context, task.id)
        }
        
        // Sync checklist:
        // 1. Get current DB items
        val currentItems = repository.getChecklistForTask(task.id).first()
        
        // 2. Determine to Add, Update, or Delete
        // Items in 'items' with id=0 are NEW.
        // Items in 'items' with id!=0 are UPDATES (change text/completed).
        // Items in 'currentItems' NOT in 'items' are DELETED.
        
        val newItems = items.filter { it.id == 0 }
        val updatedItems = items.filter { it.id != 0 }
        val updatedIds = updatedItems.map { it.id }.toSet()
        val deletedItems = currentItems.filter { it.id !in updatedIds }
        
        for (item in newItems) {
            repository.insertChecklistItem(item.copy(taskId = task.id))
        }
        for (item in updatedItems) {
            repository.updateChecklistItem(item)
        }
        for (item in deletedItems) {
            repository.deleteChecklistItem(item)
        }
        
        checkTaskCompletion(task.id)
    }
}

class TasksViewModelFactory(private val application: Application, private val repository: TasksRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasksViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
