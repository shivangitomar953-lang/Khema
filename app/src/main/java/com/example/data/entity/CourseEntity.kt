package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for local caching of Course content.
 * Serves as the single source of truth for offline-first UI display.
 */
@Entity(tableName = "courses")
data class CourseEntity(
  @PrimaryKey
  val id: String,
  val title: String,
  val subtitle: String,
  val description: String,
  val instructorName: String = "Unique_editor_ji",
  val currentPriceInr: Int = 999,
  val originalPriceInr: Int = 4999,
  val promoBadgeText: String = "80% DISCOUNT",
  val bannerUrl: String = "",
  val isPublished: Boolean = true,
  val totalLessonsCount: Int = 8,
  val totalDuration: String = "2h 45m",
  val lastSyncedTimestamp: Long = System.currentTimeMillis(),
  val syncStatus: String = "LOCAL_ONLY" // "SYNCED", "PENDING_SYNC", "LOCAL_ONLY"
)
