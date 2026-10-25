package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.AcademyConfigEntity
import com.example.data.entity.LessonEntity
import com.example.data.entity.ModuleEntity
import com.example.data.entity.PurchaseEntity
import com.example.data.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademyDao {
  // Config
  @Query("SELECT * FROM academy_config WHERE id = 'singleton_config' LIMIT 1")
  fun getConfig(): Flow<AcademyConfigEntity?>

  @Query("SELECT * FROM academy_config WHERE id = 'singleton_config' LIMIT 1")
  suspend fun getConfigSync(): AcademyConfigEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateConfig(config: AcademyConfigEntity)

  // Modules
  @Query("SELECT * FROM modules ORDER BY orderIndex ASC")
  fun getAllModules(): Flow<List<ModuleEntity>>

  @Query("SELECT * FROM modules ORDER BY orderIndex ASC")
  suspend fun getAllModulesSync(): List<ModuleEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertModule(module: ModuleEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertModules(modules: List<ModuleEntity>)

  @Update
  suspend fun updateModule(module: ModuleEntity)

  @Query("DELETE FROM modules WHERE id = :moduleId")
  suspend fun deleteModuleById(moduleId: String)

  // Lessons
  @Query("SELECT * FROM lessons ORDER BY orderIndex ASC")
  fun getAllLessons(): Flow<List<LessonEntity>>

  @Query("SELECT * FROM lessons ORDER BY orderIndex ASC")
  suspend fun getAllLessonsSync(): List<LessonEntity>

  @Query("SELECT * FROM lessons WHERE moduleId = :moduleId ORDER BY orderIndex ASC")
  fun getLessonsByModule(moduleId: String): Flow<List<LessonEntity>>

  @Query("SELECT * FROM lessons WHERE id = :id LIMIT 1")
  fun getLessonById(id: String): Flow<LessonEntity?>

  @Query("SELECT * FROM lessons WHERE id = :id LIMIT 1")
  suspend fun getLessonByIdSync(id: String): LessonEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLesson(lesson: LessonEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLessons(lessons: List<LessonEntity>)

  @Update
  suspend fun updateLesson(lesson: LessonEntity)

  @Query("DELETE FROM lessons WHERE id = :lessonId")
  suspend fun deleteLessonById(lessonId: String)

  @Query("DELETE FROM lessons WHERE moduleId = :moduleId")
  suspend fun deleteLessonsByModule(moduleId: String)

  // Students
  @Query("SELECT * FROM students ORDER BY enrolledTimestamp DESC")
  fun getAllStudents(): Flow<List<StudentEntity>>

  @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
  fun getStudentById(id: String): Flow<StudentEntity?>

  @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
  suspend fun getStudentByIdSync(id: String): StudentEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStudent(student: StudentEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStudents(students: List<StudentEntity>)

  @Update
  suspend fun updateStudent(student: StudentEntity)

  // Purchases
  @Query("SELECT * FROM purchases ORDER BY timestamp DESC")
  fun getAllPurchases(): Flow<List<PurchaseEntity>>

  @Query("SELECT * FROM purchases WHERE studentId = :studentId ORDER BY timestamp DESC")
  fun getPurchasesForStudent(studentId: String): Flow<List<PurchaseEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPurchase(purchase: PurchaseEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPurchases(purchases: List<PurchaseEntity>)
}
