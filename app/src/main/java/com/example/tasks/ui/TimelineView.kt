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
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Add
import com.example.tasks.data.Task
import com.example.tasks.ui.components.SummaryCards
import java.text.SimpleDateFormat
import java.util.Calendar
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
    onAddTask: (Date) -> Unit,
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
            val parseFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parseFormat.parse(dateString) ?: Date()
            val today = Calendar.getInstance()
            val headerDate = Calendar.getInstance().apply { time = date }
            val isToday = today.get(Calendar.YEAR) == headerDate.get(Calendar.YEAR) &&
                         today.get(Calendar.DAY_OF_YEAR) == headerDate.get(Calendar.DAY_OF_YEAR)

            item {
                TimelineHeader(dateString = dateString, isToday = isToday)
            }
            
            if (isToday) {
                val now = System.currentTimeMillis()
                val overdueCount = tasksWithChecklists.count { !it.task.isCompleted && it.task.deadline < now }
                val unplannedCount = tasksWithChecklists.count { it.task.deadline == 0L }
                
                item {
                    SummaryCards(
                        todoCount = tasksForDate.count { !it.task.isCompleted },
                        overdueCount = overdueCount,
                        unplannedCount = unplannedCount
                    )
                }
            }

            itemsIndexed(tasksForDate) { index, taskWithChecklist ->
                val task = taskWithChecklist.task
                val workspace = if (task.workspaceId != null) workspaces[task.workspaceId] else null
                TimelineTaskItem(
                    task = task,
                    checklist = taskWithChecklist.checklist,
                    isLast = false, // We'll have an "Add task" item after this
                    workspaceName = workspace?.name,
                    workspaceColor = workspace?.color,
                    onCheckedChange = onCheckedChange,
                    onChecklistItemChange = onChecklistItemChange,
                    onDelete = onDelete,
                    onEdit = onEdit
                )
            }
            
            item {
                TimelineAddTaskRow(
                    date = date,
                    isToday = isToday,
                    onAddTask = onAddTask
                )
            }
        }
    }
}

@Composable
fun TimelineHeader(dateString: String, isToday: Boolean) {
    val displayFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
    val parseFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = parseFormat.parse(dateString) ?: Date()

    Column {
        Text(
            text = displayFormat.format(date),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        if (isToday) {
            Text(
                text = "Today",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
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
        Column(
            modifier = Modifier.width(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(2.dp, Color(0xFF0056B3), CircleShape)
                    .clickable { onCheckedChange(task, !task.isCompleted) },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFF0056B3), CircleShape)
                    )
                }
            }
            
            // Vertical Line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(Color(0xFF0056B3).copy(alpha = 0.5f))
            )
        }
        
        // Task Item Content
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onEdit(task) }
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                Text(
                    text = timeFormat.format(Date(task.deadline)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Repeats Icon (Placeholder)
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Repeat,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Notification Icon
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.NotificationsNone,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
            }
            
            // Inline Checklist
            if (checklist.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                checklist.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                            .clickable { onChecklistItemChange(item) }
                    ) {
                        CircularCheckbox(
                            checked = item.isCompleted,
                            onCheckedChange = { onChecklistItemChange(item) },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.text,
                            style = MaterialTheme.typography.bodySmall,
                            textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null,
                            color = if (item.isCompleted) Color.Gray else Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineAddTaskRow(
    date: Date,
    isToday: Boolean,
    onAddTask: (Date) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onAddTask(date) }
    ) {
        Column(
            modifier = Modifier.width(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Line from above
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(8.dp)
                    .background(Color(0xFF0056B3).copy(alpha = 0.5f))
            )
            
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Text(
            text = if (isToday) "Add task for today" else "Add task",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )
    }
}
