package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.entity.AcademyConfigEntity
import com.example.data.repository.AcademyRepository
import com.example.data.repository.OperationResult
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Unique Editor", appName)
  }

  @Test
  fun `verify benefits parser and serializer`() {
    val benefits = listOf("45+ Lessons", "15GB Creator Pack", "Certificate")
    val json = AcademyRepository.serializeBenefits(benefits)
    val parsed = AcademyRepository.parseBenefits(json)
    assertEquals(benefits, parsed)
  }

  @Test
  fun `verify Course entity to and from Firestore DTO`() {
    val entity = com.example.data.entity.CourseEntity(
      id = "c_1",
      title = "CapCut Masterclass",
      subtitle = "Zero to Hero",
      description = "Full mobile guide",
      instructorName = "Unique_editor_ji",
      currentPriceInr = 999,
      originalPriceInr = 4999
    )
    val dto = com.example.data.remote.model.CourseFirestoreDto.fromEntity(entity)
    assertEquals("c_1", dto.id)
    assertEquals("CapCut Masterclass", dto.title)
    assertEquals(999L, dto.currentPriceInr)

    val restoredEntity = dto.toEntity("SYNCED")
    assertEquals(entity.id, restoredEntity.id)
    assertEquals(entity.title, restoredEntity.title)
    assertEquals(entity.currentPriceInr, restoredEntity.currentPriceInr)
    assertEquals("SYNCED", restoredEntity.syncStatus)
  }
}
