package com.example.data.remote.model

import com.example.data.entity.CourseEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

/**
 * Firestore remote DTO for dynamic course content synchronization.
 * Uses default parameter values to provide the required synthetic no-arg constructor.
 */
@IgnoreExtraProperties
data class CourseFirestoreDto(
  @DocumentId
  val id: String = "",
  val title: String = "",
  val subtitle: String = "",
  val description: String = "",
  val instructorName: String = "Unique_editor_ji",
  val currentPriceInr: Long = 999L,
  val originalPriceInr: Long = 4999L,
  val promoBadgeText: String = "",
  val bannerUrl: String = "",
  val isPublished: Boolean = true,
  val totalLessonsCount: Long = 0L,
  val totalDuration: String = "",
  @ServerTimestamp
  val createdAt: Timestamp? = null,
  @ServerTimestamp
  val updatedAt: Timestamp? = null
) {
  fun toEntity(syncStatus: String = "SYNCED"): CourseEntity {
    return CourseEntity(
      id = id.ifBlank { "course_default" },
      title = title,
      subtitle = subtitle,
      description = description,
      instructorName = instructorName,
      currentPriceInr = currentPriceInr.toInt(),
      originalPriceInr = originalPriceInr.toInt(),
      promoBadgeText = promoBadgeText,
      bannerUrl = bannerUrl,
      isPublished = isPublished,
      totalLessonsCount = totalLessonsCount.toInt(),
      totalDuration = totalDuration,
      lastSyncedTimestamp = System.currentTimeMillis(),
      syncStatus = syncStatus
    )
  }

  companion object {
    fun fromEntity(entity: CourseEntity): CourseFirestoreDto {
      return CourseFirestoreDto(
        id = entity.id,
        title = entity.title,
        subtitle = entity.subtitle,
        description = entity.description,
        instructorName = entity.instructorName,
        currentPriceInr = entity.currentPriceInr.toLong(),
        originalPriceInr = entity.originalPriceInr.toLong(),
        promoBadgeText = entity.promoBadgeText,
        bannerUrl = entity.bannerUrl,
        isPublished = entity.isPublished,
        totalLessonsCount = entity.totalLessonsCount.toLong(),
        totalDuration = entity.totalDuration
      )
    }
  }
}
