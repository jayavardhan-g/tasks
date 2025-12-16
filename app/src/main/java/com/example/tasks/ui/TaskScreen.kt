package com.example.tasks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.CheckCircle

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.tasks.data.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TasksViewModel
) {
    val globalTasks by viewModel.globalTasks.observeAsState(initial = emptyList())
    val filteredTasks by viewModel.filteredTasks.observeAsState(initial = emptyList())
    val workspaces by viewModel.workspaces.observeAsState(initial = emptyList())
    val currentWorkspaceId by viewModel.currentWorkspaceId.observeAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showNewTaskSheet by remember { mutableStateOf(false) }
    var showAddWorkspaceDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Timeline, 1 = Workspace

    val workspacesMap = remember(workspaces) {
        workspaces.associateBy { it.id }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedTab == 0) "Timeline" else "Workspaces") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    if (selectedTab == 1) {
                        IconButton(onClick = { showAddWorkspaceDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Workspace")
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Timeline") },
                    label = { Text("Timeline") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Workspaces") },
                    label = { Text("Workspaces") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewTaskSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (selectedTab == 0) {
                // Timeline Tab (All Tasks)
                TimelineView(
                    tasksWithChecklists = globalTasks,
                    workspaces = workspacesMap,
                    onCheckedChange = { task, checked ->
                        viewModel.update(task.copy(isCompleted = checked))
                    },
                    onChecklistItemChange = { item ->
                        viewModel.toggleChecklistItem(item)
                    },
                    onDelete = { task ->
                        viewModel.delete(task)
                    },
                    onEdit = { task ->
                        editingTask = task
                    }
                )
            } else {
                // ... (Workspace Tab logic to be updated similarly)
                Column {
                    // Workspace Filter Chips
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = currentWorkspaceId == -1 || currentWorkspaceId == null,
                                onClick = { viewModel.setWorkspace(-1) },
                                label = { Text("All") },
                                leadingIcon = { 
                                    Icon(
                                        Icons.Default.Home, 
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    ) 
                                }
                            )
                        }
                        items(workspaces) { workspace ->
                            val isSelected = workspace.id == currentWorkspaceId
                            val workspaceColor = androidx.compose.ui.graphics.Color(workspace.color)
                            
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setWorkspace(workspace.id) },
                                label = { Text(workspace.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = workspaceColor,
                                    selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    labelColor = workspaceColor
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = workspaceColor,
                                    selectedBorderColor = workspaceColor,
                                    borderWidth = 1.dp
                                )
                            )
                        }
                    }
                    
                    if (workspaces.isEmpty()) {
                         Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                             Text("No workspaces yet. Add one!", style = MaterialTheme.typography.bodyLarge)
                         }
                    } else {
                        TimelineView(
                            tasksWithChecklists = filteredTasks,
                            workspaces = workspacesMap,
                            onCheckedChange = { task, checked ->
                                viewModel.update(task.copy(isCompleted = checked))
                            },
                            onChecklistItemChange = { item ->
                                viewModel.toggleChecklistItem(item)
                            },
                            onDelete = { task ->
                                viewModel.delete(task)
                            },
                            onEdit = { task ->
                                editingTask = task
                            }
                        )
                    }
                }
            }
        }

        if (showAddTaskDialog || editingTask != null) {
            val checklist by if (editingTask != null) {
                viewModel.getChecklist(editingTask!!.id).observeAsState(emptyList())
            } else {
                remember { mutableStateOf(emptyList()) }
            }

            TaskDialog(
                task = editingTask,
                checklist = checklist,
                workspaces = workspaces,
                onDismiss = { 
                    showAddTaskDialog = false 
                    editingTask = null
                },
                onSave = { title, desc, deadline, workspaceId, checklistItems ->
                    if (editingTask != null) {
                        val updatedTask = editingTask!!.copy(
                            title = title,
                            description = desc,
                            deadline = deadline,
                            workspaceId = workspaceId
                        )
                        val mappedItems = checklistItems.map { 
                            com.example.tasks.data.ChecklistItem(it.id, updatedTask.id, it.text, it.isCompleted) 
                        }
                        viewModel.updateTaskWithChecklist(updatedTask, mappedItems)
                    } else {
                        val task = Task(
                            title = title,
                            description = desc,
                            deadline = deadline,
                            workspaceId = workspaceId
                        )
                        val items = checklistItems.map { 
                            com.example.tasks.data.ChecklistItem(0, 0, it.text, it.isCompleted) 
                        }
                        viewModel.insertTaskWithChecklist(task, items)
                    }
                    showAddTaskDialog = false
                    editingTask = null
                },
                onAddWorkspace = { name ->
                    val randomColor = (0xFF000000..0xFFFFFFFF).random() or 0xFF000000 // Ensure opaque
                    viewModel.insertWorkspace(com.example.tasks.data.Workspace(name = name, color = randomColor))
                }
            )
        }
        
        if (showAddWorkspaceDialog) {
            AddWorkspaceDialog(
                onDismiss = { showAddWorkspaceDialog = false },
                onAdd = { name ->
                    val randomColor = (0xFF000000..0xFFFFFFFF).random() or 0xFF000000
                    viewModel.insertWorkspace(com.example.tasks.data.Workspace(name = name, color = randomColor))
                    showAddWorkspaceDialog = false
                }
            )
        }
        
        if (showNewTaskSheet) {
            NewTaskBottomSheet(
                workspaces = workspaces,
                onDismiss = { showNewTaskSheet = false },
                onSave = { title, desc, deadline, workspaceId, priority, tags ->
                    val task = Task(
                        title = title,
                        description = desc,
                        deadline = deadline,
                        workspaceId = workspaceId,
                        priority = priority,
                        tags = tags
                    )
                    // For now empty checklist for new simple tasks
                    viewModel.insertTaskWithChecklist(task, emptyList())
                    showNewTaskSheet = false
                },
                onAddWorkspace = { name ->
                    val randomColor = (0xFF000000..0xFFFFFFFF).random() or 0xFF000000
                    viewModel.insertWorkspace(com.example.tasks.data.Workspace(name = name, color = randomColor))
                }
            )
        }
    }
}

@Composable
fun EmptyWorkspaceScreen(onAddWorkspace: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Tasks!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Create your first workspace to get started.")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Workspace Name") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { if (name.isNotBlank()) onAddWorkspace(name) },
            enabled = name.isNotBlank()
        ) {
            Text("Create Workspace")
        }
    }
}

@Composable
fun AddWorkspaceDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Workspace") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onAdd(name) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDialog(
    task: Task? = null,
    checklist: List<com.example.tasks.data.ChecklistItem> = emptyList(),
    workspaces: List<com.example.tasks.data.Workspace>,
    onDismiss: () -> Unit,
    onSave: (String, String, Long, Int?, List<ChecklistItemEntry>) -> Unit,
    onAddWorkspace: (String) -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(task?.deadline ?: System.currentTimeMillis()) }
    
    var expanded by remember { mutableStateOf(false) }
    var selectedWorkspace by remember { 
        mutableStateOf(workspaces.find { it.id == task?.workspaceId }) 
    }
    var showNewWorkspaceInput by remember { mutableStateOf(false) }
    var newWorkspaceName by remember { mutableStateOf("") }

    // Checklist State
    // Checklist State
    val checklistItems = remember(checklist) { 
        (if (task != null) {
            checklist.map { ChecklistItemEntry(it.id, it.text, it.isCompleted) }
        } else {
            emptyList()
        }).toMutableStateList()
    }
    // Update checklist items if checklist prop changes (e.g. initial load for edit)
    // Actually remember(checklist) might be safer but local mutations would be lost if checklist updates from VM
    // For now, assume initial load is enough.
    
    var newChecklistItemText by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
    val timePickerState = rememberTimePickerState(
        initialHour = Calendar.getInstance().apply { timeInMillis = selectedDate }.get(Calendar.HOUR_OF_DAY),
        initialMinute = Calendar.getInstance().apply { timeInMillis = selectedDate }.get(Calendar.MINUTE)
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { date ->
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = date
                        // Preserve time if only changing date, or reset? Let's just set date.
                        // Ideally we flow to TimePicker next
                        selectedDate = calendar.timeInMillis
                    }
                    showDatePicker = false
                    showTimePicker = true // Open time picker after date
                }) {
                    Text("Next")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    if (showTimePicker) {
         AlertDialog(
             onDismissRequest = { showTimePicker = false },
             confirmButton = {
                 TextButton(onClick = {
                     val calendar = Calendar.getInstance()
                     calendar.timeInMillis = selectedDate
                     calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                     calendar.set(Calendar.MINUTE, timePickerState.minute)
                     selectedDate = calendar.timeInMillis
                     showTimePicker = false
                 }) {
                     Text("OK")
                 }
             },
             dismissButton = {
                 TextButton(onClick = { showTimePicker = false }) {
                     Text("Cancel")
                 }
             },
             text = {
                 TimePicker(state = timePickerState)
             }
         )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Add New Task" else "Edit Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
                
                val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Due: ${dateFormat.format(Date(selectedDate))}")
                }
                
                // Workspace Selector
                Box {
                    OutlinedTextField(
                        value = selectedWorkspace?.name ?: "No Workspace",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Workspace") },
                        trailingIcon = {
                            Icon(Icons.Default.List, contentDescription = "Select Workspace")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { expanded = !expanded }
                    )
                    androidx.compose.material3.DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("No Workspace") },
                            onClick = {
                                selectedWorkspace = null
                                expanded = false
                            }
                        )
                        workspaces.forEach { workspace ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(workspace.name) },
                                onClick = {
                                    selectedWorkspace = workspace
                                    expanded = false
                                }
                            )
                        }
                        Divider()
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("Create New Workspace...") },
                            onClick = {
                                showNewWorkspaceInput = true
                                expanded = false
                            }
                        )
                    }
                }

                if (showNewWorkspaceInput) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newWorkspaceName,
                            onValueChange = { newWorkspaceName = it },
                            label = { Text("New Workspace Name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Button(onClick = {
                            if (newWorkspaceName.isNotBlank()) {
                                onAddWorkspace(newWorkspaceName)
                                showNewWorkspaceInput = false
                                newWorkspaceName = ""
                            }
                        }) {
                            Text("Add")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("Checklist", style = MaterialTheme.typography.titleSmall)
                
                // Add Item Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = newChecklistItemText,
                        onValueChange = { newChecklistItemText = it },
                        placeholder = { Text("Add item") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(onClick = { 
                        if (newChecklistItemText.isNotBlank()) {
                            checklistItems.add(ChecklistItemEntry(0, newChecklistItemText, false))
                            newChecklistItemText = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
                
                // Checklist Items List
                LazyColumn(modifier = Modifier.height(150.dp)) {
                    itemsIndexed(checklistItems) { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            CircularCheckbox(
                                checked = item.isCompleted,
                                onCheckedChange = { checked ->
                                    checklistItems[index] = item.copy(isCompleted = checked)
                                }
                            )
                            Text(
                                text = item.text,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium,
                                textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null
                            )
                            IconButton(onClick = { checklistItems.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (title.isNotBlank()) {
                        onSave(title, description, selectedDate, selectedWorkspace?.id, checklistItems)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TaskItem(
    task: Task,
    workspaceName: String? = null,
    workspaceColor: Long? = null,
    onCheckedChange: (Task, Boolean) -> Unit,
    onDelete: (Task) -> Unit,
    onEdit: (Task) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(task) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularCheckbox(
            checked = task.isCompleted,
            onCheckedChange = { onCheckedChange(task, it) }
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
            )
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = dateFormat.format(Date(task.deadline)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (workspaceName != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = workspaceName,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (workspaceColor != null) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                color = if (workspaceColor != null) androidx.compose.ui.graphics.Color(workspaceColor) else MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
        IconButton(onClick = { onDelete(task) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Task")
        }
    }
}

data class ChecklistItemEntry(
    val id: Int, 
    val text: String,
    val isCompleted: Boolean
)

@Composable
fun CircularCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (checked) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Uncheck",
            tint = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .clickable { onCheckedChange(!checked) }
        )
    } else {
        Box(
            modifier = modifier
                .size(24.dp)
                .padding(2.dp) // Adjust padding to match icon visual size if needed
                .border(2.dp, MaterialTheme.colorScheme.onSurfaceVariant, androidx.compose.foundation.shape.CircleShape)
                .clickable { onCheckedChange(!checked) }
        )
    }
}
