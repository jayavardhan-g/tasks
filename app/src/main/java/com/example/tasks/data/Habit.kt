package com.example.tasks.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.room.*
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val iconName: String,
    val colorHex: Long,
    val streak: Int = 0,
    val lastCompletedDate: String? = null, // yyyy-MM-dd
    val targetValue: Int = 1,
    val currentProgress: Int = 0,
    val progressDate: String? = null, // yyyy-MM-dd
    val unit: String? = null
) {
    @Ignore
    val color: Color = Color(colorHex.toInt())

    @Ignore
    val icon: ImageVector = when (iconName) {
        "WaterDrop" -> Icons.Default.WaterDrop
        "MenuBook" -> Icons.Default.MenuBook
        "FitnessCenter" -> Icons.Default.FitnessCenter
        "DirectionsRun" -> Icons.Default.DirectionsRun
        "SelfImprovement" -> Icons.Default.SelfImprovement
        "Brush" -> Icons.Default.Brush
        "Code" -> Icons.Default.Code
        "NightsStay" -> Icons.Default.NightsStay
        else -> Icons.Default.Check
    }

    @Ignore
    val isCompletedToday: Boolean = lastCompletedDate == SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}

@Entity(
    tableName = "habit_history",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("habitId"), Index("date")]
)
data class HabitHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val habitId: String,
    val date: String, // yyyy-MM-dd
    val isCompleted: Boolean = true
)
