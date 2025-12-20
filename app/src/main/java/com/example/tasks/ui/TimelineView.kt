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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Surface
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import kotlinx.coroutines.launch
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
import androidx.compose.material.icons.filled.Flag
import com.example.tasks.data.Task
import com.example.tasks.ui.components.SummaryCards
import com.example.tasks.ui.components.CalendarStrip
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
    scrollToTaskId: Int? = null,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val (tasksWithDeadlines, unplannedTasks) = remember(tasksWithChecklists) {
        tasksWithChecklists.partition { it.task.deadline != 0L }
    }

    val groupedTasks = remember(tasksWithDeadlines) {
        tasksWithDeadlines.sortedBy { it.task.deadline }.groupBy {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.format(Date(it.task.deadline))
        }
    }

    val dateList = remember(tasksWithDeadlines) {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        
        // Find earliest and latest deadlines
        val earliest = tasksWithDeadlines.minOfOrNull { it.task.deadline } ?: now
        val latest = tasksWithDeadlines.maxOfOrNull { it.task.deadline } ?: now
        
        // Start Date: earlier of (Today - 1 month) or Earliest Deadline
        calendar.timeInMillis = now
        calendar.add(Calendar.MONTH, -1)
        val oneMonthAgo = calendar.timeInMillis
        val startTime = minOf(earliest, oneMonthAgo)
        
        // End Date: later of (Latest Deadline + 1 week) or (Today + 15 days)
        calendar.timeInMillis = latest
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val latestPlusWeek = calendar.timeInMillis
        
        calendar.timeInMillis = now
        calendar.add(Calendar.DAY_OF_YEAR, 15)
        val fifteenDaysFromNow = calendar.timeInMillis
        val endTime = maxOf(latestPlusWeek, fifteenDaysFromNow)
        
        // Generate list of dates
        val list = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        val current = Calendar.getInstance().apply { timeInMillis = startTime }
        val end = Calendar.getInstance().apply { timeInMillis = endTime }
        
        // Normalize to midnight for comparison
        current.set(Calendar.HOUR_OF_DAY, 0)
        current.set(Calendar.MINUTE, 0)
        current.set(Calendar.SECOND, 0)
        current.set(Calendar.MILLISECOND, 0)
        
        end.set(Calendar.HOUR_OF_DAY, 0)
        end.set(Calendar.MINUTE, 0)
        end.set(Calendar.SECOND, 0)
        end.set(Calendar.MILLISECOND, 0)
        
        while (!current.after(end)) {
            list.add(dateFormat.format(current.time))
            current.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    val listState = rememberLazyListState()
    
    val todayIndex = remember(dateList, groupedTasks) {
        calculateIndexForDate(
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            dateList,
            groupedTasks
        )
    }

    LaunchedEffect(todayIndex) {
        if (todayIndex > 0) {
            listState.scrollToItem(todayIndex)
        }
    }

    LaunchedEffect(scrollToTaskId) {
        if (scrollToTaskId != null) {
            val index = calculateIndexForTaskId(scrollToTaskId, dateList, groupedTasks)
            if (index != -1) {
                listState.animateScrollToItem(index)
            }
        }
    }

    val isTodayVisible by remember {
        derivedStateOf {
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) true
            else {
                val todayKey = "header_${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}"
                visibleItems.any { it.key == todayKey }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp)
        ) {
        // 1. Timeline for Dates
        dateList.forEach { dateString ->
            val parseFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parseFormat.parse(dateString) ?: Date()
            val today = Calendar.getInstance()
            val headerDate = Calendar.getInstance().apply { time = date }
            val isToday = today.get(Calendar.YEAR) == headerDate.get(Calendar.YEAR) &&
                         today.get(Calendar.DAY_OF_YEAR) == headerDate.get(Calendar.DAY_OF_YEAR)
            
            val tasksForDate = groupedTasks[dateString] ?: emptyList()

            item(key = "header_$dateString") {
                TimelineHeader(dateString = dateString, isToday = isToday)
            }
            
            if (isToday) {
                val now = System.currentTimeMillis()
                val overdueCount = tasksWithChecklists.count { !it.task.isCompleted && it.task.deadline != 0L && it.task.deadline < now }
                val unplannedCount = unplannedTasks.count { !it.task.isCompleted }
                
                item(key = "summary_$dateString") {
                    SummaryCards(
                        todoCount = tasksForDate.count { !it.task.isCompleted },
                        overdueCount = overdueCount,
                        unplannedCount = unplannedCount
                    )
                }
            }

            itemsIndexed(tasksForDate, key = { _, taskWithChecklist -> taskWithChecklist.task.id!! }) { index, taskWithChecklist ->
                val task = taskWithChecklist.task
                val workspace = if (task.workspaceId != null) workspaces[task.workspaceId] else null
                TimelineTaskItem(
                    task = task,
                    checklist = taskWithChecklist.checklist,
                    isLast = false,
                    workspaceName = workspace?.name,
                    workspaceColor = workspace?.color,
                    onCheckedChange = onCheckedChange,
                    onChecklistItemChange = onChecklistItemChange,
                    onDelete = onDelete,
                    onEdit = onEdit
                )
            }
            
            item(key = "add_$dateString") {
                TimelineAddTaskRow(
                    date = date,
                    isToday = isToday,
                    onAddTask = onAddTask
                )
            }
        }
        
        // 2. Unplanned Section
        if (unplannedTasks.isNotEmpty()) {
            item(key = "unplanned_header") {
                Text(
                    text = "Unplanned",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            
            itemsIndexed(unplannedTasks, key = { _, taskWithChecklist -> taskWithChecklist.task.id!! }) { index, taskWithChecklist ->
                val task = taskWithChecklist.task
                val workspace = if (task.workspaceId != null) workspaces[task.workspaceId] else null
                TimelineTaskItem(
                    task = task,
                    checklist = taskWithChecklist.checklist,
                    isLast = index == unplannedTasks.size - 1,
                    workspaceName = workspace?.name,
                    workspaceColor = workspace?.color,
                    onCheckedChange = onCheckedChange,
                    onChecklistItemChange = onChecklistItemChange,
                    onDelete = onDelete,
                    onEdit = onEdit
                )
            }
            
            item(key = "add_unplanned") {
                TimelineAddTaskRow(
                    date = Date(0), // Using 0 to indicate no deadline
                    isToday = false,
                    onAddTask = onAddTask
                )
            }
        }
    }
        CalendarStrip(
            onDateSelected = { dateStr ->
                val index = calculateIndexForDate(dateStr, dateList, groupedTasks)
                scope.launch {
                    listState.animateScrollToItem(index)
                }
            }
        )
    }
    
    // "Go to Today" Floating Button
    AnimatedVisibility(
        visible = !isTodayVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 120.dp) // Anchored above CalendarStrip
    ) {
        Surface(
            onClick = {
                scope.launch {
                    if (todayIndex >= 0) {
                        listState.animateScrollToItem(todayIndex)
                    }
                }
            },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
}

private fun calculateIndexForDate(
    targetDate: String,
    dateList: List<String>,
    groupedTasks: Map<String, List<com.example.tasks.data.TaskWithChecklist>>
): Int {
    var index = 0
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    for (dateStr in dateList) {
        if (dateStr == targetDate) {
            break
        }
        val tasksForDate = groupedTasks[dateStr] ?: emptyList()
        index += 1 // Header
        
        if (dateStr == todayStr) {
            index += 1 // SummaryCards
        }
        
        index += tasksForDate.size // Tasks
        index += 1 // Add Task Row
    }
    return index
}

private fun calculateIndexForTaskId(
    targetId: Int,
    dateList: List<String>,
    groupedTasks: Map<String, List<com.example.tasks.data.TaskWithChecklist>>
): Int {
    var index = 0
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    for (dateStr in dateList) {
        val tasksForDate = groupedTasks[dateStr] ?: emptyList()
        val taskIndex = tasksForDate.indexOfFirst { it.task.id == targetId }
        
        if (taskIndex != -1) {
            return index + 1 + (if (dateStr == todayStr) 1 else 0) + taskIndex
        }
        
        index += 1 // Header
        if (dateStr == todayStr) {
            index += 1 // SummaryCards
        }
        index += tasksForDate.size // Tasks
        index += 1 // Add Task Row
    }
    return -1
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
    val indicatorColor = if (workspaceColor != null) Color(workspaceColor) else Color(0xFF0056B3)
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        // Timeline Line
        Column(
            modifier = Modifier.width(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(2.dp, indicatorColor, CircleShape)
                    .clickable { onCheckedChange(task, !task.isCompleted) },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(indicatorColor, CircleShape)
                    )
                }
            }
            
            // Vertical Line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(indicatorColor.copy(alpha = 0.5f))
            )
        }
        
        // Task Item Content
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onEdit(task) }
                .padding(bottom = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (task.priority > 0) {
                    val priorityColor = when (task.priority) {
                        3 -> Color(0xFFF44336) // Red
                        2 -> Color(0xFFFF9800) // Orange
                        1 -> Color(0xFF4CAF50) // Green
                        else -> Color.Transparent
                    }
                    if (priorityColor != Color.Transparent) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Priority",
                            modifier = Modifier.size(16.dp).padding(end = 4.dp),
                            tint = priorityColor
                        )
                    }
                }
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )
            }
            
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
                    .background(Color.Gray.copy(alpha = 0.3f))
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
