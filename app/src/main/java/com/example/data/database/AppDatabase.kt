package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AcademyDao
import com.example.data.dao.CourseDao
import com.example.data.entity.AcademyConfigEntity
import com.example.data.entity.CourseEntity
import com.example.data.entity.LessonEntity
import com.example.data.entity.ModuleEntity
import com.example.data.entity.PurchaseEntity
import com.example.data.entity.StudentEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@Database(
  entities = [
    AcademyConfigEntity::class,
    CourseEntity::class,
    ModuleEntity::class,
    LessonEntity::class,
    StudentEntity::class,
    PurchaseEntity::class
  ],
  version = 2,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun academyDao(): AcademyDao
  abstract fun courseDao(): CourseDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "unique_editor_academy.db"
        )
          .fallbackToDestructiveMigration()
          .addCallback(AppDatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class AppDatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.academyDao(), database.courseDao())
          }
        }
      }

      private suspend fun populateInitialData(dao: AcademyDao, courseDao: CourseDao) {
        // Seed Masterclass Course
        val defaultCourse = CourseEntity(
          id = "course_masterclass_01",
          title = "Full-Stack Cinematic Video Editing & Reels Growth Blueprint",
          subtitle = "Master Mobile & PC Editing: CapCut, Premiere Pro, After Effects, DaVinci Resolve & Reels Algorithm",
          description = "Zero to Hero complete editing mastery with 45+ HD lessons, 100+ sound effects, premium cinematic LUTs, and viral retention pacing.",
          instructorName = "Unique_editor_ji",
          currentPriceInr = 999,
          originalPriceInr = 4999,
          promoBadgeText = "80% DISCOUNT • LIMITED TIME BATCH",
          bannerUrl = "",
          isPublished = true,
          totalLessonsCount = 8,
          totalDuration = "2h 45m",
          syncStatus = "LOCAL_ONLY"
        )
        courseDao.insertCourse(defaultCourse)

        // Initial Benefits JSON
        val benefits = listOf(
          "45+ In-Depth 4K Lessons covering Mobile & Desktop Editing",
          "Exclusive 15GB+ Creator Pack: Cinematic LUTs, SFX & Motion Overlays",
          "Viral Instagram Reels & YouTube Shorts Hook Psychology",
          "CapCut Pro, Adobe Premiere Pro & DaVinci Resolve Workflows",
          "Private Telegram/WhatsApp Community with Weekly Q&A",
          "Official Verified Course Completion Certificate"
        )
        val benefitsArray = JSONArray()
        benefits.forEach { benefitsArray.put(it) }

        // Initial FAQs JSON
        val faqs = listOf(
          Pair("Can I learn editing if I only have a smartphone?", "Yes! Over 60% of this course is dedicated to mobile editing in CapCut & VN with pro-level color grading and keyframing."),
          Pair("Is there a refund policy or money-back guarantee?", "We offer a 7-day unconditional satisfaction guarantee. If you don't level up your editing, get 100% of your money back."),
          Pair("How long do I have access to the lessons?", "You get lifetime access! All future lesson additions, project files, and preset updates are included at no extra cost."),
          Pair("How do I get my verified certificate?", "Upon completing all video modules and submitting your capstone editing project, your verified certificate is unlocked instantly."),
          Pair("What payment methods are supported?", "We support all major Indian UPI apps (Google Pay, PhonePe, Paytm, BHIM), NetBanking, and Debit/Credit Cards.")
        )
        val faqsArray = JSONArray()
        faqs.forEachIndexed { idx, pair ->
          val obj = JSONObject()
          obj.put("id", "faq_$idx")
          obj.put("question", pair.first)
          obj.put("answer", pair.second)
          faqsArray.put(obj)
        }

        // Insert initial config
        val initialConfig = AcademyConfigEntity(
          id = "singleton_config",
          academyName = "Unique Editor Ji Academy",
          logoUrl = "",
          heroHeading = "Master High-Ticket Video Editing & Viral Content",
          heroSubtitle = "The ultimate blueprint to go from beginner to a pro editor earning ₹50,000+ monthly. Learn CapCut, Premiere Pro, Sound Design, and Client Pitching.",
          heroBannerRes = "academy_hero_banner_1792954771092",
          heroBannerUrl = "",
          primaryColorHex = "#6366F1",
          accentColorHex = "#F59E0B",
          ctaButtonText = "Enroll in Masterclass",
          ctaButtonColorHex = "#6366F1",
          courseTitle = "Full-Stack Cinematic Video Editing & Reels Growth Blueprint",
          courseDescription = "Step-by-step masterclass with 45+ HD lessons, 100+ sound effects, premium cinematic LUTs, transitions presets, and proven viral retention pacing.",
          instructorName = "Unique_editor_ji",
          instructorBio = "Founder of Unique_editor_ji with 500K+ followers across Instagram & YouTube. Trained 12,000+ creative editors worldwide.",
          instructorAvatarRes = "instructor_profile_1792954806015",
          instructorAvatarUrl = "",
          instagramHandle = "@unique_editor_ji",
          instagramUrl = "https://instagram.com/unique_editor_ji",
          whatsappSupport = "+91 98765 43210",
          supportEmail = "shivangitomar953@gmail.com",
          footerText = "© 2026 Unique_editor_ji Academy. Empowering creators and editors worldwide.",
          benefitsJson = benefitsArray.toString(),
          faqsJson = faqsArray.toString(),
          currentPriceInr = 999,
          originalPriceInr = 4999,
          promoBadgeText = "80% DISCOUNT • LIMITED TIME BATCH",
          isRegistrationOpen = true
        )
        dao.insertOrUpdateConfig(initialConfig)

        // Seed Modules
        val modules = listOf(
          ModuleEntity("mod_1", "Module 1: Foundations & Mobile Editing Mastery", "Master UI, cuts, pacing, speed ramping, and CapCut secret tools.", 1),
          ModuleEntity("mod_2", "Module 2: Desktop Powerhouse (Premiere Pro & DaVinci)", "Workspaces, timeline hygiene, Lumetri color science, and dynamic linking.", 2),
          ModuleEntity("mod_3", "Module 3: Cinematic Sound Design & Foley FX", "Layering ambience, whooshes, risers, bass drops, and voice audio cleaning.", 3),
          ModuleEntity("mod_4", "Module 4: Viral Retention & Reels Algorithm Secrets", "The 3-second hook formula, kinetic text typography, and B-roll layering.", 4),
          ModuleEntity("mod_5", "Module 5: Monetization & High-Ticket Client Acquisition", "Building an irresistible portfolio, international client pitching, and contracts.", 5)
        )
        dao.insertModules(modules)

        // Seed Lessons (with first lesson as free preview!)
        val lessons = listOf(
          LessonEntity(
            id = "les_101",
            moduleId = "mod_1",
            title = "Welcome & Video Editing Career Roadmap (2026)",
            description = "Discover the entire roadmap, tools required, and how to reach the top 1% of editors within 90 days.",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            duration = "14m 20s",
            thumbnailRes = "lesson_thumb_cinematic_1792954791251",
            orderIndex = 1,
            isPublished = true,
            isFreePreview = true,
            viewsCount = 1840
          ),
          LessonEntity(
            id = "les_102",
            moduleId = "mod_1",
            title = "Mobile Editing Masterclass: Advanced CapCut & VN",
            description = "Frame-accurate trimming, compound clips, keyframe easing, and seamless masked zoom transitions on phone.",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            duration = "22m 15s",
            thumbnailRes = "lesson_thumb_cinematic_1792954791251",
            orderIndex = 2,
            isPublished = true,
            isFreePreview = true,
            viewsCount = 1290
          ),
          LessonEntity(
            id = "les_103",
            moduleId = "mod_1",
            title = "Seamless Speed Ramping & Smooth Slow Motion",
            description = "Custom speed curve graphs, optical flow frame interpolation, and sync to beat drops.",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            duration = "18m 40s",
            thumbnailRes = "lesson_thumb_cinematic_1792954791251",
            orderIndex = 3,
            isPublished = true,
            isFreePreview = false,
            viewsCount = 940
          ),
          LessonEntity(
            id = "les_201",
            moduleId = "mod_2",
            title = "Premiere Pro Interface Setup & Fast Keyboard Shortcuts",
            description = "Optimizing your timeline for 10x faster playback and zero lag with custom proxy workflows.",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            duration = "26m 10s",
            thumbnailRes = "lesson_thumb_cinematic_1792954791251",
            orderIndex = 4,
            isPublished = true,
            isFreePreview = false,
            viewsCount = 820
          ),
          LessonEntity(
            id = "les_202",
            moduleId = "mod_2",
            title = "Hollywood Color Grading: DaVinci Nodes & Custom LUTs",
            description = "Color balance, skin tone qualifier, teal-and-orange grading, and applying the included 15GB LUT pack.",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            duration = "31m 05s",
            thumbnailRes = "lesson_thumb_cinematic_1792954791251",
            orderIndex = 5,
            isPublished = true,
            isFreePreview = false,
            viewsCount = 780
          ),
          LessonEntity(
            id = "les_301",
            moduleId = "mod_3",
            title = "Sound Design Secrets: Making Flat Videos Come Alive",
            description = "Subtle whooshes, risers, cinematic cinematic impacts, and audio ducking behind creator speech.",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
            duration = "19m 50s",
            thumbnailRes = "lesson_thumb_cinematic_1792954791251",
            orderIndex = 6,
            isPublished = true,
            isFreePreview = false,
            viewsCount = 610
          ),
          LessonEntity(
            id = "les_401",
            moduleId = "mod_4",
            title = "The 3-Second Viral Hook Architecture for Reels",
            description = "Visual pattern interrupts, split screens, dynamic pop-up graphics, and retaining 85%+ audience graph.",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
            duration = "24m 30s",
            thumbnailRes = "lesson_thumb_cinematic_1792954791251",
            orderIndex = 7,
            isPublished = true,
            isFreePreview = false,
            viewsCount = 590
          ),
          LessonEntity(
            id = "les_501",
            moduleId = "mod_5",
            title = "Landing $500–$2000 International Video Editing Clients",
            description = "Cold outreach email scripts, Instagram DM pitching, creating your Notion portfolio, and contract templates.",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            duration = "28m 10s",
            thumbnailRes = "lesson_thumb_cinematic_1792954791251",
            orderIndex = 8,
            isPublished = true,
            isFreePreview = false,
            viewsCount = 510
          )
        )
        dao.insertLessons(lessons)

        // Seed Sample Students
        val now = System.currentTimeMillis()
        val dayMs = 86400000L
        val students = listOf(
          StudentEntity("stu_1", "Aarav Sharma", "aarav.video@gmail.com", "+91 98112 34567", "", now - dayMs * 12, true),
          StudentEntity("stu_2", "Rohan Mehta", "rohan.creatives@gmail.com", "+91 97234 56789", "", now - dayMs * 8, true),
          StudentEntity("stu_3", "Ananya Verma", "ananya.vlog@gmail.com", "+91 99345 67890", "", now - dayMs * 5, true),
          StudentEntity("stu_4", "Kavya Patel", "kavya.edits@gmail.com", "+91 96456 78901", "", now - dayMs * 2, true),
          StudentEntity("stu_5", "Sameer Khan", "sameer.shorts@gmail.com", "+91 95567 89012", "", now - dayMs * 1, false)
        )
        dao.insertStudents(students)

        // Seed Verified Purchases
        val purchases = listOf(
          PurchaseEntity(
            id = "pur_101",
            orderId = "ORD_UEJ_908124",
            studentId = "stu_1",
            studentName = "Aarav Sharma",
            studentEmail = "aarav.video@gmail.com",
            amountInr = 999,
            originalPriceInr = 4999,
            paymentMethod = "UPI (Google Pay)",
            paymentId = "pay_UEJ_gpay_981247",
            signature = "sig_sha256_e8a49c0d2f1b8a9c3d4e5f6a",
            timestamp = now - dayMs * 12,
            status = "VERIFIED",
            courseTitle = "Full-Stack Video Editing & Viral Content Blueprint"
          ),
          PurchaseEntity(
            id = "pur_102",
            orderId = "ORD_UEJ_908235",
            studentId = "stu_2",
            studentName = "Rohan Mehta",
            studentEmail = "rohan.creatives@gmail.com",
            amountInr = 999,
            originalPriceInr = 4999,
            paymentMethod = "UPI (PhonePe)",
            paymentId = "pay_UEJ_phonepe_334512",
            signature = "sig_sha256_b3c2d1e0f9a8b7c6d5e4f3a2",
            timestamp = now - dayMs * 8,
            status = "VERIFIED",
            courseTitle = "Full-Stack Video Editing & Viral Content Blueprint"
          ),
          PurchaseEntity(
            id = "pur_103",
            orderId = "ORD_UEJ_908389",
            studentId = "stu_3",
            studentName = "Ananya Verma",
            studentEmail = "ananya.vlog@gmail.com",
            amountInr = 1499,
            originalPriceInr = 4999,
            paymentMethod = "Credit Card (HDFC)",
            paymentId = "pay_UEJ_card_881920",
            signature = "sig_sha256_a1b2c3d4e5f60718293a4b5c",
            timestamp = now - dayMs * 5,
            status = "VERIFIED",
            courseTitle = "Full-Stack Video Editing & Viral Content Blueprint"
          ),
          PurchaseEntity(
            id = "pur_104",
            orderId = "ORD_UEJ_908492",
            studentId = "stu_4",
            studentName = "Kavya Patel",
            studentEmail = "kavya.edits@gmail.com",
            amountInr = 999,
            originalPriceInr = 4999,
            paymentMethod = "UPI (Paytm)",
            paymentId = "pay_UEJ_paytm_551923",
            signature = "sig_sha256_7f8e9d0c1b2a3f4e5d6c7b8a",
            timestamp = now - dayMs * 2,
            status = "VERIFIED",
            courseTitle = "Full-Stack Video Editing & Viral Content Blueprint"
          )
        )
        dao.insertPurchases(purchases)
      }
    }
  }
}
