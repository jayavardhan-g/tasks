package com.example.tasks.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Task::class, Workspace::class, ChecklistItem::class, Habit::class, HabitHistory::class], version = 9, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun workspaceDao(): WorkspaceDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: android.content.Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
