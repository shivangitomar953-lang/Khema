package com.example.ui.screens.admin

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.entity.LessonEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen

@Composable
fun AdminThumbnailsScreen(
  lessons: List<LessonEntity>,
  onUpdateLessonThumbnail: (lessonId: String, newUrl: String) -> Unit
) {
  val context = LocalContext.current
  var selectedLessonForThumbnail by remember { mutableStateOf<LessonEntity?>(null) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
      .testTag("admin_thumbnails_screen")
  ) {
    Column {
      Text(
        text = "Thumbnail Management",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Every lesson has a custom or default thumbnail optimized for mobile loading.",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      modifier = Modifier.fillMaxSize(),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(lessons) { lesson ->
        val thumbResId = remember(lesson.thumbnailRes) {
          context.resources.getIdentifier(
            lesson.thumbnailRes,
            "drawable",
            context.packageName
          ).takeIf { it != 0 }
        }

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { selectedLessonForThumbnail = lesson },
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
            ) {
              if (lesson.thumbnailUrl.isNotBlank()) {
                AsyncImage(
                  model = lesson.thumbnailUrl,
                  contentDescription = lesson.title,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )
              } else if (thumbResId != null) {
                Image(
                  painter = painterResource(id = thumbResId),
                  contentDescription = lesson.title,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )
              }

              Box(
                modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .padding(6.dp)
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color.Black.copy(alpha = 0.7f))
                  .padding(horizontal = 4.dp, vertical = 2.dp)
              ) {
                Text(
                  text = lesson.duration,
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = lesson.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                maxLines = 2
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = if (lesson.thumbnailUrl.isNotBlank()) "Custom URL" else "Default Asset",
                  fontSize = 9.sp,
                  color = if (lesson.thumbnailUrl.isNotBlank()) AccentAmber else PrimaryIndigo,
                  fontWeight = FontWeight.Bold
                )

                Icon(
                  imageVector = Icons.Default.Edit,
                  contentDescription = "Edit Thumbnail",
                  tint = PrimaryIndigo,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    }
  }

  // DIALOG: EDIT LESSON THUMBNAIL
  if (selectedLessonForThumbnail != null) {
    val lesson = selectedLessonForThumbnail!!
    var customUrlInput by remember { mutableStateOf(lesson.thumbnailUrl) }

    AlertDialog(
      onDismissRequest = { selectedLessonForThumbnail = null },
      title = { Text("Change Lesson Thumbnail", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "Lesson: ${lesson.title}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          // Live Preview of Thumbnail
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .aspectRatio(16f / 9f)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.Black)
          ) {
            if (customUrlInput.isNotBlank()) {
              AsyncImage(
                model = customUrlInput,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
            } else {
              val defaultResId = context.resources.getIdentifier(
                lesson.thumbnailRes,
                "drawable",
                context.packageName
              )
              if (defaultResId != 0) {
                Image(
                  painter = painterResource(id = defaultResId),
                  contentDescription = null,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )
              }
            }
          }

          OutlinedTextField(
            value = customUrlInput,
            onValueChange = { customUrlInput = it },
            label = { Text("Custom Image URL (JPG/PNG/WebP)") },
            placeholder = { Text("https://example.com/thumbnail.jpg") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          // Preset thumbnails buttons
          Text(text = "Quick Presets:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            OutlinedButton(
              onClick = { customUrlInput = "" },
              modifier = Modifier.weight(1f)
            ) {
              Text("Default Asset", fontSize = 10.sp)
            }

            OutlinedButton(
              onClick = {
                customUrlInput = "https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?auto=format&fit=crop&w=800&q=80"
              },
              modifier = Modifier.weight(1f)
            ) {
              Text("Studio Set", fontSize = 10.sp)
            }

            OutlinedButton(
              onClick = {
                customUrlInput = "https://images.unsplash.com/photo-1536240478700-b869070f9279?auto=format&fit=crop&w=800&q=80"
              },
              modifier = Modifier.weight(1f)
            ) {
              Text("Color Wave", fontSize = 10.sp)
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateLessonThumbnail(lesson.id, customUrlInput.trim())
            selectedLessonForThumbnail = null
          }
        ) {
          Text("Apply Thumbnail")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { selectedLessonForThumbnail = null }) {
          Text("Cancel")
        }
      }
    )
  }
}
