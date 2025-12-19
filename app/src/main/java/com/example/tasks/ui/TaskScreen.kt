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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp

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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tasks.data.Task
import com.example.tasks.data.Workspace
import com.example.tasks.data.TaskWithChecklist
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.*
import com.example.tasks.data.TaskDraft
import com.example.tasks.ui.components.WorkspaceProgressCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TasksViewModel,
    onNavigateToNewTask: (TaskDraft?) -> Unit,
    onEditTask: (Task, List<com.example.tasks.data.ChecklistItem>) -> Unit
) {
    val globalTasks by viewModel.globalTasks.observeAsState(initial = emptyList())
    val filteredTasks by viewModel.filteredTasks.observeAsState(initial = emptyList())
    val workspaces by viewModel.workspaces.observeAsState(initial = emptyList())
    val currentWorkspaceId by viewModel.currentWorkspaceId.observeAsState()

    var showNewTaskSheet by remember { mutableStateOf(false) }
    var newTaskInitialDate by remember { mutableStateOf<Long?>(null) }
    var showAddWorkspaceDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Timeline, 1 = Workspace
    var isSearchActive by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val matches by viewModel.matches.observeAsState(initial = emptyList())
    val currentMatchIndex by viewModel.currentMatchIndex.collectAsState()

    val workspacesMap = remember(workspaces) {
        workspaces.associateBy { it.id }
    }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                TopAppBar(
                    title = {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Search tasks...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = Color.Black
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            isSearchActive = false 
                            viewModel.setSearchQuery("")
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (searchQuery.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (matches.isEmpty()) "0/0" else "${currentMatchIndex + 1}/${matches.size}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                IconButton(onClick = { viewModel.navigatePreviousMatch() }) {
                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Previous Match")
                                }
                                IconButton(onClick = { viewModel.navigateNextMatch() }) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Next Match")
                                }
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black
                    )
                )
            } else {
                TopAppBar(
                    title = { 
                        if (selectedTab == 0) {
                            Text(
                                "Tasks",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                "Workspace Progress",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.Black
                    ),
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        if (selectedTab == 0) {
                            IconButton(onClick = { /* More */ }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More")
                            }
                        } else if (selectedTab == 1) {
                            IconButton(onClick = { showAddWorkspaceDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Add Workspace")
                            }
                        }
                    }
                )
            }
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
                        // find checklist from globalTasks? globalTasks is TaskWithChecklist
                        val matchingItem = globalTasks.find { it.task.id == task.id }
                        onEditTask(task, matchingItem?.checklist ?: emptyList())
                    },
                    onAddTask = { date ->
                        newTaskInitialDate = if (date.time == 0L) null else date.time
                        showNewTaskSheet = true
                    },
                    scrollToTaskId = if (isSearchActive && matches.isNotEmpty()) matches[currentMatchIndex] else null
                )
            } else {
                // Workspaces Tab (Grid Summary)
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    if (workspaces.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No workspaces yet. Add one!", style = MaterialTheme.typography.bodyLarge)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(workspaces) { workspace ->
                                // Calculate metrics for this workspace
                                val workspaceTasks = globalTasks.filter { it.task.workspaceId == workspace.id }
                                val total = workspaceTasks.size
                                val completed = workspaceTasks.count { it.task.isCompleted }
                                
                                WorkspaceProgressCard(
                                    name = workspace.name,
                                    color = workspace.color,
                                    completedTasks = completed,
                                    totalTasks = total,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
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
                onDismiss = { 
                    showNewTaskSheet = false
                    newTaskInitialDate = null
                },
                onSave = { title, desc, deadline, workspaceId, priority, tags, pinAsNotification ->
                    val task = Task(
                        title = title,
                        description = desc,
                        deadline = deadline,
                        workspaceId = workspaceId,
                        priority = priority,
                        tags = tags,
                        pinAsNotification = pinAsNotification
                    )
                    // For now empty checklist for new simple tasks
                    viewModel.insertTaskWithChecklist(task, emptyList())
                    showNewTaskSheet = false
                    newTaskInitialDate = null
                },
                onAddWorkspace = { name ->
                    val randomColor = (0xFF000000..0xFFFFFFFF).random() or 0xFF000000
                    viewModel.insertWorkspace(com.example.tasks.data.Workspace(name = name, color = randomColor))
                },
                onExpandToFull = { title, date, workspaceId, priority, pinAsNotification ->
                    showNewTaskSheet = false
                    newTaskInitialDate = null
                    onNavigateToNewTask(TaskDraft(title = title, deadline = date, workspaceId = workspaceId, priority = priority, pinAsNotification = pinAsNotification))
                },
                initialDate = newTaskInitialDate
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
