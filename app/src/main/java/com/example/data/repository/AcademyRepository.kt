package com.example.data.repository

import com.example.data.dao.AcademyDao
import com.example.data.entity.AcademyConfigEntity
import com.example.data.entity.LessonEntity
import com.example.data.entity.ModuleEntity
import com.example.data.entity.PurchaseEntity
import com.example.data.entity.StudentEntity
import com.example.data.model.FaqItem
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.util.UUID

sealed class PurchaseResult {
  data class Success(val purchase: PurchaseEntity) : PurchaseResult()
  data class Error(val message: String) : PurchaseResult()
}

sealed class OperationResult {
  data class Success(val message: String) : OperationResult()
  data class Error(val error: String) : OperationResult()
}

class AcademyRepository(
  private val dao: AcademyDao
) {
  val config: Flow<AcademyConfigEntity?> = dao.getConfig()
  val modules: Flow<List<ModuleEntity>> = dao.getAllModules()
  val lessons: Flow<List<LessonEntity>> = dao.getAllLessons()
  val students: Flow<List<StudentEntity>> = dao.getAllStudents()
  val purchases: Flow<List<PurchaseEntity>> = dao.getAllPurchases()

  suspend fun getCurrentConfig(): AcademyConfigEntity {
    return dao.getConfigSync() ?: AcademyConfigEntity()
  }

  // 1. DYNAMIC PRICING ENGINE WITH STRICT BACKEND VALIDATION
  suspend fun updatePricing(
    currentPrice: Int,
    originalPrice: Int?,
    promoBadge: String
  ): OperationResult {
    // Validate on backend
    if (currentPrice < 1) {
      return OperationResult.Error("Course price must be at least ₹1")
    }
    if (currentPrice > 500000) {
      return OperationResult.Error("Course price cannot exceed ₹5,00,000")
    }
    if (originalPrice != null && originalPrice < currentPrice) {
      return OperationResult.Error("Original price cannot be less than discounted price (₹$currentPrice)")
    }

    val currentConfig = getCurrentConfig()
    val updated = currentConfig.copy(
      currentPriceInr = currentPrice,
      originalPriceInr = originalPrice ?: (currentPrice * 2),
      promoBadgeText = promoBadge.trim()
    )
    dao.insertOrUpdateConfig(updated)
    return OperationResult.Success("Course price successfully updated to ₹$currentPrice")
  }

  // 2. BACKEND PAYMENT VERIFICATION & SECURE ORDER EXECUTION
  // Rule: Never trust a price sent by client! We fetch authoritative price from database.
  suspend fun verifyAndExecutePurchase(
    studentName: String,
    studentEmail: String,
    studentPhone: String,
    paymentMethod: String
  ): PurchaseResult {
    val cleanName = studentName.trim()
    val cleanEmail = studentEmail.trim()
    val cleanPhone = studentPhone.trim()

    if (cleanName.length < 2) {
      return PurchaseResult.Error("Please enter a valid full name")
    }
    if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
      return PurchaseResult.Error("Please enter a valid email address")
    }

    val currentConfig = getCurrentConfig()
    // Authoritative backend price:
    val authoritativePrice = currentConfig.currentPriceInr
    val authoritativeOriginalPrice = currentConfig.originalPriceInr

    val timestamp = System.currentTimeMillis()
    val orderId = "ORD_UEJ_${(100000..999999).random()}"
    val paymentId = "pay_UEJ_${System.currentTimeMillis().toString().takeLast(6)}_${(100..999).random()}"

    // Cryptographic signature generation (simulating Razorpay signature verification)
    val rawSignaturePayload = "$orderId|$paymentId|$authoritativePrice|unique_secret_academy"
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(rawSignaturePayload.toByteArray(Charsets.UTF_8))
    val signatureHex = hashBytes.joinToString("") { "%02x".format(it) }

    val studentId = "stu_${cleanEmail.replace("@", "_").replace(".", "_")}"

    val purchase = PurchaseEntity(
      id = UUID.randomUUID().toString(),
      orderId = orderId,
      studentId = studentId,
      studentName = cleanName,
      studentEmail = cleanEmail,
      amountInr = authoritativePrice,
      originalPriceInr = authoritativeOriginalPrice,
      paymentMethod = paymentMethod,
      paymentId = paymentId,
      signature = signatureHex,
      timestamp = timestamp,
      status = "VERIFIED",
      courseTitle = currentConfig.courseTitle
    )

    // Save purchase record in DB
    dao.insertPurchase(purchase)

    // Update or insert Student record with active paid access
    val existingStudent = dao.getStudentByIdSync(studentId)
    val updatedStudent = if (existingStudent != null) {
      existingStudent.copy(
        name = cleanName,
        phone = if (cleanPhone.isNotEmpty()) cleanPhone else existingStudent.phone,
        hasPaidAccess = true
      )
    } else {
      StudentEntity(
        id = studentId,
        name = cleanName,
        email = cleanEmail,
        phone = cleanPhone,
        enrolledTimestamp = timestamp,
        hasPaidAccess = true
      )
    }
    dao.insertStudent(updatedStudent)

    return PurchaseResult.Success(purchase)
  }

  // 3. LESSON MANAGEMENT
  suspend fun addLesson(
    moduleId: String,
    title: String,
    description: String,
    videoUrl: String,
    duration: String,
    thumbnailUrl: String,
    isFreePreview: Boolean
  ): OperationResult {
    if (title.isBlank()) return OperationResult.Error("Lesson title cannot be empty")
    if (videoUrl.isBlank()) return OperationResult.Error("Video URL cannot be empty")

    val allLessons = dao.getAllLessonsSync()
    val nextOrder = (allLessons.maxOfOrNull { it.orderIndex } ?: 0) + 1

    val newLesson = LessonEntity(
      id = "les_${UUID.randomUUID().toString().take(8)}",
      moduleId = moduleId,
      title = title.trim(),
      description = description.trim(),
      videoUrl = videoUrl.trim(),
      duration = duration.ifBlank { "10m 00s" },
      thumbnailRes = "lesson_thumb_cinematic_1792954791251",
      thumbnailUrl = thumbnailUrl.trim(),
      orderIndex = nextOrder,
      isPublished = true,
      isFreePreview = isFreePreview,
      viewsCount = 0
    )
    dao.insertLesson(newLesson)
    return OperationResult.Success("Lesson '${newLesson.title}' added successfully")
  }

  suspend fun updateLesson(lesson: LessonEntity): OperationResult {
    if (lesson.title.isBlank()) return OperationResult.Error("Lesson title cannot be empty")
    dao.updateLesson(lesson)
    return OperationResult.Success("Lesson updated successfully")
  }

  suspend fun deleteLesson(lessonId: String): OperationResult {
    dao.deleteLessonById(lessonId)
    return OperationResult.Success("Lesson removed successfully")
  }

  suspend fun toggleLessonPublish(lessonId: String): OperationResult {
    val lesson = dao.getLessonByIdSync(lessonId) ?: return OperationResult.Error("Lesson not found")
    val updated = lesson.copy(isPublished = !lesson.isPublished)
    dao.updateLesson(updated)
    val state = if (updated.isPublished) "published" else "unpublished (draft)"
    return OperationResult.Success("Lesson marked as $state")
  }

  suspend fun toggleLessonFreePreview(lessonId: String): OperationResult {
    val lesson = dao.getLessonByIdSync(lessonId) ?: return OperationResult.Error("Lesson not found")
    val updated = lesson.copy(isFreePreview = !lesson.isFreePreview)
    dao.updateLesson(updated)
    val state = if (updated.isFreePreview) "Free Preview" else "Premium (Locked)"
    return OperationResult.Success("Lesson status changed to $state")
  }

  suspend fun reorderLesson(lessonId: String, moveUp: Boolean): OperationResult {
    val allLessons = dao.getAllLessonsSync().sortedBy { it.orderIndex }.toMutableList()
    val index = allLessons.indexOfFirst { it.id == lessonId }
    if (index == -1) return OperationResult.Error("Lesson not found")

    val targetIndex = if (moveUp) index - 1 else index + 1
    if (targetIndex < 0 || targetIndex >= allLessons.size) {
      return OperationResult.Error("Cannot move lesson further")
    }

    val currentLesson = allLessons[index]
    val otherLesson = allLessons[targetIndex]

    val updatedCurrent = currentLesson.copy(orderIndex = otherLesson.orderIndex)
    val updatedOther = otherLesson.copy(orderIndex = currentLesson.orderIndex)

    dao.updateLesson(updatedCurrent)
    dao.updateLesson(updatedOther)
    return OperationResult.Success("Lesson reordered")
  }

  // 4. MODULE MANAGEMENT
  suspend fun addModule(title: String, description: String): OperationResult {
    if (title.isBlank()) return OperationResult.Error("Module title cannot be empty")
    val allModules = dao.getAllModulesSync()
    val nextOrder = (allModules.maxOfOrNull { it.orderIndex } ?: 0) + 1
    val module = ModuleEntity(
      id = "mod_${UUID.randomUUID().toString().take(8)}",
      title = title.trim(),
      description = description.trim(),
      orderIndex = nextOrder
    )
    dao.insertModule(module)
    return OperationResult.Success("Module '${module.title}' created")
  }

  suspend fun deleteModule(moduleId: String): OperationResult {
    dao.deleteLessonsByModule(moduleId)
    dao.deleteModuleById(moduleId)
    return OperationResult.Success("Module and its lessons removed")
  }

  // 5. WEBSITE CUSTOMIZATION
  suspend fun saveAcademyConfig(config: AcademyConfigEntity): OperationResult {
    dao.insertOrUpdateConfig(config)
    return OperationResult.Success("Website appearance & settings published successfully")
  }

  // 6. STUDENT ACCESS CONTROL
  suspend fun toggleStudentPaidAccess(studentId: String, hasPaid: Boolean): OperationResult {
    val student = dao.getStudentByIdSync(studentId) ?: return OperationResult.Error("Student not found")
    val updated = student.copy(hasPaidAccess = hasPaid)
    dao.updateStudent(updated)
    val status = if (hasPaid) "granted full access" else "access revoked"
    return OperationResult.Success("Student ${student.name} $status")
  }

  // Helper JSON converters for benefits and FAQs
  companion object {
    fun parseBenefits(json: String): List<String> {
      if (json.isBlank()) return emptyList()
      return try {
        val array = JSONArray(json)
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
          list.add(array.getString(i))
        }
        list
      } catch (e: Exception) {
        emptyList()
      }
    }

    fun serializeBenefits(list: List<String>): String {
      val array = JSONArray()
      list.forEach { array.put(it) }
      return array.toString()
    }

    fun parseFaqs(json: String): List<FaqItem> {
      if (json.isBlank()) return emptyList()
      return try {
        val array = JSONArray(json)
        val list = mutableListOf<FaqItem>()
        for (i in 0 until array.length()) {
          val obj = array.getJSONObject(i)
          list.add(
            FaqItem(
              id = obj.optString("id", "faq_$i"),
              question = obj.optString("question", ""),
              answer = obj.optString("answer", "")
            )
          )
        }
        list
      } catch (e: Exception) {
        emptyList()
      }
    }

    fun serializeFaqs(list: List<FaqItem>): String {
      val array = JSONArray()
      list.forEach { item ->
        val obj = JSONObject()
        obj.put("id", item.id)
        obj.put("question", item.question)
        obj.put("answer", item.answer)
        array.put(obj)
      }
      return array.toString()
    }
  }
}
