package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class LessonEntity(
  @PrimaryKey
  val id: String,
  val moduleId: String,
  val title: String,
  val description: String,
  val videoUrl: String,
  val duration: String,
  val thumbnailRes: String = "lesson_thumb_cinematic_1792954791251",
  val thumbnailUrl: String = "",
  val orderIndex: Int,
  val isPublished: Boolean = true,
  val isFreePreview: Boolean = false,
  val viewsCount: Int = 0
)
