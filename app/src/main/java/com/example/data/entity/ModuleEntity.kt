package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modules")
data class ModuleEntity(
  @PrimaryKey
  val id: String,
  val title: String,
  val description: String,
  val orderIndex: Int
)
