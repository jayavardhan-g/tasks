package com.example.tasks.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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
    
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val sheetState = rememberModalBottomSheetState()

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
            .background(Color.White)
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    if (dragAmount < -10) { // Swiped up
                        showDatePicker = true
                    }
                }
            }
    ) {
        androidx.compose.material3.Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 0.5.dp)
        
        // Drag Handle
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(width = 30.dp, height = 4.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.4f))
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { showDatePicker = true }
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentMonthYear,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
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

    if (showDatePicker) {
        ModalBottomSheet(
            onDismissRequest = { showDatePicker = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    title = null,
                    headline = null
                )
                
                LaunchedEffect(datePickerState.selectedDateMillis) {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { 
                            timeInMillis = millis 
                        }
                        
                        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        outputFormat.timeZone = TimeZone.getTimeZone("UTC")
                        val dateStr = outputFormat.format(Date(millis))
                        
                        val localCal = Calendar.getInstance().apply {
                            set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                        }
                        selectedDate = localCal
                        
                        onDateSelected(dateStr)
                        showDatePicker = false
                    }
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
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .width(40.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = dayLetter,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF0056B3) else Color.LightGray
        )
        
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isSelected -> Color(0xFF0056B3)
                        isToday -> Color(0xFFF0F5FA)
                        else -> Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.toString(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
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
