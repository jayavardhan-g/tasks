package com.example.tasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.tasks.ui.TaskScreen
import com.example.tasks.ui.TasksViewModel
import com.example.tasks.ui.TasksViewModelFactory
import com.example.tasks.ui.theme.TasksTheme

class MainActivity : ComponentActivity() {

    private val tasksViewModel: TasksViewModel by viewModels {
        TasksViewModelFactory((application as TasksApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TasksTheme {
                TaskScreen(viewModel = tasksViewModel)
            }
        }
    }
}
