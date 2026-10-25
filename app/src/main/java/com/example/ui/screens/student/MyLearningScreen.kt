package com.example.ui.screens.student

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.entity.AcademyConfigEntity
import com.example.data.entity.LessonEntity
import com.example.data.entity.ModuleEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen

@Composable
fun MyLearningScreen(
  config: AcademyConfigEntity,
  modules: List<ModuleEntity>,
  lessons: List<LessonEntity>,
  isEnrolled: Boolean,
  studentName: String,
  onGoToCheckout: () -> Unit,
  onPlayLesson: (LessonEntity) -> Unit
) {
  val context = LocalContext.current
  var showCertificateModal by remember { mutableStateOf(false) }
  var downloadAssetSuccess by remember { mutableStateOf<String?>(null) }

  if (!isEnrolled) {
    // Locked State - prompt enrollment
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)
        .testTag("my_learning_locked_state"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(CircleShape)
          .background(AccentAmber.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = null,
          tint = AccentAmber,
          modifier = Modifier.size(36.dp)
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Course Locked",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Enroll in Unique_editor_ji's masterclass to unlock all ${lessons.size} lessons, project files, and the 15GB creator assets pack.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
      Spacer(modifier = Modifier.height(20.dp))
      Button(
        onClick = onGoToCheckout,
        modifier = Modifier
          .fillMaxWidth(0.85f)
          .height(48.dp)
          .testTag("unlock_course_now_button"),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Unlock for ₹${config.currentPriceInr}", fontWeight = FontWeight.Bold)
      }
    }
    return
  }

  // Enrolled Student Learning Dashboard
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("my_learning_enrolled_list"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(6.dp))
      // Welcome student banner
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Welcome back, $studentName",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Enrolled in ${config.courseTitle}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SuccessGreen.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "PAID ACCESS ACTIVE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = SuccessGreen
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Course Progress",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "2 of ${lessons.size} Lessons (25%)",
              fontSize = 12.sp,
              color = AccentAmber,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          LinearProgressIndicator(
            progress = { 0.25f },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = PrimaryIndigo,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
          )
        }
      }
    }

    // Bonus Downloads & Certificate Actions
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Creator Pack Download Button
        Card(
          modifier = Modifier
            .weight(1f)
            .clickable {
              downloadAssetSuccess = "15GB Creator Pack (LUTs, SFX & Presets) download link generated!"
            },
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "15GB Creator Pack", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = "Download LUTs & SFX", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }

        // Certificate Generator Button
        Card(
          modifier = Modifier
            .weight(1f)
            .clickable { showCertificateModal = true },
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = Icons.Default.School, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Course Certificate", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = "View & Download", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }

      if (downloadAssetSuccess != null) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = downloadAssetSuccess!!,
          fontSize = 11.sp,
          color = SuccessGreen,
          fontWeight = FontWeight.Medium
        )
      }
    }

    // Modules & Lessons List
    items(modules) { module ->
      val moduleLessons = lessons.filter { it.moduleId == module.id }.sortedBy { it.orderIndex }
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = module.title,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          moduleLessons.forEach { lesson ->
            val thumbResId = remember(lesson.thumbnailRes) {
              context.resources.getIdentifier(
                lesson.thumbnailRes,
                "drawable",
                context.packageName
              ).takeIf { it != 0 }
            }

            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onPlayLesson(lesson) },
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(width = 72.dp, height = 44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black)
                ) {
                  if (lesson.thumbnailUrl.isNotBlank()) {
                    AsyncImage(
                      model = lesson.thumbnailUrl,
                      contentDescription = null,
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxSize()
                    )
                  } else if (thumbResId != null) {
                    Image(
                      painter = painterResource(id = thumbResId),
                      contentDescription = null,
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxSize()
                    )
                  }

                  Box(
                    modifier = Modifier
                      .size(22.dp)
                      .clip(CircleShape)
                      .background(PrimaryIndigo)
                      .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.PlayArrow,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = lesson.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 2
                  )
                  Text(
                    text = "Duration: ${lesson.duration}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = "Watch",
                  tint = PrimaryIndigo,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Certificate Dialog
  if (showCertificateModal) {
    AlertDialog(
      onDismissRequest = { showCertificateModal = false },
      title = {
        Text("Verified Certificate of Excellence", fontWeight = FontWeight.Bold)
      },
      text = {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, AccentAmber, RoundedCornerShape(12.dp)),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(imageVector = Icons.Default.School, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "UNIQUE_EDITOR_JI ACADEMY", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AccentAmber)
            Text(text = "CERTIFICATE OF COMPLETION", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "This is proudly presented to", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = studentName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryIndigo)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "For successfully completing the Full-Stack Cinematic Video Editing & Reels Growth Blueprint.",
              fontSize = 10.sp,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Certificate ID: UEJ-CERT-${System.currentTimeMillis().toString().takeLast(6)}",
              fontSize = 9.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      },
      confirmButton = {
        Button(onClick = { showCertificateModal = false }) {
          Text("Done")
        }
      }
    )
  }
}
