package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
  @PrimaryKey
  val id: String,
  val name: String,
  val email: String,
  val phone: String,
  val avatarUrl: String = "",
  val enrolledTimestamp: Long,
  val hasPaidAccess: Boolean = false,
  val completedLessonsJson: String = "[]"
)
