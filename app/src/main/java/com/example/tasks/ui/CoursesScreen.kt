package com.example.tasks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tasks.data.AttendanceRecord
import com.example.tasks.data.Course
import com.example.tasks.ui.components.AddCourseSheet
import java.util.Calendar

@Composable
fun CoursesScreen(
    viewModel: TasksViewModel
) {
    val courses by viewModel.allCourses.observeAsState(emptyList())

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Courses",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (courses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No courses added yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                courses.forEach { course ->
                        item(key = course.id) {
                            CourseCard(course = course, viewModel = viewModel)
                        }
                }
            }
        }
    }
}

@Composable
fun CourseCard(course: Course, viewModel: TasksViewModel) {
    // Determine attendance percentage
    val attendance by viewModel.getAttendanceForCourse(course.id).observeAsState(emptyList())
    val totalClasses = attendance.count { it.status != "CANCELLED" }
    val attended = attendance.count { it.status == "PRESENT" }
    val percentage = if (totalClasses > 0) (attended.toFloat() / totalClasses.toFloat()) * 100 else 0f
    
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(course.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(course.professor, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (course.location.isNotBlank()) {
                         Text(course.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(course.color, androidx.compose.foundation.shape.CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Attendance Visualization
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = percentage / 100f,
                    modifier = Modifier.weight(1f).height(8.dp),
                    color = if (percentage >= 75) Color(0xFF4CAF50) else if (percentage >= 50) Color(0xFFFF9800) else Color(0xFFF44336),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${percentage.toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                "Attended $attended / $totalClasses classes", 
                style = MaterialTheme.typography.labelSmall, 
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
