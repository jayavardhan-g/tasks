package com.example.tasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.toArgb

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitSheet(
    onDismiss: () -> Unit,
    onSave: (String, String, Long, Int, String?, String, Int, String, Int, String?, String, String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    
    // State
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") } // Motivation
    
    var selectedIconName by remember { mutableStateOf("WaterDrop") }
    var selectedColor by remember { mutableStateOf(Color(0xFF2196F3)) }
    
    var targetValue by remember { mutableStateOf(1) }
    var unit by remember { mutableStateOf("") }
    
    var frequencyType by remember { mutableStateOf("DAILY") }
    var frequencyGoal by remember { mutableStateOf(1) }
    var frequencyDays by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var repeatInterval by remember { mutableStateOf(1) }
    
    var isReminderOn by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf("09:00") }
    
    var priority by remember { mutableStateOf("MEDIUM") }

    // Sub-sheet states
    var showColorIconSheet by remember { mutableStateOf(false) }
    var showTargetSheet by remember { mutableStateOf(false) }
    var showFrequencySheet by remember { mutableStateOf(false) }
    var showReminderSheet by remember { mutableStateOf(false) }
    var showPrioritySheet by remember { mutableStateOf(false) }

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
            // --- Header: Name & Save ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Name Input (BasicTextField style)
                androidx.compose.foundation.text.BasicTextField(
                    value = name,
                    onValueChange = { name = it },
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (name.isEmpty()) {
                                Text("New Habit", style = TextStyle(fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Save Button (Icon style)
                IconButton(
                    onClick = { 
                        if (name.isNotBlank()) {
                            onSave(
                                name, 
                                selectedIconName, 
                                selectedColor.toArgb().toLong(), 
                                targetValue, 
                                if (targetValue > 1) unit else null,
                                frequencyType,
                                frequencyGoal,
                                frequencyDays.joinToString(","),
                                repeatInterval,
                                if (isReminderOn) reminderTime else null,
                                priority,
                                description.takeIf { it.isNotBlank() }
                            )
                            onDismiss()
                        }
                    },
                    enabled = name.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Save", tint = if (name.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            // --- Motivation / Description ---
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
                            Text("Motivation...", style = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Option Rows ---

            // 1. Color & Icon
            HabitOptionRow(
                icon = Icons.Default.ColorLens,
                text = "Color & Icon",
                valueText = selectedIconName, // Could show icon or color box here ideally
                color = selectedColor,
                onClick = { showColorIconSheet = true }
            )

            // 2. Target
            HabitOptionRow(
                icon = Icons.Default.TrackChanges,
                text = "Daily Target",
                valueText = "$targetValue ${if (targetValue > 1 && unit.isNotBlank()) unit else ""}",
                onClick = { showTargetSheet = true }
            )

            // 3. Frequency
            HabitOptionRow(
                icon = Icons.Default.Repeat,
                text = "Frequency",
                valueText = when(frequencyType) {
                    "DAILY" -> "Daily"
                    "WEEKLY_GOAL" -> "$frequencyGoal/week"
                    "SPECIFIC_DAYS" -> "Fixed days"
                    "REPEAT_INTERVAL" -> "Every $repeatInterval days"
                    else -> "Daily"
                },
                onClick = { showFrequencySheet = true }
            )

            // 4. Reminder
             HabitOptionRow(
                icon = Icons.Default.Notifications,
                text = "Reminder",
                valueText = if (isReminderOn) reminderTime else "Off",
                onClick = { showReminderSheet = true }
            )

            // 5. Priority
             HabitOptionRow(
                icon = Icons.Default.Flag,
                text = "Priority",
                valueText = priority,
                onClick = { showPrioritySheet = true }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- Sub-Sheets / Dialogs ---
        
        if (showColorIconSheet) {
            ColorIconBottomSheet(
                initialColor = selectedColor,
                initialIconName = selectedIconName,
                onDismiss = { showColorIconSheet = false },
                onSave = { c, i ->
                    selectedColor = c
                    selectedIconName = i
                    showColorIconSheet = false
                }
            )
        }
        
        if (showTargetSheet) {
            TargetBottomSheet(
                initialTarget = targetValue,
                initialUnit = unit,
                onDismiss = { showTargetSheet = false },
                onSave = { t, u ->
                    targetValue = t
                    unit = u
                    showTargetSheet = false
                }
            )
        }
        
        if (showFrequencySheet) {
            FrequencyBottomSheet(
                initialType = frequencyType,
                initialGoal = frequencyGoal,
                initialDays = frequencyDays,
                initialInterval = repeatInterval,
                onDismiss = { showFrequencySheet = false },
                onSave = { t, g, d, i ->
                    frequencyType = t
                    frequencyGoal = g
                    frequencyDays = d
                    repeatInterval = i
                    showFrequencySheet = false
                }
            )
        }
        
        if (showReminderSheet) {
             ReminderBottomSheet(
                initialIsOn = isReminderOn,
                initialTime = reminderTime,
                onDismiss = { showReminderSheet = false },
                onSave = { isOn, time ->
                    isReminderOn = isOn
                    reminderTime = time
                    showReminderSheet = false
                }
            )
        }
        
        if (showPrioritySheet) {
            HabitPriorityBottomSheet(
                initialPriority = priority,
                onDismiss = { showPrioritySheet = false },
                onSave = { p -> 
                    priority = p
                    showPrioritySheet = false
                }
            )
        }
    }
}

@Composable
fun HabitOptionRow(
    icon: ImageVector,
    text: String,
    valueText: String? = null,
    color: Color? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (color != null) {
             Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(color))
        } else {
             Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (valueText != null) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// --- Sub-Sheet Implementations (simplified for file length) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorIconBottomSheet(
    initialColor: Color,
    initialIconName: String,
    onDismiss: () -> Unit,
    onSave: (Color, String) -> Unit
) {
    var selectedColor by remember { mutableStateOf(initialColor) }
    var selectedIconName by remember { mutableStateOf(initialIconName) }
     
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Color & Icon", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Reusing existing selection UI logic basically
            val colors = listOf(Color(0xFF2196F3), Color(0xFFFF9800), Color(0xFF4CAF50), Color(0xFFF44336), Color(0xFF9C27B0), Color(0xFF009688))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                colors.forEach { color ->
                    ColorItem(color = color, isSelected = selectedColor == color, onClick = { selectedColor = color })
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            val icons = listOf("WaterDrop" to Icons.Default.WaterDrop, "FitnessCenter" to Icons.Default.FitnessCenter, "MenuBook" to Icons.Default.MenuBook, "DirectionsRun" to Icons.Default.DirectionsRun, "SelfImprovement" to Icons.Default.SelfImprovement, "Brush" to Icons.Default.Brush, "Code" to Icons.Default.Code, "NightsStay" to Icons.Default.NightsStay)
            LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.height(150.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(icons) { (name, vector) ->
                    HabitIconItem(icon = vector, isSelected = selectedIconName == name, color = if (selectedIconName == name) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant, onClick = { selectedIconName = name })
                }
            }
            
             Button(onClick = { onSave(selectedColor, selectedIconName) }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) { Text("Done") }
             Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetBottomSheet(
    initialTarget: Int,
    initialUnit: String,
    onDismiss: () -> Unit,
    onSave: (Int, String) -> Unit
) {
    var target by remember { mutableStateOf(initialTarget) }
    var unit by remember { mutableStateOf(initialUnit) }
    
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Daily Goal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if(target > 1) target-- }) { Icon(Icons.Default.Remove, null) }
                Text(target.toString(), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(horizontal = 16.dp))
                IconButton(onClick = { target++ }) { Icon(Icons.Default.Add, null) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit (Optional)") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { onSave(target, unit) }, modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) { Text("Done") }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrequencyBottomSheet(
    initialType: String,
    initialGoal: Int,
    initialDays: Set<Int>,
    initialInterval: Int,
    onDismiss: () -> Unit,
    onSave: (String, Int, Set<Int>, Int) -> Unit
) {
    var type by remember { mutableStateOf(initialType) }
    var goal by remember { mutableStateOf(initialGoal) }
    var days by remember { mutableStateOf(initialDays) }
    var interval by remember { mutableStateOf(initialInterval) }
    
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Frequency", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
             Spacer(modifier = Modifier.height(16.dp))
             
             // Reuse chips logic (simplified)
             Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  listOf("DAILY", "WEEKLY_GOAL", "SPECIFIC_DAYS", "REPEAT_INTERVAL").forEach { t ->
                      val label = when(t) { "WEEKLY_GOAL" -> "Weekly"; "SPECIFIC_DAYS" -> "Days"; "REPEAT_INTERVAL" -> "Interval"; else -> "Daily" }
                      FilterChip(selected = type == t, onClick = { type = t }, label = { Text(label) })
                  }
             }
             Spacer(modifier = Modifier.height(16.dp))
             
             when(type) {
                 "WEEKLY_GOAL" -> {
                      Text("Goal: $goal times / week")
                      Slider(value = goal.toFloat(), onValueChange = { goal = it.toInt() }, valueRange = 1f..7f, steps = 5)
                 }
                 "SPECIFIC_DAYS" -> {
                     Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                         val dLabels = listOf("M", "T", "W", "T", "F", "S", "S")
                         dLabels.forEachIndexed { i, l ->
                             val dNum = i + 1
                             FilterChip(selected = days.contains(dNum), onClick = { days = if(days.contains(dNum)) days - dNum else days + dNum }, label = { Text(l) })
                         }
                     }
                 }
                 "REPEAT_INTERVAL" -> {
                     Text("Every $interval days")
                     Slider(value = interval.toFloat(), onValueChange = { interval = it.toInt() }, valueRange = 1f..30f)
                 }
             }
            
            Button(onClick = { onSave(type, goal, days, interval) }, modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) { Text("Done") }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderBottomSheet(
    initialIsOn: Boolean,
    initialTime: String,
    onDismiss: () -> Unit,
    onSave: (Boolean, String) -> Unit
) {
    var isOn by remember { mutableStateOf(initialIsOn) }
    var time by remember { mutableStateOf(initialTime) }
    
    ModalBottomSheet(onDismissRequest = onDismiss) {
         Column(modifier = Modifier.padding(16.dp)) {
            Text("Reminder", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Enable Reminder")
                Switch(checked = isOn, onCheckedChange = { isOn = it })
            }
            if(isOn) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (HH:mm)") }, modifier = Modifier.fillMaxWidth())
            }
            Button(onClick = { onSave(isOn, time) }, modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) { Text("Done") }
            Spacer(modifier = Modifier.height(16.dp))
         }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitPriorityBottomSheet(
    initialPriority: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var priority by remember { mutableStateOf(initialPriority) }
     ModalBottomSheet(onDismissRequest = onDismiss) {
         Column(modifier = Modifier.padding(16.dp)) {
            Text("Priority", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("LOW", "MEDIUM", "HIGH").forEach { p ->
                    FilterChip(selected = priority == p, onClick = { priority = p }, label = { Text(p) })
                }
            }
            Button(onClick = { onSave(priority) }, modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) { Text("Done") }
            Spacer(modifier = Modifier.height(16.dp))
         }
    }
}

// ... Keep existing helpers (HabitIconItem, ColorItem) ...
// (Pasted below for completeness if I were doing a partial edit, but here I am creating a full file or updating heavily. 
// I should ensure the helpers are included or not duplicated. The previous file had them.)

@Composable
fun HabitIconItem(
    icon: ImageVector,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) color.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) color else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun ColorItem(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
