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
import kotlinx.coroutines.flow.flatMapLatest

class TasksViewModel(private val repository: TasksRepository) : ViewModel() {

    val workspaces: LiveData<List<Workspace>> = repository.allWorkspaces.asLiveData()
    
    private val _currentWorkspaceId = MutableLiveData<Int>(-1)
    val currentWorkspaceId: LiveData<Int> = _currentWorkspaceId

    // For Workspace Tab (Filtered)
    val filteredTasks: LiveData<List<Task>> = _currentWorkspaceId.switchMap { id ->
        if (id == -1) { // -1 represents "All" in the workspace tab
             repository.allTasks.asLiveData()
        } else {
             repository.getTasksByWorkspace(id).asLiveData()
        }
    }

    // For Timeline Tab (Global)
    val globalTasks: LiveData<List<Task>> = repository.allTasks.asLiveData()

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
