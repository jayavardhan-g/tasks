package com.example.tasks.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.tasks.data.Task
import com.example.tasks.data.TasksRepository
import kotlinx.coroutines.launch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.switchMap
import com.example.tasks.data.Workspace
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest

class TasksViewModel(private val repository: TasksRepository) : ViewModel() {

    val workspaces: LiveData<List<Workspace>> = repository.allWorkspaces.asLiveData()
    
    private val _currentWorkspaceId = MutableLiveData<Int>(-1)
    val currentWorkspaceId: LiveData<Int> = _currentWorkspaceId

    // For Workspace Tab (Filtered)
    // For Workspace Tab (Filtered)
    val filteredTasks: LiveData<List<com.example.tasks.data.TaskWithChecklist>> = _currentWorkspaceId.switchMap { id ->
        if (id == -1) { // -1 represents "All" in the workspace tab
             repository.allTasks.asLiveData()
        } else {
             repository.getTasksByWorkspace(id).asLiveData()
        }
    }

    // For Timeline Tab (Global)
    val globalTasks: LiveData<List<com.example.tasks.data.TaskWithChecklist>> = repository.allTasks.asLiveData()

    fun setWorkspace(workspaceId: Int) {
        _currentWorkspaceId.value = workspaceId
    }

    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }

    fun insertWorkspace(workspace: Workspace) = viewModelScope.launch {
        repository.insertWorkspace(workspace)
    }

    fun deleteWorkspace(workspace: Workspace) = viewModelScope.launch {
        repository.deleteWorkspace(workspace)
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
    }

    fun updateTaskWithChecklist(task: Task, items: List<com.example.tasks.data.ChecklistItem>) = viewModelScope.launch {
        repository.update(task)
        
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
        
        newItems.forEach { 
            repository.insertChecklistItem(it.copy(taskId = task.id)) 
        }
        updatedItems.forEach { 
            repository.updateChecklistItem(it) 
        }
        deletedItems.forEach { 
            repository.deleteChecklistItem(it) 
        }
        
        checkTaskCompletion(task.id)
    }
}

class TasksViewModelFactory(private val repository: TasksRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
