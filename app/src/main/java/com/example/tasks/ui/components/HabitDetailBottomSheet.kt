package com.example.tasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasks.data.Habit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailBottomSheet(
    habit: Habit,
    onDismiss: () -> Unit,
    onToggle: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Icon and Streak
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(habit.color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    habit.icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = habit.color
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = habit.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Whatshot,
                    contentDescription = "Streak",
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${habit.streak} day streak",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFFF9800),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Total", value = "24", habit.color)
                StatItem(label = "This Week", value = "5/7", habit.color)
                StatItem(label = "Consistency", value = "85%", habit.color)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Weekly Progress View (Placeholder)
            Text(
                text = "Last 7 Days",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.Start),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                val completed = listOf(true, true, false, true, true, habit.isCompletedToday, false)
                days.forEachIndexed { index, day ->
                    WeeklyDayCircle(day = day, isCompleted = completed[index], color = habit.color)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Toggle Button
            Button(
                onClick = {
                    onToggle()
                    // onDismiss() // Optional: keep open or close
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (habit.isCompletedToday) Color.Gray.copy(alpha = 0.2f) else habit.color,
                    contentColor = if (habit.isCompletedToday) MaterialTheme.colorScheme.onSurface else Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(
                    text = if (habit.isCompletedToday) "Mark Incomplete" else "Complete for Today",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun WeeklyDayCircle(day: String, isCompleted: Boolean, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    if (isCompleted) color else color.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = day, style = MaterialTheme.typography.labelSmall)
    }
}

// Add missing check icon import handled by choosing right vector if needed.
// Need to add import for Icons.Default.Check
