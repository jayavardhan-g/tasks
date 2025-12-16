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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerBottomSheet(
    onDismiss: () -> Unit,
    initialDate: Long?,
    onDateSelected: (Long?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var selectedDate by remember { mutableStateOf(initialDate) }
    
    // Internal states for custom pickers
    var showCustomDatePicker by remember { mutableStateOf(false) }
    var showCustomTimePicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets.ime
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header showing current selection
            val headerText = if (selectedDate == null) "No date" else {
                SimpleDateFormat("EEE, MMM d, HH:mm", Locale.getDefault()).format(Date(selectedDate!!))
            }
            Text(
                text = headerText,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
            )
            
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // Shortcuts
            val calendar = Calendar.getInstance()
            
            // Today
            val today = calendar.timeInMillis
            DateTimePickerRow(
                icon = Icons.Outlined.DateRange,
                text = "Today",
                subtext = SimpleDateFormat("EEE", Locale.getDefault()).format(Date(today)),
                onClick = { selectedDate = today }
            )

            // Tomorrow
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrow = calendar.timeInMillis
             DateTimePickerRow(
                icon = Icons.Outlined.DateRange,
                text = "Tomorrow",
                subtext = SimpleDateFormat("EEE", Locale.getDefault()).format(Date(tomorrow)),
                onClick = { selectedDate = tomorrow }
            )

            // Next Week (Next Monday)
            calendar.timeInMillis = System.currentTimeMillis()
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
             if (calendar.timeInMillis <= System.currentTimeMillis()) {
                 calendar.add(Calendar.WEEK_OF_YEAR, 1)
             }
            val nextWeek = calendar.timeInMillis
             DateTimePickerRow(
                icon = Icons.Outlined.DateRange,
                text = "Next week",
                subtext = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(nextWeek)),
                onClick = { selectedDate = nextWeek }
            )

            // Custom
             DateTimePickerRow(
                icon = Icons.Default.Settings, // Placeholder for "Custom" icon
                text = "Custom",
                onClick = { showCustomDatePicker = true }
            )
            
            // No Date
             DateTimePickerRow(
                icon = Icons.Default.Close, 
                text = "No date",
                onClick = { selectedDate = null }
            )
            
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            
            // Add Time
             DateTimePickerRow(
                icon = Icons.Outlined.Notifications, // Placeholder for "Time" icon
                text = "Add time",
                onClick = { showCustomTimePicker = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { 
                        onDateSelected(selectedDate)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Select")
                }
            }
        }
    }

    // Custom Pickers
    if (showCustomDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate ?: System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showCustomDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { date ->
                        val currentCal = Calendar.getInstance().apply { timeInMillis = selectedDate ?: System.currentTimeMillis() }
                        val newCal = Calendar.getInstance().apply { timeInMillis = date }
                        newCal.set(Calendar.HOUR_OF_DAY, currentCal.get(Calendar.HOUR_OF_DAY))
                        newCal.set(Calendar.MINUTE, currentCal.get(Calendar.MINUTE))
                        selectedDate = newCal.timeInMillis
                    }
                    showCustomDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    if (showCustomTimePicker) {
        val cal = Calendar.getInstance().apply { timeInMillis = selectedDate ?: System.currentTimeMillis() }
        val timePickerState = rememberTimePickerState(
            initialHour = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE)
        )
        
        // Custom Dialog for time picker since there isn't a direct TimePickerDialog composable in M3 like DatePicker
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showCustomTimePicker = false },
            confirmButton = {
                 TextButton(onClick = {
                    cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    cal.set(Calendar.MINUTE, timePickerState.minute)
                    selectedDate = cal.timeInMillis
                    showCustomTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showCustomTimePicker = false }) { Text("Cancel") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }
}

@Composable
fun DateTimePickerRow(
    icon: ImageVector,
    text: String,
    subtext: String? = null,
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
        Text(text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        if (subtext != null) {
            Spacer(modifier = Modifier.weight(1f))
            Text(subtext, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
