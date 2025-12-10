package com.example.tasks.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import com.example.tasks.data.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TimelineView(
    tasks: List<Task>,
    workspaces: Map<Int, String> = emptyMap(),
    onCheckedChange: (Task, Boolean) -> Unit,
    onDelete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedTasks = remember(tasks) {
        tasks.sortedBy { it.deadline }.groupBy {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.format(Date(it.deadline))
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        groupedTasks.forEach { (dateString, tasksForDate) ->
            item {
                TimelineHeader(dateString = dateString)
            }
            items(tasksForDate) { task ->
                TimelineTaskItem(
                    task = task,
                    workspaceName = if (task.workspaceId != null) workspaces[task.workspaceId] else null,
                    onCheckedChange = onCheckedChange,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
fun TimelineHeader(dateString: String) {
    val displayFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val parseFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = parseFormat.parse(dateString) ?: Date()

    Text(
        text = displayFormat.format(date),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun TimelineTaskItem(
    task: Task,
    workspaceName: String? = null,
    onCheckedChange: (Task, Boolean) -> Unit,
    onDelete: (Task) -> Unit
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        // Timeline Line
        Box(modifier = Modifier.width(24.dp)) {
           Canvas(modifier = Modifier.fillMaxSize()) {
               val startX = size.width / 2
               drawLine(
                   color = Color.Gray,
                   start = Offset(startX, 0f),
                   end = Offset(startX, size.height),
                   strokeWidth = 2f
               )
               drawCircle(
                   color = Color.Gray,
                   radius = 6f,
                   center = Offset(startX, size.height / 2)
               )
           }
        }
        
        // Task Item
        Box(modifier = Modifier.weight(1f)) {
            TaskItem(
                task = task,
                workspaceName = workspaceName,
                onCheckedChange = onCheckedChange,
                onDelete = onDelete
            )
        }
    }
}
