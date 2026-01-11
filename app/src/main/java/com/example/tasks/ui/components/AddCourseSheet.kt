package com.example.tasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.tasks.data.ClassSchedule
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseSheet(
    onDismiss: () -> Unit,
    onSave: (String, String, String, Long, List<ClassSchedule>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var professor by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color(0xFF2196F3)) }
    var schedules by remember { mutableStateOf(listOf<ClassSchedule>()) }
    
    var showScheduleDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        windowInsets = WindowInsets.ime
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("New Course", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Course Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = professor,
                onValueChange = { professor = it },
                label = { Text("Professor") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
             OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location (Room/Building)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Color", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val colors = listOf(Color(0xFF2196F3), Color(0xFFF44336), Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFF9C27B0))
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(if (selectedColor == color) 2.dp else 0.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                            .clickable { selectedColor = color }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Schedule", style = MaterialTheme.typography.labelLarge)
                TextButton(onClick = { showScheduleDialog = true }) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Class Time")
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                schedules.forEach { schedule ->
                    val dayName = when(schedule.dayOfWeek) {
                        Calendar.MONDAY -> "Mon"; Calendar.TUESDAY -> "Tue"; Calendar.WEDNESDAY -> "Wed"
                        Calendar.THURSDAY -> "Thu"; Calendar.FRIDAY -> "Fri"; Calendar.SATURDAY -> "Sat"; else -> "Sun"
                    }
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                         Row(
                             modifier = Modifier.fillMaxWidth().padding(8.dp),
                             horizontalArrangement = Arrangement.SpaceBetween,
                             verticalAlignment = Alignment.CenterVertically
                         ) {
                             Text("$dayName ${schedule.startTime} - ${schedule.endTime}", modifier = Modifier.padding(start = 8.dp))
                             IconButton(onClick = { schedules = schedules - schedule }) {
                                 Icon(Icons.Default.Clear, "Remove")
                             }
                         }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { 
                    if (name.isNotBlank()) {
                        onSave(name, professor, location, selectedColor.toArgb().toLong(), schedules)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text("Save Course")
            }
        }
    }
    
    if (showScheduleDialog) {
        AddScheduleDialog(
            onDismiss = { showScheduleDialog = false },
            onAdd = { day, start, end ->
                schedules = schedules + ClassSchedule(day, start, end)
                showScheduleDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleDialog(
    onDismiss: () -> Unit,
    onAdd: (Int, String, String) -> Unit
) {
    var selectedDay by remember { mutableStateOf(Calendar.MONDAY) }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:00") }
    
    // Very simple implementation for brevity, ideally reuse time pickers
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Class Time") },
        text = {
            Column {
                // Day Selection
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                     val days = listOf("M" to Calendar.MONDAY, "T" to Calendar.TUESDAY, "W" to Calendar.WEDNESDAY, "T" to Calendar.THURSDAY, "F" to Calendar.FRIDAY)
                     days.forEach { (label, dayConst) ->
                         FilterChip(
                             selected = selectedDay == dayConst,
                             onClick = { selectedDay = dayConst },
                             label = { Text(label) }
                         )
                     }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Time inputs (simplified as text for now, should use TimePicker)
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start (HH:mm)") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onAdd(selectedDay, startTime, endTime) }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
