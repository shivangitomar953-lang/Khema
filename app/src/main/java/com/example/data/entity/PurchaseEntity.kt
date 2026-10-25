package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class PurchaseEntity(
  @PrimaryKey
  val id: String,
  val orderId: String,
  val studentId: String,
  val studentName: String,
  val studentEmail: String,
  val amountInr: Int,
  val originalPriceInr: Int,
  val paymentMethod: String,
  val paymentId: String,
  val signature: String,
  val timestamp: Long,
  val status: String = "VERIFIED",
  val courseTitle: String
)
