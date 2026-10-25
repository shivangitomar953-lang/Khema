package com.example.data.repository

import android.util.Log
import com.example.data.dao.CourseDao
import com.example.data.entity.CourseEntity
import com.example.data.remote.model.CourseFirestoreDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Repository orchestrating Room local caching with optional Firestore remote synchronization.
 * Offline-first: Room serves as the immediate source of truth for the UI, while Firestore
 * powers multi-device remote dynamic updates.
 */
class CourseSyncRepository(
  private val courseDao: CourseDao,
  private val firestore: FirebaseFirestore? = null
) {
  val cachedCourses: Flow<List<CourseEntity>> = courseDao.getAllCourses()

  suspend fun getCourseById(id: String): CourseEntity? {
    return courseDao.getCourseByIdSync(id)
  }

  /**
   * Saves course to local Room database immediately.
   * If Firestore is available and sync is requested, asynchronously writes to remote.
   */
  suspend fun saveCourse(course: CourseEntity, syncToRemote: Boolean = true): Result<CourseEntity> =
    withContext(Dispatchers.IO) {
      try {
        val initialStatus = if (syncToRemote && firestore != null) "PENDING_SYNC" else "LOCAL_ONLY"
        val courseToSave = course.copy(syncStatus = initialStatus)
        courseDao.insertCourse(courseToSave)

        if (syncToRemote && firestore != null) {
          try {
            val dto = CourseFirestoreDto.fromEntity(courseToSave)
            firestore.collection("courses")
              .document(courseToSave.id)
              .set(dto)
              .await()

            val now = System.currentTimeMillis()
            courseDao.updateSyncStatus(courseToSave.id, "SYNCED", now)
            return@withContext Result.success(courseToSave.copy(syncStatus = "SYNCED", lastSyncedTimestamp = now))
          } catch (e: Exception) {
            Log.w("CourseSyncRepo", "Remote Firestore sync failed; cached locally: ${e.message}")
          }
        }
        Result.success(courseToSave)
      } catch (e: Exception) {
        Log.e("CourseSyncRepo", "Failed to save course: ${e.message}", e)
        Result.failure(e)
      }
    }

  /**
   * Pulls latest courses from Firestore remote collection and updates Room local cache.
   */
  suspend fun syncFromRemote(): Result<Int> = withContext(Dispatchers.IO) {
    if (firestore == null) {
      return@withContext Result.failure(IllegalStateException("Firestore is not initialized."))
    }
    try {
      val snapshot = firestore.collection("courses").get().await()
      val remoteCourses = snapshot.documents.mapNotNull { doc ->
        doc.toObject(CourseFirestoreDto::class.java)?.toEntity(syncStatus = "SYNCED")
      }

      if (remoteCourses.isNotEmpty()) {
        courseDao.insertCourses(remoteCourses)
      }
      Log.d("CourseSyncRepo", "Synced ${remoteCourses.size} courses from Firestore to Room cache")
      Result.success(remoteCourses.size)
    } catch (e: Exception) {
      Log.e("CourseSyncRepo", "Firestore pull sync failed: ${e.message}", e)
      Result.failure(e)
    }
  }

  suspend fun deleteCourse(id: String, deleteRemote: Boolean = true): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        courseDao.deleteCourseById(id)
        if (deleteRemote && firestore != null) {
          try {
            firestore.collection("courses").document(id).delete().await()
          } catch (e: Exception) {
            Log.w("CourseSyncRepo", "Remote deletion failed for course $id: ${e.message}")
          }
        }
        Result.success(Unit)
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
}
