package com.example.tasks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*
import kotlin.random.Random

@Composable
fun HabitDashboard(habits: List<com.example.tasks.data.Habit>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            HabitOverviewHeader(habits = habits)
        }

        item {
            Text(
                "Daily Goals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            DailyGoals(habits = habits)
        }
        
        item {
            Text(
                "Activity Heatmap",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            ActivityHeatmap()
        }
        
        item {
            Text(
                "Your Streaks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        items(habits) { habit ->
            StreakCard(
                habitName = habit.name,
                streak = habit.streak,
                color = habit.color
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun HabitOverviewHeader(habits: List<com.example.tasks.data.Habit>) {
    val total = habits.size
    val completed = habits.count { it.isCompletedToday }
    val completionRate = if (total > 0) (completed * 100 / total) else 0
    val bestStreak = habits.maxOfOrNull { it.streak } ?: 0

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            label = "Completion",
            value = "$completionRate%",
            icon = Icons.Default.TrendingUp,
            color = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Best Streak",
            value = "$bestStreak Days",
            icon = Icons.Default.LocalFireDepartment,
            color = Color(0xFFFF9800),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(24.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Column {
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = color)
                Text(label, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun ActivityHeatmap() {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Last 90 Days", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                   Text("Less", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                   Spacer(modifier = Modifier.width(4.dp))
                   listOf(0.1f, 0.4f, 0.7f, 1.0f).forEach { alpha ->
                       Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha)))
                       Spacer(modifier = Modifier.width(2.dp))
                   }
                   Spacer(modifier = Modifier.width(4.dp))
                   Text("More", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Grid of colored squares
            val random = Random(42)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                for (row in 0 until 7) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (col in 0 until 20) {
                            val intensity = random.nextFloat()
                            val color = if (intensity < 0.2f) {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.primary.copy(alpha = intensity)
                            }
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(color)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StreakCard(
    habitName: String,
    streak: Int,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = color)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(habitName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("$streak days streak", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            
            CircularProgressIndicator(
                progress = 0.7f,
                modifier = Modifier.size(32.dp),
                color = color,
                strokeWidth = 3.dp,
                trackColor = color.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun DailyGoals(habits: List<com.example.tasks.data.Habit>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(habits) { habit ->
            GoalCircle(
                label = habit.name, 
                progress = if (habit.isCompletedToday) 1f else 0.4f, // 0.4f as a 'started' placeholder if not done? No, just 1 or 0
                color = habit.color,
                modifier = Modifier.width(100.dp)
            )
        }
    }
}

@Composable
fun GoalCircle(
    label: String,
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
                CircularProgressIndicator(
                    progress = 1.0f,
                    modifier = Modifier.fillMaxSize(),
                    color = color.copy(alpha = 0.1f),
                    strokeWidth = 6.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxSize(),
                    color = color,
                    strokeWidth = 6.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Text(
                    "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
