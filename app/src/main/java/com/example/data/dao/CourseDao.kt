package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
  @Query("SELECT * FROM courses ORDER BY title ASC")
  fun getAllCourses(): Flow<List<CourseEntity>>

  @Query("SELECT * FROM courses ORDER BY title ASC")
  suspend fun getAllCoursesSync(): List<CourseEntity>

  @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
  fun getCourseById(id: String): Flow<CourseEntity?>

  @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
  suspend fun getCourseByIdSync(id: String): CourseEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCourse(course: CourseEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCourses(courses: List<CourseEntity>)

  @Update
  suspend fun updateCourse(course: CourseEntity)

  @Query("UPDATE courses SET syncStatus = :syncStatus, lastSyncedTimestamp = :timestamp WHERE id = :id")
  suspend fun updateSyncStatus(id: String, syncStatus: String, timestamp: Long)

  @Query("DELETE FROM courses WHERE id = :id")
  suspend fun deleteCourseById(id: String)
}
