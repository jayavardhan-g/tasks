package com.example.tasks.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Notifications

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.ui.text.font.FontWeight
import com.example.tasks.data.Workspace
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskBottomSheet(
    workspaces: List<Workspace>,
    onDismiss: () -> Unit,
    onSave: (String, String, Long, Int?, Int, String, Boolean) -> Unit, // title, desc, deadline, workspaceId, priority, tags, pinAsNotification
    onAddWorkspace: (String) -> Unit,
    onExpandToFull: (String, Long?, Int?, Int, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var selectedWorkspace by remember { mutableStateOf<Workspace?>(null) }
    var priority by remember { mutableStateOf(0) }
    
    var pinAsNotification by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) } 
    var showDateTimePickerSheet by remember { mutableStateOf(false) } 
    var showWorkspaceSheet by remember { mutableStateOf(false) }
    var showPrioritySheet by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { newState ->
            if (newState == androidx.compose.material3.SheetValue.Expanded) {
                onExpandToFull(title, selectedDate, selectedWorkspace?.id, priority, pinAsNotification)
                false // Don't snap to expanded visually, just navigate
            } else {
                true
            }
        }
    )

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
        ) {
            // Header: Title + Save
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 24.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    decorationBox = { innerTextField ->
                        androidx.compose.foundation.layout.Box {
                            if (title.isEmpty()) {
                                Text("New Task", style = androidx.compose.ui.text.TextStyle(fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
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
                            onSave(title, "", selectedDate ?: System.currentTimeMillis(), selectedWorkspace?.id, priority, "", pinAsNotification)
                        }
                    },
                    enabled = title.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                }
            }

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
                Icon(androidx.compose.material.icons.Icons.Outlined.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
            }
            
            // Add reminder (Placeholder)
            OptionRow(
                icon = Icons.Default.Notifications,
                text = "Add reminder",
                onClick = { /* TODO */ }
            )

            // Priority
            OptionRow(
                icon = androidx.compose.material.icons.Icons.Default.Star, // Or outlined star
                text = if (priority == 0) "Set priority" else "Priority: ${arrayOf("None", "Low", "Medium", "High")[priority]}",
                onClick = { showPrioritySheet = true }
            )

            // Pin as notification (Placeholder)
             Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                 // Checkbox needs import or full path
                androidx.compose.material3.Checkbox(
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
    }
}
