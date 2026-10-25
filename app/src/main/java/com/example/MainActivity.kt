package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.AcademyConfigEntity
import com.example.ui.components.AdminPinDialog
import com.example.ui.components.InstallAppModal
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.StorageInfoDialog
import com.example.ui.components.TopAcademyBar
import com.example.ui.components.VideoPlayerModal
import com.example.ui.screens.admin.AdminAppearanceScreen
import com.example.ui.screens.admin.AdminLessonsScreen
import com.example.ui.screens.admin.AdminOverviewScreen
import com.example.ui.screens.admin.AdminPricingScreen
import com.example.ui.screens.admin.AdminStudentsPurchasesScreen
import com.example.ui.screens.admin.AdminThumbnailsScreen
import com.example.ui.screens.student.CheckoutScreen
import com.example.ui.screens.student.CourseLandingScreen
import com.example.ui.screens.student.MyLearningScreen
import com.example.ui.screens.student.StudentProfileScreen
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.AcademyViewModel
import com.example.ui.viewmodel.AdminNavTab
import com.example.ui.viewmodel.StudentNavTab

class MainActivity : ComponentActivity() {

  private val viewModel: AcademyViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme(darkTheme = true) {
        AcademyApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun AcademyApp(viewModel: AcademyViewModel) {
  val config by viewModel.config.collectAsStateWithLifecycle()
  val modules by viewModel.modules.collectAsStateWithLifecycle()
  val lessons by viewModel.lessons.collectAsStateWithLifecycle()
  val students by viewModel.students.collectAsStateWithLifecycle()
  val purchases by viewModel.purchases.collectAsStateWithLifecycle()

  val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()
  val studentTab by viewModel.studentTab.collectAsStateWithLifecycle()
  val adminTab by viewModel.adminTab.collectAsStateWithLifecycle()

  val studentName by viewModel.currentStudentName.collectAsStateWithLifecycle()
  val studentEmail by viewModel.currentStudentEmail.collectAsStateWithLifecycle()

  val activeLessonForPlayer by viewModel.activeLessonForPlayer.collectAsStateWithLifecycle()
  val showStorageInfoModal by viewModel.showStorageInfoModal.collectAsStateWithLifecycle()
  val lastSuccessfulPurchase by viewModel.lastSuccessfulPurchase.collectAsStateWithLifecycle()
  val checkoutLoading by viewModel.checkoutLoading.collectAsStateWithLifecycle()

  val isEnrolled = viewModel.isCurrentUserEnrolled()

  val snackbarHostState = remember { SnackbarHostState() }
  var showAdminPinDialog by remember { mutableStateOf(false) }
  var showInstallModal by remember { mutableStateOf(false) }

  // Listen for ViewModel UI snackbars
  LaunchedEffect(Unit) {
    viewModel.uiMessage.collect { uiMsg ->
      snackbarHostState.showSnackbar(uiMsg.message)
    }
  }

  // Handle device back press
  BackHandler(enabled = isAdminMode || studentTab != StudentNavTab.COURSE) {
    if (isAdminMode) {
      if (adminTab != AdminNavTab.OVERVIEW) {
        viewModel.setAdminTab(AdminNavTab.OVERVIEW)
      } else {
        viewModel.toggleAdminMode(false)
      }
    } else {
      viewModel.setStudentTab(StudentNavTab.COURSE)
    }
  }

  val activeConfig = config ?: AcademyConfigEntity()

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.safeDrawing),
    topBar = {
      TopAcademyBar(
        config = activeConfig,
        isAdminMode = isAdminMode,
        onToggleAdmin = {
          if (isAdminMode) {
            viewModel.toggleAdminMode(false)
          } else {
            showAdminPinDialog = true
          }
        },
        onOpenStorageGuide = {
          viewModel.setShowStorageInfo(true)
        },
        onOpenInstallModal = {
          showInstallModal = true
        }
      )
    },
    bottomBar = {
      if (isAdminMode) {
        // ADMIN STUDIO NAVIGATION BAR
        NavigationBar(
          modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("admin_navigation_bar"),
          tonalElevation = 6.dp
        ) {
          NavigationBarItem(
            selected = adminTab == AdminNavTab.OVERVIEW,
            onClick = { viewModel.setAdminTab(AdminNavTab.OVERVIEW) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Overview") },
            label = { Text("Overview", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentAmber, indicatorColor = AccentAmber.copy(alpha = 0.2f))
          )
          NavigationBarItem(
            selected = adminTab == AdminNavTab.PRICING,
            onClick = { viewModel.setAdminTab(AdminNavTab.PRICING) },
            icon = { Icon(Icons.Default.PriceChange, contentDescription = "Pricing") },
            label = { Text("Pricing", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentAmber, indicatorColor = AccentAmber.copy(alpha = 0.2f))
          )
          NavigationBarItem(
            selected = adminTab == AdminNavTab.LESSONS,
            onClick = { viewModel.setAdminTab(AdminNavTab.LESSONS) },
            icon = { Icon(Icons.Default.VideoLibrary, contentDescription = "Lessons") },
            label = { Text("Lessons", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentAmber, indicatorColor = AccentAmber.copy(alpha = 0.2f))
          )
          NavigationBarItem(
            selected = adminTab == AdminNavTab.THUMBNAILS,
            onClick = { viewModel.setAdminTab(AdminNavTab.THUMBNAILS) },
            icon = { Icon(Icons.Default.Image, contentDescription = "Thumbs") },
            label = { Text("Thumbs", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentAmber, indicatorColor = AccentAmber.copy(alpha = 0.2f))
          )
          NavigationBarItem(
            selected = adminTab == AdminNavTab.APPEARANCE,
            onClick = { viewModel.setAdminTab(AdminNavTab.APPEARANCE) },
            icon = { Icon(Icons.Default.ColorLens, contentDescription = "Branding") },
            label = { Text("Branding", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentAmber, indicatorColor = AccentAmber.copy(alpha = 0.2f))
          )
          NavigationBarItem(
            selected = adminTab == AdminNavTab.STUDENTS_PURCHASES,
            onClick = { viewModel.setAdminTab(AdminNavTab.STUDENTS_PURCHASES) },
            icon = { Icon(Icons.Default.People, contentDescription = "Students") },
            label = { Text("Ledger", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentAmber, indicatorColor = AccentAmber.copy(alpha = 0.2f))
          )
        }
      } else {
        // STUDENT PLATFORM NAVIGATION BAR
        NavigationBar(
          modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("student_navigation_bar"),
          tonalElevation = 6.dp
        ) {
          NavigationBarItem(
            selected = studentTab == StudentNavTab.COURSE,
            onClick = { viewModel.setStudentTab(StudentNavTab.COURSE) },
            icon = { Icon(Icons.Default.School, contentDescription = "Course") },
            label = { Text("Course", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryIndigo, indicatorColor = PrimaryIndigo.copy(alpha = 0.2f))
          )
          NavigationBarItem(
            selected = studentTab == StudentNavTab.MY_LEARNING,
            onClick = { viewModel.setStudentTab(StudentNavTab.MY_LEARNING) },
            icon = { Icon(Icons.Default.MenuBook, contentDescription = "My Learning") },
            label = { Text("My Learning", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryIndigo, indicatorColor = PrimaryIndigo.copy(alpha = 0.2f))
          )
          NavigationBarItem(
            selected = studentTab == StudentNavTab.CHECKOUT,
            onClick = { viewModel.setStudentTab(StudentNavTab.CHECKOUT) },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Enroll") },
            label = { Text("Enroll (₹${activeConfig.currentPriceInr})", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryIndigo, indicatorColor = PrimaryIndigo.copy(alpha = 0.2f))
          )
          NavigationBarItem(
            selected = studentTab == StudentNavTab.PROFILE,
            onClick = { viewModel.setStudentTab(StudentNavTab.PROFILE) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryIndigo, indicatorColor = PrimaryIndigo.copy(alpha = 0.2f))
          )
        }
      }
    },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (isAdminMode) {
        // ADMIN STUDIO VIEWS
        when (adminTab) {
          AdminNavTab.OVERVIEW -> {
            AdminOverviewScreen(
              config = activeConfig,
              lessons = lessons,
              students = students,
              purchases = purchases,
              onNavigateToPricing = { viewModel.setAdminTab(AdminNavTab.PRICING) },
              onNavigateToLessons = { viewModel.setAdminTab(AdminNavTab.LESSONS) },
              onNavigateToAppearance = { viewModel.setAdminTab(AdminNavTab.APPEARANCE) }
            )
          }
          AdminNavTab.PRICING -> {
            AdminPricingScreen(
              config = activeConfig,
              onSavePricing = { current, orig, badge ->
                viewModel.updatePricing(current, orig, badge)
              }
            )
          }
          AdminNavTab.LESSONS -> {
            AdminLessonsScreen(
              modules = modules,
              lessons = lessons,
              onAddModule = { title, desc -> viewModel.addModule(title, desc) },
              onAddLesson = { modId, title, desc, url, duration, thumb, isFree ->
                viewModel.addLesson(modId, title, desc, url, duration, thumb, isFree)
              },
              onUpdateLesson = { lesson -> viewModel.updateLesson(lesson) },
              onDeleteLesson = { id -> viewModel.deleteLesson(id) },
              onTogglePublish = { id -> viewModel.toggleLessonPublish(id) },
              onToggleFreePreview = { id -> viewModel.toggleLessonFreePreview(id) },
              onReorderLesson = { id, up -> viewModel.reorderLesson(id, up) },
              onPreviewLesson = { lesson -> viewModel.playLesson(lesson) },
              onOpenStorageGuide = { viewModel.setShowStorageInfo(true) }
            )
          }
          AdminNavTab.THUMBNAILS -> {
            AdminThumbnailsScreen(
              lessons = lessons,
              onUpdateLessonThumbnail = { lessonId, newUrl ->
                val lesson = lessons.find { it.id == lessonId }
                if (lesson != null) {
                  viewModel.updateLesson(lesson.copy(thumbnailUrl = newUrl))
                }
              }
            )
          }
          AdminNavTab.APPEARANCE -> {
            AdminAppearanceScreen(
              config = activeConfig,
              modules = modules,
              lessons = lessons,
              onSaveConfig = { updated ->
                viewModel.updateEditableConfig { updated }
                viewModel.saveAppearanceSettings()
              }
            )
          }
          AdminNavTab.STUDENTS_PURCHASES -> {
            AdminStudentsPurchasesScreen(
              students = students,
              purchases = purchases,
              onToggleStudentAccess = { id, hasAccess ->
                viewModel.toggleStudentAccess(id, hasAccess)
              }
            )
          }
        }
      } else {
        // STUDENT PLATFORM VIEWS
        when (studentTab) {
          StudentNavTab.COURSE -> {
            CourseLandingScreen(
              config = activeConfig,
              modules = modules,
              lessons = lessons,
              isEnrolled = isEnrolled,
              onEnrollClick = { viewModel.setStudentTab(StudentNavTab.CHECKOUT) },
              onGoToLearning = { viewModel.setStudentTab(StudentNavTab.MY_LEARNING) },
              onPlayLesson = { lesson -> viewModel.playLesson(lesson) },
              onOpenInstallModal = { showInstallModal = true }
            )
          }
          StudentNavTab.MY_LEARNING -> {
            MyLearningScreen(
              config = activeConfig,
              modules = modules,
              lessons = lessons,
              isEnrolled = isEnrolled,
              studentName = studentName,
              onGoToCheckout = { viewModel.setStudentTab(StudentNavTab.CHECKOUT) },
              onPlayLesson = { lesson -> viewModel.playLesson(lesson) }
            )
          }
          StudentNavTab.CHECKOUT -> {
            CheckoutScreen(
              config = activeConfig,
              studentName = studentName,
              studentEmail = studentEmail,
              isLoading = checkoutLoading,
              onPayNow = { name, email, phone, method ->
                viewModel.executeCheckout(name, email, phone, method)
              }
            )
          }
          StudentNavTab.PROFILE -> {
            StudentProfileScreen(
              config = activeConfig,
              studentName = studentName,
              studentEmail = studentEmail,
              isEnrolled = isEnrolled,
              purchases = purchases,
              onOpenInstallModal = { showInstallModal = true }
            )
          }
        }
      }
    }
  }

  // OVERLAY MODALS

  // 1. In-app Video Player Modal
  if (activeLessonForPlayer != null) {
    VideoPlayerModal(
      lesson = activeLessonForPlayer!!,
      onDismiss = { viewModel.closePlayer() }
    )
  }

  // 2. Storage & Bandwidth Cost Guide Modal
  if (showStorageInfoModal) {
    StorageInfoDialog(
      onDismiss = { viewModel.setShowStorageInfo(false) }
    )
  }

  // 3. Verified Payment Receipt Dialog
  if (lastSuccessfulPurchase != null) {
    ReceiptDialog(
      purchase = lastSuccessfulPurchase!!,
      onDismiss = { viewModel.dismissPurchaseReceipt() },
      onStartLearning = { viewModel.setStudentTab(StudentNavTab.MY_LEARNING) }
    )
  }

  // 4. Admin Security PIN Dialog
  if (showAdminPinDialog) {
    AdminPinDialog(
      adminEmail = "shivangitomar953@gmail.com",
      onDismiss = { showAdminPinDialog = false },
      onSuccess = {
        showAdminPinDialog = false
        viewModel.toggleAdminMode(true)
      }
    )
  }

  // 5. Install App Modal (Direct APK & Chrome Add to Home Screen PWA)
  if (showInstallModal) {
    InstallAppModal(
      onDismiss = { showInstallModal = false }
    )
  }
}
