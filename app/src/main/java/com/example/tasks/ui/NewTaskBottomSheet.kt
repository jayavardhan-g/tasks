package com.example.tasks.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Notifications

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasks.data.ChecklistItem
import com.example.tasks.data.Workspace
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskBottomSheet(
    workspaces: List<Workspace>,
    taskToEdit: com.example.tasks.data.Task? = null,
    initialChecklist: List<com.example.tasks.data.ChecklistItem> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (String, String, Long, Int?, Int, String, List<ChecklistItem>, Boolean) -> Unit, // title, desc, deadline, workspaceId, priority, tags, checklist, pinAsNotification
    onAddWorkspace: (String) -> Unit,
    initialDate: Long? = null,
    initialWorkspaceId: Int? = null
) {
    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var selectedDate by remember { 
        mutableStateOf<Long?>(
            if (taskToEdit != null) {
                if (taskToEdit.deadline == 0L) null else taskToEdit.deadline
            } else {
                initialDate
            }
        ) 
    }
    var selectedWorkspace by remember { mutableStateOf<Workspace?>(null) }
    
    LaunchedEffect(initialWorkspaceId, taskToEdit, workspaces) {
        val targetId = taskToEdit?.workspaceId ?: initialWorkspaceId
        if (targetId != null && targetId != -1) {
            selectedWorkspace = workspaces.find { it.id == targetId }
        }
    }

    var priority by remember { mutableStateOf(taskToEdit?.priority ?: 0) }
    var tags by remember { mutableStateOf(taskToEdit?.tags ?: "") }
    var pinAsNotification by remember { mutableStateOf(taskToEdit?.pinAsNotification ?: false) }

    var showDatePicker by remember { mutableStateOf(false) } 
    var showDateTimePickerSheet by remember { mutableStateOf(false) } 
    var showWorkspaceSheet by remember { mutableStateOf(false) }
    var showPrioritySheet by remember { mutableStateOf(false) }
    var showTagInput by remember { mutableStateOf(false) }
    var showChecklistSheet by remember { mutableStateOf(false) }

    val checklistItems = remember { 
        androidx.compose.runtime.mutableStateListOf<ChecklistItem>().apply {
            addAll(initialChecklist)
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets.ime
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 16.dp) 
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Title + Save
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.foundation.text.BasicTextField(
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
                                Text("New Task", style = TextStyle(fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = { 
                        if (title.isNotBlank()) {
                            onSave(title, description, selectedDate ?: 0L, selectedWorkspace?.id, priority, tags, checklistItems.toList(), pinAsNotification)
                        }
                    },
                    enabled = title.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description Input
            androidx.compose.foundation.text.BasicTextField(
                value = description,
                onValueChange = { description = it },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                decorationBox = { innerTextField ->
                    Box {
                        if (description.isEmpty()) {
                            Text("Add details...", style = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Workspace Option
            OptionRow(
                icon = Icons.Default.List, 
                text = selectedWorkspace?.name ?: "Select workspace",
                onClick = { showWorkspaceSheet = true }
            )

            // Date / Time Option
            Row(
                 modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                 verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(16.dp))
                
                val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                val dateLabel = if (selectedDate != null) dateFormat.format(Date(selectedDate!!)) else "No date"
                
                AssistChip(
                    onClick = { showDateTimePickerSheet = true },
                    label = { Text(dateLabel) },
                    modifier = Modifier.padding(end = 8.dp)
                )

                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val timeLabel = if (selectedDate != null) timeFormat.format(Date(selectedDate!!)) else "No time"
                AssistChip(
                    onClick = { showDateTimePickerSheet = true },
                    label = { Text(timeLabel) }
                )
                
                if (selectedDate != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { selectedDate = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Clear Date", modifier = Modifier.size(16.dp))
                    }
                }
            }
            
            // Checklist Option
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

            // Priority
            OptionRow(
                icon = Icons.Default.Flag, // Or outlined star
                text = if (priority == 0) "Set priority" else "Priority: ${arrayOf("None", "Low", "Medium", "High")[priority]}",
                onClick = { showPrioritySheet = true }
            )

            // Tags
            OptionRow(
                icon = Icons.Default.Flag, 
                text = if (tags.isEmpty()) "Assign tags" else tags,
                onClick = { showTagInput = true }
            )
            
            // Reminder (Placeholder)
            OptionRow(
                icon = Icons.Default.Notifications,
                text = "Add reminder",
                onClick = { /* TODO */ }
            )

            // Pin as notification
             Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = pinAsNotification, 
                    onCheckedChange = { pinAsNotification = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pin as notification")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
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
        
        if (showWorkspaceSheet) {
            WorkspaceSelectionBottomSheet(
                workspaces = workspaces,
                onDismiss = { showWorkspaceSheet = false },
                onWorkspaceSelected = { workspace ->
                    selectedWorkspace = workspace
                    showWorkspaceSheet = false
                },
                onCreateWorkspace = { name, color ->
                    onAddWorkspace(name)
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

        if (showChecklistSheet) {
            ChecklistBottomSheet(
                onDismiss = { showChecklistSheet = false },
                onAddChecklistItem = { text ->
                    checklistItems.add(ChecklistItem(taskId = 0, text = text, isCompleted = false))
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
    }
}
