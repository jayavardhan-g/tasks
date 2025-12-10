package com.example.tasks.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.tasks.data.Task
import java.text.SimpleDateFormat
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
    var showAddWorkspaceDialog by remember { mutableStateOf(false) }
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
            FloatingActionButton(onClick = { showAddTaskDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (selectedTab == 0) {
                // Timeline Tab (All Tasks)
                TimelineView(
                    tasks = globalTasks,
                    workspaces = workspacesMap,
                    onCheckedChange = { task, checked ->
                        viewModel.update(task.copy(isCompleted = checked))
                    },
                    onDelete = { task ->
                        viewModel.delete(task)
                    }
                )
            } else {
                // Workspace Tab
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
                                label = { Text("All") }
                            )
                        }
                        items(workspaces) { workspace ->
                            FilterChip(
                                selected = workspace.id == currentWorkspaceId,
                                onClick = { viewModel.setWorkspace(workspace.id) },
                                label = { Text(workspace.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = androidx.compose.ui.graphics.Color(workspace.color),
                                    selectedLabelColor = androidx.compose.ui.graphics.Color.White
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
                            tasks = filteredTasks,
                            workspaces = workspacesMap,
                            onCheckedChange = { task, checked ->
                                viewModel.update(task.copy(isCompleted = checked))
                            },
                            onDelete = { task ->
                                viewModel.delete(task)
                            }
                        )
                    }
                }
            }
        }

        if (showAddTaskDialog) {
            AddTaskDialog(
                workspaces = workspaces,
                onDismiss = { showAddTaskDialog = false },
                onAdd = { title, desc, deadline, workspaceId ->
                    val task = Task(
                        title = title,
                        description = desc,
                        deadline = deadline,
                        workspaceId = workspaceId
                    )
                    viewModel.insert(task)
                    showAddTaskDialog = false
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
fun AddTaskDialog(
    workspaces: List<com.example.tasks.data.Workspace>,
    onDismiss: () -> Unit,
    onAdd: (String, String, Long, Int?) -> Unit, // Int? is workspaceId
    onAddWorkspace: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    
    var expanded by remember { mutableStateOf(false) }
    var selectedWorkspace by remember { mutableStateOf<com.example.tasks.data.Workspace?>(null) }
    var showNewWorkspaceInput by remember { mutableStateOf(false) }
    var newWorkspaceName by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDate = it }
                    showDatePicker = false
                }) {
                    Text("OK")
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
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
                
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
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
                                // We can't automatically select it here easily without a callback return or observing logic,
                                // but the user can select it after it appears in the list.
                                // Improvement: ViewModel could return ID, but for now simplistic approach.
                                showNewWorkspaceInput = false
                                newWorkspaceName = ""
                            }
                        }) {
                            Text("Add")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (title.isNotBlank()) {
                        onAdd(title, description, selectedDate, selectedWorkspace?.id)
                    }
                }
            ) {
                Text("Save Task")
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
    onDelete: (Task) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(task, !task.isCompleted) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
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
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
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
