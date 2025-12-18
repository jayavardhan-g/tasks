package com.example.tasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CalendarStrip(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFE8F0F8)) // Light bluish background
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "December 2025",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = "Expand Calendar",
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val days = listOf("S", "M", "T", "W", "T", "F", "S")
            val dates = listOf(14, 15, 16, 17, 18, 19, 20)
            val selectedDate = 18
            val hasTask = listOf(14, 16, 17) // Examples of days with tasks
            
            dates.forEachIndexed { index, date ->
                CalendarDay(
                    dayLetter = days[index],
                    date = date,
                    isSelected = date == selectedDate,
                    hasTask = hasTask.contains(date)
                )
            }
        }
    }
}

@Composable
fun CalendarDay(
    dayLetter: String,
    date: Int,
    isSelected: Boolean,
    hasTask: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = dayLetter,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFF0056B3) else Color.Transparent)
                .clickable { /* Handle select */ },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color.Black
            )
        }
        
        if (hasTask) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
            )
        } else {
            // Invisible spacer to maintain alignment
            Spacer(modifier = Modifier.size(4.dp))
        }
    }
}
