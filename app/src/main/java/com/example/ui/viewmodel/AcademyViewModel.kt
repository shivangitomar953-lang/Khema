package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entity.AcademyConfigEntity
import com.example.data.entity.CourseEntity
import com.example.data.entity.LessonEntity
import com.example.data.entity.ModuleEntity
import com.example.data.entity.PurchaseEntity
import com.example.data.entity.StudentEntity
import com.example.data.model.FaqItem
import com.example.data.repository.AcademyRepository
import com.example.data.repository.CourseSyncRepository
import com.example.data.repository.OperationResult
import com.example.data.repository.PurchaseResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class StudentNavTab {
  COURSE,
  MY_LEARNING,
  CHECKOUT,
  PROFILE
}

enum class AdminNavTab {
  OVERVIEW,
  PRICING,
  LESSONS,
  THUMBNAILS,
  APPEARANCE,
  STUDENTS_PURCHASES
}

data class UiMessage(
  val message: String,
  val isError: Boolean = false
)

class AcademyViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: AcademyRepository
  val courseSyncRepository: CourseSyncRepository

  init {
    val db = AppDatabase.getDatabase(application, viewModelScope)
    repository = AcademyRepository(db.academyDao())
    courseSyncRepository = CourseSyncRepository(db.courseDao())
  }

  // Repository Flows
  val config: StateFlow<AcademyConfigEntity?> = repository.config.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    null
  )

  val courses: StateFlow<List<CourseEntity>> = courseSyncRepository.cachedCourses.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  val modules: StateFlow<List<ModuleEntity>> = repository.modules.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  val lessons: StateFlow<List<LessonEntity>> = repository.lessons.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  val students: StateFlow<List<StudentEntity>> = repository.students.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  val purchases: StateFlow<List<PurchaseEntity>> = repository.purchases.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  // Navigation & Role State
  private val _isAdminMode = MutableStateFlow(false)
  val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

  private val _studentTab = MutableStateFlow(StudentNavTab.COURSE)
  val studentTab: StateFlow<StudentNavTab> = _studentTab.asStateFlow()

  private val _adminTab = MutableStateFlow(AdminNavTab.OVERVIEW)
  val adminTab: StateFlow<AdminNavTab> = _adminTab.asStateFlow()

  // Active User / Student Session
  private val _currentStudentEmail = MutableStateFlow("shivangitomar953@gmail.com")
  val currentStudentEmail: StateFlow<String> = _currentStudentEmail.asStateFlow()

  private val _currentStudentName = MutableStateFlow("Shivangi Tomar")
  val currentStudentName: StateFlow<String> = _currentStudentName.asStateFlow()

  // In-app Video Player Active Lesson
  private val _activeLessonForPlayer = MutableStateFlow<LessonEntity?>(null)
  val activeLessonForPlayer: StateFlow<LessonEntity?> = _activeLessonForPlayer.asStateFlow()

  // Storage info modal
  private val _showStorageInfoModal = MutableStateFlow(false)
  val showStorageInfoModal: StateFlow<Boolean> = _showStorageInfoModal.asStateFlow()

  // Messages
  private val _uiMessage = MutableSharedFlow<UiMessage>()
  val uiMessage = _uiMessage.asSharedFlow()

  // Editing state for Admin Customizer
  private val _editableConfig = MutableStateFlow<AcademyConfigEntity?>(null)
  val editableConfig: StateFlow<AcademyConfigEntity?> = _editableConfig.asStateFlow()

  // Checkout State
  private val _checkoutLoading = MutableStateFlow(false)
  val checkoutLoading: StateFlow<Boolean> = _checkoutLoading.asStateFlow()

  private val _lastSuccessfulPurchase = MutableStateFlow<PurchaseEntity?>(null)
  val lastSuccessfulPurchase: StateFlow<PurchaseEntity?> = _lastSuccessfulPurchase.asStateFlow()

  // Check if current user has paid access
  fun isCurrentUserEnrolled(): Boolean {
    val email = _currentStudentEmail.value
    val student = students.value.find { it.email.equals(email, ignoreCase = true) }
    return student?.hasPaidAccess == true
  }

  fun setStudentTab(tab: StudentNavTab) {
    _studentTab.value = tab
  }

  fun setAdminTab(tab: AdminNavTab) {
    _adminTab.value = tab
  }

  fun toggleAdminMode(enable: Boolean) {
    _isAdminMode.value = enable
    if (enable) {
      // Initialize editable config from current config
      _editableConfig.value = config.value ?: AcademyConfigEntity()
    }
  }

  fun playLesson(lesson: LessonEntity) {
    _activeLessonForPlayer.value = lesson
  }

  fun closePlayer() {
    _activeLessonForPlayer.value = null
  }

  fun setShowStorageInfo(show: Boolean) {
    _showStorageInfoModal.value = show
  }

  fun updateEditableConfig(updater: (AcademyConfigEntity) -> AcademyConfigEntity) {
    val current = _editableConfig.value ?: config.value ?: AcademyConfigEntity()
    _editableConfig.value = updater(current)
  }

  fun saveAppearanceSettings() {
    val current = _editableConfig.value ?: return
    viewModelScope.launch {
      val res = repository.saveAcademyConfig(current)
      handleResult(res)
    }
  }

  fun updatePricing(currentPrice: Int, originalPrice: Int?, promoBadge: String) {
    viewModelScope.launch {
      val res = repository.updatePricing(currentPrice, originalPrice, promoBadge)
      handleResult(res)
    }
  }

  fun executeCheckout(
    studentName: String,
    studentEmail: String,
    studentPhone: String,
    paymentMethod: String
  ) {
    viewModelScope.launch {
      _checkoutLoading.value = true
      _currentStudentEmail.value = studentEmail
      _currentStudentName.value = studentName

      val result = repository.verifyAndExecutePurchase(
        studentName = studentName,
        studentEmail = studentEmail,
        studentPhone = studentPhone,
        paymentMethod = paymentMethod
      )
      _checkoutLoading.value = false
      when (result) {
        is PurchaseResult.Success -> {
          _lastSuccessfulPurchase.value = result.purchase
          _uiMessage.emit(UiMessage("Payment Verified! Course Unlocked Successfully for ₹${result.purchase.amountInr}"))
          _studentTab.value = StudentNavTab.MY_LEARNING
        }
        is PurchaseResult.Error -> {
          _uiMessage.emit(UiMessage(result.message, isError = true))
        }
      }
    }
  }

  fun dismissPurchaseReceipt() {
    _lastSuccessfulPurchase.value = null
  }

  // Lesson Management
  fun addLesson(
    moduleId: String,
    title: String,
    description: String,
    videoUrl: String,
    duration: String,
    thumbnailUrl: String,
    isFreePreview: Boolean
  ) {
    viewModelScope.launch {
      val res = repository.addLesson(
        moduleId = moduleId,
        title = title,
        description = description,
        videoUrl = videoUrl,
        duration = duration,
        thumbnailUrl = thumbnailUrl,
        isFreePreview = isFreePreview
      )
      handleResult(res)
    }
  }

  fun syncCoursesWithRemote() {
    viewModelScope.launch {
      val result = courseSyncRepository.syncFromRemote()
      result.onSuccess { count ->
        _uiMessage.emit(UiMessage("Successfully synchronized $count courses with remote Firestore"))
      }.onFailure { error ->
        _uiMessage.emit(UiMessage("Sync info: ${error.message ?: "Local cache active"}", isError = false))
      }
    }
  }

  fun saveCourse(course: CourseEntity, syncToRemote: Boolean = true) {
    viewModelScope.launch {
      val res = courseSyncRepository.saveCourse(course, syncToRemote)
      res.onSuccess {
        _uiMessage.emit(UiMessage("Course '${it.title}' saved to local cache"))
      }.onFailure {
        _uiMessage.emit(UiMessage(it.message ?: "Failed to save course", isError = true))
      }
    }
  }

  fun updateLesson(lesson: LessonEntity) {
    viewModelScope.launch {
      val res = repository.updateLesson(lesson)
      handleResult(res)
    }
  }

  fun deleteLesson(lessonId: String) {
    viewModelScope.launch {
      val res = repository.deleteLesson(lessonId)
      handleResult(res)
    }
  }

  fun toggleLessonPublish(lessonId: String) {
    viewModelScope.launch {
      val res = repository.toggleLessonPublish(lessonId)
      handleResult(res)
    }
  }

  fun toggleLessonFreePreview(lessonId: String) {
    viewModelScope.launch {
      val res = repository.toggleLessonFreePreview(lessonId)
      handleResult(res)
    }
  }

  fun reorderLesson(lessonId: String, moveUp: Boolean) {
    viewModelScope.launch {
      val res = repository.reorderLesson(lessonId, moveUp)
      handleResult(res)
    }
  }

  // Module Management
  fun addModule(title: String, description: String) {
    viewModelScope.launch {
      val res = repository.addModule(title, description)
      handleResult(res)
    }
  }

  fun deleteModule(moduleId: String) {
    viewModelScope.launch {
      val res = repository.deleteModule(moduleId)
      handleResult(res)
    }
  }

  // Student Access
  fun toggleStudentAccess(studentId: String, hasPaid: Boolean) {
    viewModelScope.launch {
      val res = repository.toggleStudentPaidAccess(studentId, hasPaid)
      handleResult(res)
    }
  }

  private suspend fun handleResult(res: OperationResult) {
    when (res) {
      is OperationResult.Success -> _uiMessage.emit(UiMessage(res.message, isError = false))
      is OperationResult.Error -> _uiMessage.emit(UiMessage(res.error, isError = true))
    }
  }
}
