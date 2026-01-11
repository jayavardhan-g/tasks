package com.example.tasks.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<Course>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course): Long

    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecord>>
    
    @Query("SELECT * FROM attendance WHERE courseId = :courseId")
    fun getAttendanceForCourse(courseId: Int): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecord)
    
    @Query("DELETE FROM attendance WHERE courseId = :courseId AND date = :date")
    suspend fun deleteAttendance(courseId: Int, date: String)
}
