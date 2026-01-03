package com.example.tasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SummaryCards(
    todoCount: Int,
    overdueCount: Int,
    unplannedCount: Int,
    onTodoClick: () -> Unit = {},
    onOverdueClick: () -> Unit = {},
    onUnplannedClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            count = todoCount,
            label = "To Do",
            onClick = onTodoClick,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            count = overdueCount,
            label = "Overdue",
            onClick = onOverdueClick,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            count = unplannedCount,
            label = "Unplanned",
            onClick = onUnplannedClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryCard(
    count: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF0F4F8)) // Light bluish gray
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = Color.Black
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
    }
}
