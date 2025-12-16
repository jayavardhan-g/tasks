package com.example.tasks.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
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
    onSave: (String, String, Long, Int?, Int, String) -> Unit, // title, desc, deadline, workspaceId, priority, tags
    onAddWorkspace: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedWorkspace by remember { mutableStateOf<Workspace?>(null) }
    var priority by remember { mutableStateOf(0) } // 0: None, 1: Low, 2: Medium, 3: High
    var tags by remember { mutableStateOf("") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets.ime
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 16.dp) // Extra padding for navigation bar usually handled by scaffold but good here
        ) {
            // Header / Quick Entry
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("New Task") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { 
                        if (title.isNotBlank()) {
                            onSave(title, description, selectedDate, selectedWorkspace?.id, priority, tags)
                        }
                    },
                    enabled = title.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Options Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                
                AssistChip(
                    onClick = { showDatePicker = true },
                    label = { Text(dateFormat.format(Date(selectedDate))) },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                )
                
                AssistChip(
                    onClick = { showTimePicker = true },
                    label = { Text(timeFormat.format(Date(selectedDate))) },
                    leadingIcon = { Icon(Icons.Default.Notifications, contentDescription = null) }
                )
                
                AssistChip(
                    onClick = { /* Toggle Workspace logic, simpler for MVP */ 
                        // For now, just a simplified toggle for the first workspace or None
                         if (workspaces.isNotEmpty()) {
                             val currentIndex = workspaces.indexOf(selectedWorkspace)
                             val nextIndex = currentIndex + 1
                             selectedWorkspace = if (nextIndex < workspaces.size) workspaces[nextIndex] else null
                         }
                    },
                    label = { Text(selectedWorkspace?.name ?: "No Project") },
                    leadingIcon = { 
                        Icon(
                            Icons.Default.List, 
                            contentDescription = null,
                            tint = if (selectedWorkspace != null) androidx.compose.ui.graphics.Color(selectedWorkspace!!.color) else MaterialTheme.colorScheme.onSurface
                        ) 
                    },
                     colors = if (selectedWorkspace != null) AssistChipDefaults.assistChipColors(
                        leadingIconContentColor = androidx.compose.ui.graphics.Color(selectedWorkspace!!.color)
                    ) else AssistChipDefaults.assistChipColors()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Expanded Options (Visible/Scrolled to when dragged up, or just always present in column but below fold)
            // In ModalBottomSheet, content is scrollable. 
            
            Text("Details", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Priority", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("None", "Low", "Medium", "High").forEachIndexed { index, label ->
                    FilterChip(
                        selected = priority == index,
                        onClick = { priority = index },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Tags (comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { date ->
                            val calendar = Calendar.getInstance()
                            val originalTime = Calendar.getInstance().apply { timeInMillis = selectedDate }
                            calendar.timeInMillis = date
                            calendar.set(Calendar.HOUR_OF_DAY, originalTime.get(Calendar.HOUR_OF_DAY))
                            calendar.set(Calendar.MINUTE, originalTime.get(Calendar.MINUTE))
                            selectedDate = calendar.timeInMillis
                        }
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

        if (showTimePicker) {
            val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
            val timePickerState = rememberTimePickerState(
                initialHour = calendar.get(Calendar.HOUR_OF_DAY),
                initialMinute = calendar.get(Calendar.MINUTE)
            )

            // TimePicker doesn't have a built-in dialog in M3, so wrapper might be needed
            // For brevity using a basic AlertDialog or similar, same as TaskDialog logic
             androidx.compose.material3.AlertDialog(
                 onDismissRequest = { showTimePicker = false },
                 confirmButton = {
                     TextButton(onClick = {
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
    }
}
