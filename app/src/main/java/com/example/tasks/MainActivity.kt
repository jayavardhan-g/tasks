package com.example.tasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.tasks.data.Task
import com.example.tasks.ui.NewTaskScreen
import com.example.tasks.ui.TaskScreen
import com.example.tasks.ui.TasksViewModel
import com.example.tasks.ui.TasksViewModelFactory
import androidx.compose.runtime.livedata.observeAsState
import com.example.tasks.ui.theme.TasksTheme

class MainActivity : ComponentActivity() {

    private val tasksViewModel: TasksViewModel by viewModels {
        TasksViewModelFactory((application as TasksApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TasksTheme {
                TasksApp(viewModel = tasksViewModel)
            }
        }
    }
}

sealed interface Screen {
    object Home : Screen
    object NewTask : Screen
    data class EditTask(val task: Task, val checklist: List<com.example.tasks.data.ChecklistItem>) : Screen
}

@Composable
fun TasksApp(viewModel: TasksViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    
    // Helper to switch screen
    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    when (val screen = currentScreen) {
        is Screen.Home -> {
            TaskScreen(
                viewModel = viewModel,
                onNavigateToNewTask = { navigateTo(Screen.NewTask) },
                onEditTask = { task, checklist -> 
                    navigateTo(Screen.EditTask(task, checklist))
                }
            )
        }
        is Screen.NewTask -> {
            val globalTasks by viewModel.globalTasks.observeAsState(emptyList())
            val workspaces by viewModel.workspaces.observeAsState(emptyList())
            
            NewTaskScreen(
                workspaces = workspaces,
                onNavigateBack = { navigateTo(Screen.Home) },
                onSave = { title, desc, deadline, workspaceId, priority, tags, checklistItems ->
                     val task = Task(
                        title = title,
                        description = desc,
                        deadline = deadline,
                        workspaceId = workspaceId,
                        priority = priority,
                        tags = tags
                    )
                    viewModel.insertTaskWithChecklist(task, checklistItems)
                    navigateTo(Screen.Home)
                },
                onAddWorkspace = { name, color ->
                    viewModel.insertWorkspace(com.example.tasks.data.Workspace(name = name, color = color.toLong()))
                }
            )
        }
        is Screen.EditTask -> {
            val workspaces by viewModel.workspaces.observeAsState(emptyList())

            NewTaskScreen(
                workspaces = workspaces,
                taskToEdit = screen.task,
                initialChecklist = screen.checklist,
                onNavigateBack = { navigateTo(Screen.Home) },
                onSave = { title, desc, deadline, workspaceId, priority, tags, checklistItems ->
                     val task = screen.task.copy(
                        title = title,
                        description = desc,
                        deadline = deadline,
                        workspaceId = workspaceId,
                        priority = priority,
                        tags = tags
                     )
                    viewModel.updateTaskWithChecklist(task, checklistItems)
                    navigateTo(Screen.Home)
                },
                onAddWorkspace = { name, color ->
                    viewModel.insertWorkspace(com.example.tasks.data.Workspace(name = name, color = color.toLong()))
                }
            )
        }
    }
}
