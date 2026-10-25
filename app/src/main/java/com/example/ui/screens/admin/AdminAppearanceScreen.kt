package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.AcademyConfigEntity
import com.example.data.entity.LessonEntity
import com.example.data.entity.ModuleEntity
import com.example.data.model.FaqItem
import com.example.data.repository.AcademyRepository
import com.example.ui.screens.student.CourseLandingScreen
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen

@Composable
fun AdminAppearanceScreen(
  config: AcademyConfigEntity,
  modules: List<ModuleEntity>,
  lessons: List<LessonEntity>,
  onSaveConfig: (AcademyConfigEntity) -> Unit
) {
  var activeTab by remember { mutableIntStateOf(0) } // 0 = Editor, 1 = Live Preview

  var academyName by remember(config.academyName) { mutableStateOf(config.academyName) }
  var courseTitle by remember(config.courseTitle) { mutableStateOf(config.courseTitle) }
  var heroHeading by remember(config.heroHeading) { mutableStateOf(config.heroHeading) }
  var heroSubtitle by remember(config.heroSubtitle) { mutableStateOf(config.heroSubtitle) }
  var heroBannerUrl by remember(config.heroBannerUrl) { mutableStateOf(config.heroBannerUrl) }
  var ctaText by remember(config.ctaButtonText) { mutableStateOf(config.ctaButtonText) }
  var instructorName by remember(config.instructorName) { mutableStateOf(config.instructorName) }
  var instructorBio by remember(config.instructorBio) { mutableStateOf(config.instructorBio) }
  var instagramHandle by remember(config.instagramHandle) { mutableStateOf(config.instagramHandle) }
  var instagramUrl by remember(config.instagramUrl) { mutableStateOf(config.instagramUrl) }
  var whatsappSupport by remember(config.whatsappSupport) { mutableStateOf(config.whatsappSupport) }
  var footerText by remember(config.footerText) { mutableStateOf(config.footerText) }

  // Benefits & FAQs lists
  var benefitsList by remember(config.benefitsJson) {
    mutableStateOf(AcademyRepository.parseBenefits(config.benefitsJson).toMutableList())
  }
  var faqsList by remember(config.faqsJson) {
    mutableStateOf(AcademyRepository.parseFaqs(config.faqsJson).toMutableList())
  }

  var newBenefitText by remember { mutableStateOf("") }
  var newFaqQuestion by remember { mutableStateOf("") }
  var newFaqAnswer by remember { mutableStateOf("") }

  // Construct current working draft
  val draftConfig = remember(
    academyName, courseTitle, heroHeading, heroSubtitle, heroBannerUrl,
    ctaText, instructorName, instructorBio, instagramHandle, instagramUrl,
    whatsappSupport, footerText, benefitsList, faqsList
  ) {
    config.copy(
      academyName = academyName,
      courseTitle = courseTitle,
      heroHeading = heroHeading,
      heroSubtitle = heroSubtitle,
      heroBannerUrl = heroBannerUrl,
      ctaButtonText = ctaText,
      instructorName = instructorName,
      instructorBio = instructorBio,
      instagramHandle = instagramHandle,
      instagramUrl = instagramUrl,
      whatsappSupport = whatsappSupport,
      footerText = footerText,
      benefitsJson = AcademyRepository.serializeBenefits(benefitsList),
      faqsJson = AcademyRepository.serializeFaqs(faqsList)
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .testTag("admin_appearance_screen")
  ) {
    // Mode Switcher Tabs (Editor vs Live Preview)
    TabRow(selectedTabIndex = activeTab) {
      Tab(
        selected = activeTab == 0,
        onClick = { activeTab = 0 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Settings Editor", fontWeight = FontWeight.SemiBold)
          }
        }
      )
      Tab(
        selected = activeTab == 1,
        onClick = { activeTab = 1 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Preview, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Live Preview", fontWeight = FontWeight.SemiBold)
          }
        }
      )
    }

    if (activeTab == 1) {
      // Live Preview Mode
      Box(modifier = Modifier.fillMaxSize()) {
        CourseLandingScreen(
          config = draftConfig,
          modules = modules,
          lessons = lessons,
          isEnrolled = false,
          onEnrollClick = {},
          onGoToLearning = {},
          onPlayLesson = {},
          onOpenInstallModal = {}
        )
      }
    } else {
      // Editor Form Mode
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Section: Academy & Course Branding
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "Branding & Typography",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
              value = academyName,
              onValueChange = { academyName = it },
              label = { Text("Academy / Website Name") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = courseTitle,
              onValueChange = { courseTitle = it },
              label = { Text("Course Title") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = heroHeading,
              onValueChange = { heroHeading = it },
              label = { Text("Homepage Hero Heading") },
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = heroSubtitle,
              onValueChange = { heroSubtitle = it },
              label = { Text("Hero Subtitle / Description") },
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = heroBannerUrl,
              onValueChange = { heroBannerUrl = it },
              label = { Text("Custom Hero Banner URL (Optional)") },
              placeholder = { Text("https://example.com/banner.jpg") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = ctaText,
              onValueChange = { ctaText = it },
              label = { Text("Call-To-Action Button Label") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }

        // Section: Instructor Profile & Socials
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "Instructor & Support Links",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
              value = instructorName,
              onValueChange = { instructorName = it },
              label = { Text("Instructor Name") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = instructorBio,
              onValueChange = { instructorBio = it },
              label = { Text("Instructor Bio") },
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = instagramHandle,
              onValueChange = { instagramHandle = it },
              label = { Text("Instagram Handle") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = instagramUrl,
              onValueChange = { instagramUrl = it },
              label = { Text("Instagram Profile URL") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = whatsappSupport,
              onValueChange = { whatsappSupport = it },
              label = { Text("WhatsApp Support Number") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = footerText,
              onValueChange = { footerText = it },
              label = { Text("Footer Copyright Text") },
              modifier = Modifier.fillMaxWidth()
            )
          }
        }

        // Section: Benefits Manager
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "Course Benefits (${benefitsList.size} items)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )

            benefitsList.forEachIndexed { idx, item ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                  .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = "• $item", fontSize = 12.sp, modifier = Modifier.weight(1f))
                IconButton(
                  onClick = {
                    val updated = benefitsList.toMutableList()
                    updated.removeAt(idx)
                    benefitsList = updated
                  },
                  modifier = Modifier.size(24.dp)
                ) {
                  Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed, modifier = Modifier.size(16.dp))
                }
              }
            }

            // Add benefit row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedTextField(
                value = newBenefitText,
                onValueChange = { newBenefitText = it },
                label = { Text("New Benefit") },
                placeholder = { Text("e.g. 50+ Premiere Keyboard Shortcuts PDF") },
                singleLine = true,
                modifier = Modifier.weight(1f)
              )
              Button(
                onClick = {
                  if (newBenefitText.isNotBlank()) {
                    val updated = benefitsList.toMutableList()
                    updated.add(newBenefitText.trim())
                    benefitsList = updated
                    newBenefitText = ""
                  }
                },
                enabled = newBenefitText.isNotBlank()
              ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
              }
            }
          }
        }

        // Section: FAQs Manager
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "Frequently Asked Questions (${faqsList.size} items)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )

            faqsList.forEachIndexed { idx, item ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                  .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(text = "Q: ${item.question}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                  Text(text = "A: ${item.answer}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(
                  onClick = {
                    val updated = faqsList.toMutableList()
                    updated.removeAt(idx)
                    faqsList = updated
                  },
                  modifier = Modifier.size(24.dp)
                ) {
                  Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed, modifier = Modifier.size(16.dp))
                }
              }
            }

            // Add FAQ Form
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                .padding(10.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(text = "Add New FAQ Item", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              OutlinedTextField(
                value = newFaqQuestion,
                onValueChange = { newFaqQuestion = it },
                label = { Text("Question") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
              OutlinedTextField(
                value = newFaqAnswer,
                onValueChange = { newFaqAnswer = it },
                label = { Text("Answer") },
                modifier = Modifier.fillMaxWidth()
              )
              Button(
                onClick = {
                  if (newFaqQuestion.isNotBlank() && newFaqAnswer.isNotBlank()) {
                    val updated = faqsList.toMutableList()
                    updated.add(FaqItem(id = "faq_${System.currentTimeMillis()}", question = newFaqQuestion.trim(), answer = newFaqAnswer.trim()))
                    faqsList = updated
                    newFaqQuestion = ""
                    newFaqAnswer = ""
                  }
                },
                enabled = newFaqQuestion.isNotBlank() && newFaqAnswer.isNotBlank()
              ) {
                Text("Add FAQ")
              }
            }
          }
        }

        // SAVE & PUBLISH ALL CHANGES
        Button(
          onClick = { onSaveConfig(draftConfig) },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("admin_save_appearance_button"),
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Publish Visual Changes to Website", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}
