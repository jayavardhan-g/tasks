package com.example.tasks.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tasks.MainActivity
import com.example.tasks.R
import com.example.tasks.data.Task

object NotificationHelper {
    const val CHANNEL_ID = "pinned_tasks_channel"
    private const val CHANNEL_NAME = "Pinned Tasks"
    private const val CHANNEL_DESCRIPTION = "Notifications for pinned tasks"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_LOW 
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showTaskNotification(context: Context, task: Task) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val dateFormat = java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault())
        val deadlineStr = dateFormat.format(java.util.Date(task.deadline))

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Fallback or existing icon
            .setContentTitle("${task.title} [ $deadlineStr ]")
            .setContentText(task.description)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true) // Makes it pinned/persistent
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)

        try {
             with(NotificationManagerCompat.from(context)) {
                notify(task.id, builder.build())
            }
        } catch (e: SecurityException) {
            // Handle missing permission
        }
    }

    fun cancelTaskNotification(context: Context, taskId: Int) {
        try {
            with(NotificationManagerCompat.from(context)) {
                cancel(taskId)
            }
        } catch (e: SecurityException) {
            // Handle missing permission
        }
    }
}
