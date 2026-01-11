package com.example.tasks.data

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val professor: String,
    val location: String,
    val colorHex: Long,
    val scheduleJson: String // Serialized List<ClassSchedule>
) {
    @Ignore
    val color: Color = Color(colorHex.toInt())
}

data class ClassSchedule(
    val dayOfWeek: Int, // Calendar.MONDAY (2) ... Calendar.SUNDAY (1) or however Calendar constants map usually
    val startTime: String, // "HH:mm" 24h format e.g. "14:30"
    val endTime: String // "HH:mm"
)

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("courseId"), Index("date")]
)
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val courseId: Int,
    val date: String, // "yyyy-MM-dd"
    val status: String // "PRESENT", "ABSENT", "CANCELLED"
)
