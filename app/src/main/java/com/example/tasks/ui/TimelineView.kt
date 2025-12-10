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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.tasks.data.Task
import com.example.tasks.ui.TaskItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TimelineView(
    tasksWithChecklists: List<com.example.tasks.data.TaskWithChecklist>,
    workspaces: Map<Int, com.example.tasks.data.Workspace> = emptyMap(),
    onCheckedChange: (Task, Boolean) -> Unit,
    onChecklistItemChange: (com.example.tasks.data.ChecklistItem) -> Unit,
    onDelete: (Task) -> Unit,
    onEdit: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedTasks = remember(tasksWithChecklists) {
        tasksWithChecklists.sortedBy { it.task.deadline }.groupBy {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.format(Date(it.task.deadline))
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        for ((dateString, tasksForDate) in groupedTasks) {
            item {
                TimelineHeader(dateString = dateString)
            }
            itemsIndexed(tasksForDate) { index, taskWithChecklist ->
                val task = taskWithChecklist.task
                val workspace = if (task.workspaceId != null) workspaces[task.workspaceId] else null
                TimelineTaskItem(
                    task = task,
                    checklist = taskWithChecklist.checklist,
                    isLast = index == tasksForDate.lastIndex,
                    workspaceName = workspace?.name,
                    workspaceColor = workspace?.color,
                    onCheckedChange = onCheckedChange,
                    onChecklistItemChange = onChecklistItemChange,
                    onDelete = onDelete,
                    onEdit = onEdit
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
    checklist: List<com.example.tasks.data.ChecklistItem> = emptyList(),
    isLast: Boolean,
    workspaceName: String? = null,
    workspaceColor: Long? = null,
    onCheckedChange: (Task, Boolean) -> Unit,
    onChecklistItemChange: (com.example.tasks.data.ChecklistItem) -> Unit,
    onDelete: (Task) -> Unit,
    onEdit: (Task) -> Unit
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
            Column {
                TaskItem(
                    task = task,
                    workspaceName = workspaceName,
                    workspaceColor = workspaceColor,
                    onCheckedChange = onCheckedChange,
                    onDelete = onDelete,
                    onEdit = onEdit
                )
                
                // Inline Checklist
                if (checklist.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    checklist.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, bottom = 4.dp)
                                .clickable { onChecklistItemChange(item) }
                        ) {
                            Checkbox(
                                checked = item.isCompleted,
                                onCheckedChange = { onChecklistItemChange(item) },
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.text,
                                style = MaterialTheme.typography.bodySmall,
                                textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null,
                                color = if (item.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
