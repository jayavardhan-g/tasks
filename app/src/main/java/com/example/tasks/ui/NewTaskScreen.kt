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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasks.data.Task
import com.example.tasks.data.ChecklistItem
import com.example.tasks.data.Workspace
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import com.example.tasks.data.TaskDraft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreen(
    workspaces: List<Workspace>,
    taskToEdit: Task? = null,
    draftTask: TaskDraft? = null,
    initialChecklist: List<ChecklistItem> = emptyList(),
    onNavigateBack: () -> Unit,
    onSave: (String, String, Long, Int?, Int, String, List<ChecklistItem>, Boolean) -> Unit,
    onAddWorkspace: (String, Int) -> Unit
) {
    var title by remember { mutableStateOf(taskToEdit?.title ?: draftTask?.title ?: "") }
    var description by remember { mutableStateOf(taskToEdit?.description ?: draftTask?.description ?: "") }
    var selectedDate by remember { mutableStateOf(taskToEdit?.deadline ?: draftTask?.deadline) } // Nullable to show "Today" option
    var selectedWorkspace by remember { mutableStateOf(workspaces.find { it.id == (taskToEdit?.workspaceId ?: draftTask?.workspaceId) }) }
    var priority by remember { mutableStateOf(taskToEdit?.priority ?: draftTask?.priority ?: 0) }
    var tags by remember { mutableStateOf(taskToEdit?.tags ?: draftTask?.tags ?: "") }
    var pinAsNotification by remember { mutableStateOf(taskToEdit?.pinAsNotification ?: draftTask?.pinAsNotification ?: false) }
    
    var showDateTimePickerSheet by remember { mutableStateOf(false) } // Unified sheet
    
    var showPrioritySheet by remember { mutableStateOf(false) }
    var showTagInput by remember { mutableStateOf(false) }
    var showWorkspaceSheet by remember { mutableStateOf(false) }
    var showChecklistSheet by remember { mutableStateOf(false) }
    
    val checklistItems = remember { 
        androidx.compose.runtime.mutableStateListOf<ChecklistItem>().apply {
            addAll(initialChecklist)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskToEdit == null) "Create task" else "Edit task", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    // Date Icon Button as per layout? Or just part of rows.
                    // Image shows "Create task" + Back button.
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FloatingActionButton(
                    onClick = {
                        val finalDate = selectedDate ?: System.currentTimeMillis()
                        // Ensure basic validation
                        if (title.isNotBlank()) {
                            onSave(title, description, finalDate, selectedWorkspace?.id, priority, tags, checklistItems.toList(), pinAsNotification)
                        } else {
                            // Maybe show toast? For now just don't save.
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Save")
                }

                 // Another button for "Download/Save"? The image has "+" and a "Download/Tray" icon.
                 // Assuming "+" is Save/Add. The bottom one looks like "Import/Tray".
                 // Let's implement the standard Save as defined in previous flow.
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title Input
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                decorationBox = { innerTextField ->
                    Box {
                        if (title.isEmpty()) {
                            Text("Task title", style = TextStyle(fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description Input
            BasicTextField(
                value = description,
                onValueChange = { description = it },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                decorationBox = { innerTextField ->
                    Box {
                        if (description.isEmpty()) {
                            Text("Would you like to add more details?", style = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Options List
            
            // Project
            OptionRow(
                icon = Icons.Filled.List, // Close approximation to "Grid/Layout"
                text = selectedWorkspace?.name ?: "Select workspace",
                onClick = { showWorkspaceSheet = true }
            )

            // Date / Time
            Row(
                 modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                 verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(16.dp))
                
                // Chip for Today/Date
                val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                val dateLabel = if (selectedDate == null) "Today" else dateFormat.format(Date(selectedDate!!))
                
                AssistChip(
                    onClick = { showDateTimePickerSheet = true },
                    label = { Text(dateLabel) },
                    modifier = Modifier.padding(end = 8.dp)
                )

                // Chip for Time
                val timeLabel = if (selectedDate == null) "Add time" else SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(selectedDate!!))
                AssistChip(
                    onClick = { showDateTimePickerSheet = true },
                    label = { Text(timeLabel) }
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                if (selectedDate != null) {
                    IconButton(onClick = { selectedDate = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear Date")
                    }
                }
            }

            // Reminder
            OptionRow(
                icon = Icons.Outlined.Notifications,
                text = "Add reminder", // Placeholder logic
                onClick = { /* TODO */ }
            )

            // Priority
            OptionRow(
                icon = Icons.Outlined.Flag,
                text = if (priority == 0) "Set priority" else "Priority: ${arrayOf("None", "Low", "Medium", "High")[priority]}",
                onClick = { showPrioritySheet = true }
            )

            // Checklist
            OptionRow(
                icon = Icons.Outlined.CheckCircle,
                text = "Add checklist",
                onClick = { showChecklistSheet = true }
            )
            
            // Display Checklist Items
            if (checklistItems.isNotEmpty()) {
                Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp)) {
                    checklistItems.forEachIndexed { index, item ->
                         Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (item.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.DateRange, // Placeholder for unchecked
                                contentDescription = null, 
                                modifier = Modifier.size(16.dp).clickable { 
                                    checklistItems[index] = item.copy(isCompleted = !item.isCompleted)
                                }, 
                                tint = if (item.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(item.text, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { checklistItems.removeAt(index) }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Clear, contentDescription = "Remove", modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
            
            // Tags
            OptionRow(
                icon = Icons.Default.Flag, // Tag icon usually outlined label
                text = if (tags.isEmpty()) "Assign tags" else tags,
                onClick = { showTagInput = true }
            )
            
             // Templates (Placeholder)
            OptionRow(
                icon = Icons.Default.Add, // Bookmark placeholder
                text = "Import from templates",
                onClick = { /* TODO */ }
            )
            
            // Discard
             OptionRow(
                icon = Icons.Default.Delete,
                text = "Discard",
                onClick = onNavigateBack // Or confirm/clear
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Pin Checkbox
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Checkbox(
                    checked = pinAsNotification, 
                    onCheckedChange = { pinAsNotification = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pin as notification")
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // Clearance for FAB
        }
    }
    
    // Dialogs
    
    if (showDateTimePickerSheet) {
        DateTimePickerBottomSheet(
            onDismiss = { showDateTimePickerSheet = false },
            initialDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
            }
        )
    }
    
    if (showPrioritySheet) {
        PrioritySelectionBottomSheet(
            onDismiss = { showPrioritySheet = false },
            currentPriority = priority,
            onPrioritySelected = { newPriority ->
                priority = newPriority
                showPrioritySheet = false
            }
        )
    }
    
    if (showTagInput) {
        AlertDialog(
            onDismissRequest = { showTagInput = false },
            title = { Text("Enter Tags") },
            text = {
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Comma separated tags") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = { showTagInput = false }) { Text("Done") }
            }
        )
    }
    
    if (showWorkspaceSheet) {
        WorkspaceSelectionBottomSheet(
            workspaces = workspaces,
            onDismiss = { showWorkspaceSheet = false },
            onWorkspaceSelected = { workspace ->
                selectedWorkspace = workspace
                showWorkspaceSheet = false
            },
            onCreateWorkspace = { name, color ->
                onAddWorkspace(name, color)
                // We might want to auto-select the new workspace.
                // Since this callback is void, handling that requires observing changes or returning ID.
                // For MVP, just closing sheet or keeping it open.
                // Let's assume ViewModel updates list and we can manually select if we knew ID or Name.
                // For now, simple add.
            }
        )
    }


    if (showChecklistSheet) {
        ChecklistBottomSheet(
            onDismiss = { showChecklistSheet = false },
            onAddChecklistItem = { text ->
                checklistItems.add(ChecklistItem(taskId = taskToEdit?.id ?: 0, text = text, isCompleted = false))
            }
        )
    }
}

@Composable
fun OptionRow(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
