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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.toArgb

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitSheet(
    onDismiss: () -> Unit,
    onSave: (String, String, Long, Int, String?, String, Int, String, Int, String?, String, String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    // State
    var name by remember { mutableStateOf("") }
    var selectedIconName by remember { mutableStateOf("WaterDrop") }
    var selectedColor by remember { mutableStateOf(Color(0xFF2196F3)) }
    var targetValue by remember { mutableStateOf(1) }
    var unit by remember { mutableStateOf("") }
    
    // New State
    var frequencyType by remember { mutableStateOf("DAILY") }
    var frequencyGoal by remember { mutableStateOf(1) }
    var frequencyDays by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var repeatInterval by remember { mutableStateOf(1) }
    
    var isReminderOn by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf("09:00") } // HH:mm
    
    var priority by remember { mutableStateOf("MEDIUM") }
    var description by remember { mutableStateOf("") }

    // Data
    val icons = listOf(
        "WaterDrop" to Icons.Default.WaterDrop,
        "MenuBook" to Icons.Default.MenuBook,
        "FitnessCenter" to Icons.Default.FitnessCenter,
        "DirectionsRun" to Icons.Default.DirectionsRun,
        // ... (truncated for brevity, keep existing)
        "SelfImprovement" to Icons.Default.SelfImprovement,
        "Brush" to Icons.Default.Brush,
        "Code" to Icons.Default.Code,
        "NightsStay" to Icons.Default.NightsStay
    )

    val colors = listOf(
        Color(0xFF2196F3), // Blue
        Color(0xFFFF9800), // Orange
        Color(0xFF4CAF50), // Green
        Color(0xFFF44336), // Red
        Color(0xFF9C27B0), // Purple
        Color(0xFF009688)  // Teal
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                width = 40.dp,
                height = 4.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()), // Enable scrolling
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "New Habit",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Habit Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            
            // Description/Motivation
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Motivation (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // Target & Unit (Horizontal Row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Target Stepper
                Column(modifier = Modifier.weight(1f)) {
                    Text("Daily Target", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    ) {
                        IconButton(
                            onClick = { if (targetValue > 1) targetValue-- },
                            enabled = targetValue > 1
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        Text(
                            text = targetValue.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(onClick = { targetValue++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }

                // Unit Input
                if (targetValue > 1) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit (e.g. cups)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
            
            // Frequency Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Frequency", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                
                // Frequency Type Selector
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val types = listOf("DAILY" to "Daily", "WEEKLY_GOAL" to "Weekly", "SPECIFIC_DAYS" to "Fixed", "REPEAT_INTERVAL" to "Interval")
                    types.forEach { (type, label) ->
                        FilterChip(
                            selected = frequencyType == type,
                            onClick = { frequencyType = type },
                            label = { Text(label) }
                        )
                    }
                }
                
                // Dynamic Frequency Content
                when (frequencyType) {
                    "WEEKLY_GOAL" -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                           Text("Goal: $frequencyGoal times / week", modifier = Modifier.weight(1f))
                           IconButton(onClick = { if(frequencyGoal > 1) frequencyGoal-- }) { Icon(Icons.Default.Remove, null) }
                           Text("$frequencyGoal", style = MaterialTheme.typography.titleMedium)
                           IconButton(onClick = { if(frequencyGoal < 7) frequencyGoal++ }) { Icon(Icons.Default.Add, null) }
                        }
                    }
                    "SPECIFIC_DAYS" -> {
                        // Days Toggle
                        // 1=Mon, 7=Sun
                        val days = listOf("M", "T", "W", "T", "F", "S", "S")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            days.forEachIndexed { index, dayLabel ->
                                val dayNum = index + 1
                                val isSelected = frequencyDays.contains(dayNum)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { 
                                        frequencyDays = if (isSelected) frequencyDays - dayNum else frequencyDays + dayNum
                                    },
                                    label = { Text(dayLabel) },
                                    modifier = Modifier.size(32.dp),
                                    shape = CircleShape,
                                    // contentPadding = PaddingValues(0.dp) // Might need custom layout for small circles
                                )
                            }
                        }
                    }
                    "REPEAT_INTERVAL" -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Every $repeatInterval days", modifier = Modifier.weight(1f))
                            IconButton(onClick = { if(repeatInterval > 1) repeatInterval-- }) { Icon(Icons.Default.Remove, null) }
                            Text("$repeatInterval", style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = { repeatInterval++ }) { Icon(Icons.Default.Add, null) }
                        }
                    }
                }
            }
            
            // Reminders Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                   modifier = Modifier.fillMaxWidth(),
                   horizontalArrangement = Arrangement.SpaceBetween,
                   verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Set Reminder", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Switch(checked = isReminderOn, onCheckedChange = { isReminderOn = it })
                }
                
                if (isReminderOn) {
                    // Simple Time Input for MVP (Replace with TimePicker later if needed)
                    OutlinedTextField(
                        value = reminderTime,
                        onValueChange = { reminderTime = it }, // No validation for now
                        label = { Text("Time (HH:mm)") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.Notifications, null) }
                    )
                }
            }
            
            // Priority
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Priority", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val priorities = listOf("LOW", "MEDIUM", "HIGH")
                    priorities.forEach { p ->
                        InputChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Icon Selection
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Icon", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(130.dp) // Fixed height for 2 rows roughly
                ) {
                    items(icons) { (iconName, iconVector) ->
                        HabitIconItem(
                            icon = iconVector,
                            isSelected = selectedIconName == iconName,
                            color = if (selectedIconName == iconName) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            onClick = { selectedIconName = iconName }
                        )
                    }
                }
            }

            // Color Selection
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Color", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    colors.forEach { color ->
                        ColorItem(
                            color = color,
                            isSelected = selectedColor == color,
                            onClick = { selectedColor = color }
                        )
                    }
                }
            }

            // Save Button
            Button(
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Create Habit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp)) 
        }
    }
}

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
            .size(40.dp) // Slightly bigger touch target
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
