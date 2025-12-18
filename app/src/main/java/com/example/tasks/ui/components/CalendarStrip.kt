package com.example.tasks.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarStrip(
    modifier: Modifier = Modifier,
    onDateSelected: (String) -> Unit = {}
) {
    val totalPages = 1000
    val initialPage = totalPages / 2
    val pagerState = rememberPagerState(initialPage = initialPage) { totalPages }
    
    var currentMonthYear by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    // Update month/year header when page changes
    LaunchedEffect(pagerState.currentPage) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, pagerState.currentPage - initialPage)
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        currentMonthYear = monthFormat.format(calendar.time)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White) // Changed to white to match the new design
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
             Text(
                text = currentMonthYear,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = "Expand Calendar",
                modifier = Modifier.size(20.dp),
                tint = Color.Gray
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val weekDates = remember(page) {
                val calendar = Calendar.getInstance()
                // Set to start of current week
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.add(Calendar.WEEK_OF_YEAR, page - initialPage)
                
                (0..6).map { i ->
                    (calendar.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, i) }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val days = listOf("S", "M", "T", "W", "T", "F", "S")
                
                weekDates.forEachIndexed { index, date ->
                    val isSelected = isSameDay(date, selectedDate)
                    val isToday = isSameDay(date, Calendar.getInstance())
                    
                    CalendarDay(
                        dayLetter = days[index],
                        date = date.get(Calendar.DAY_OF_MONTH),
                        isSelected = isSelected,
                        isToday = isToday,
                        onClick = { 
                            selectedDate = date
                            onDateSelected(dateFormat.format(date.time))
                        }
                    )
                }
            }
        }
    }
}

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

@Composable
fun CalendarDay(
    dayLetter: String,
    date: Int,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .width(48.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = dayLetter,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color(0xFF0056B3) else Color.Gray
        )
        
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isSelected -> Color(0xFF0056B3)
                        isToday -> Color(0xFFE8F0F8)
                        else -> Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isSelected -> Color.White
                    isToday -> Color(0xFF0056B3)
                    else -> Color.Black
                }
            )
        }
        
        // Dot for selected or today? Let's just use a simple dot if it's today
        if (isToday && !isSelected) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0056B3))
            )
        } else {
            Spacer(modifier = Modifier.size(4.dp))
        }
    }
}
