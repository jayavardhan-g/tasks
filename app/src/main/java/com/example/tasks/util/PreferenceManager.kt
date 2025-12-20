package com.example.tasks.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TIMELINE_MODE = "timeline_mode"
    }

    fun getTimelineMode(): String {
        return sharedPreferences.getString(KEY_TIMELINE_MODE, "DEFAULT") ?: "DEFAULT"
    }

    fun setTimelineMode(mode: String) {
        sharedPreferences.edit().putString(KEY_TIMELINE_MODE, mode).apply()
    }
}
