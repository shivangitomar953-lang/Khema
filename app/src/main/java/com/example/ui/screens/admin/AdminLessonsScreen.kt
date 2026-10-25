package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.entity.LessonEntity
import com.example.data.entity.ModuleEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLessonsScreen(
  modules: List<ModuleEntity>,
  lessons: List<LessonEntity>,
  onAddModule: (title: String, desc: String) -> Unit,
  onAddLesson: (
    moduleId: String,
    title: String,
    desc: String,
    videoUrl: String,
    duration: String,
    thumbUrl: String,
    isFree: Boolean
  ) -> Unit,
  onUpdateLesson: (LessonEntity) -> Unit,
  onDeleteLesson: (lessonId: String) -> Unit,
  onTogglePublish: (lessonId: String) -> Unit,
  onToggleFreePreview: (lessonId: String) -> Unit,
  onReorderLesson: (lessonId: String, moveUp: Boolean) -> Unit,
  onPreviewLesson: (LessonEntity) -> Unit,
  onOpenStorageGuide: () -> Unit
) {
  val context = LocalContext.current
  var selectedModuleIndex by remember { mutableIntStateOf(0) }
  var showAddLessonDialog by remember { mutableStateOf(false) }
  var showAddModuleDialog by remember { mutableStateOf(false) }
  var lessonToEdit by remember { mutableStateOf<LessonEntity?>(null) }
  var lessonToDelete by remember { mutableStateOf<LessonEntity?>(null) }

  val activeModule = modules.getOrNull(selectedModuleIndex)
  val displayedLessons = if (activeModule != null) {
    lessons.filter { it.moduleId == activeModule.id }.sortedBy { it.orderIndex }
  } else {
    lessons.sortedBy { it.orderIndex }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .testTag("admin_lessons_screen")
  ) {
    // Header & Actions
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Video & Lesson Management",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${lessons.size} total lessons • ${modules.size} curriculum modules",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        IconButton(
          onClick = onOpenStorageGuide,
          modifier = Modifier.testTag("admin_storage_guide_btn")
        ) {
          Icon(
            imageVector = Icons.Default.HelpOutline,
            contentDescription = "Storage Limits & Costs Guide",
            tint = AccentAmber
          )
        }
      }

      // Action Buttons (Add Module & Add Lesson)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = { showAddLessonDialog = true },
          modifier = Modifier
            .weight(1f)
            .testTag("admin_add_lesson_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Lesson", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = { showAddModuleDialog = true },
          modifier = Modifier
            .weight(1f)
            .testTag("admin_add_module_btn"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Module", fontSize = 12.sp)
        }
      }
    }

    // Module Selector Scrollable Tabs
    if (modules.isNotEmpty()) {
      ScrollableTabRow(
        selectedTabIndex = selectedModuleIndex.coerceIn(0, modules.size - 1),
        edgePadding = 16.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        modules.forEachIndexed { index, mod ->
          Tab(
            selected = selectedModuleIndex == index,
            onClick = { selectedModuleIndex = index },
            text = {
              Text(
                text = "Mod ${index + 1}: ${mod.title.take(24)}",
                fontSize = 12.sp,
                fontWeight = if (selectedModuleIndex == index) FontWeight.Bold else FontWeight.Normal
              )
            }
          )
        }
      }
    }

    // Lessons list for active module
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      if (displayedLessons.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(text = "No lessons in this module yet.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Spacer(modifier = Modifier.height(8.dp))
              Button(onClick = { showAddLessonDialog = true }) {
                Text("Add First Lesson")
              }
            }
          }
        }
      }

      itemsIndexed(displayedLessons) { index, lesson ->
        val thumbResId = remember(lesson.thumbnailRes) {
          context.resources.getIdentifier(
            lesson.thumbnailRes,
            "drawable",
            context.packageName
          ).takeIf { it != 0 }
        }

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Reorder controls
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
              ) {
                IconButton(
                  onClick = { onReorderLesson(lesson.id, true) },
                  enabled = index > 0,
                  modifier = Modifier.size(28.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Move Up",
                    tint = if (index > 0) PrimaryIndigo else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    modifier = Modifier.size(16.dp)
                  )
                }
                Text(
                  text = "#${lesson.orderIndex}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(
                  onClick = { onReorderLesson(lesson.id, false) },
                  enabled = index < displayedLessons.size - 1,
                  modifier = Modifier.size(28.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Move Down",
                    tint = if (index < displayedLessons.size - 1) PrimaryIndigo else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    modifier = Modifier.size(16.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.width(6.dp))

              // Thumbnail
              Box(
                modifier = Modifier
                  .size(width = 84.dp, height = 52.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color.Black)
                  .clickable { onPreviewLesson(lesson) }
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
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(PrimaryIndigo.copy(alpha = 0.8f))
                    .align(Alignment.Center),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.width(10.dp))

              // Title and details
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = lesson.title,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  maxLines = 2
                )
                Text(
                  text = "Duration: ${lesson.duration}",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (lesson.isPublished) SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = if (lesson.isPublished) "PUBLISHED" else "DRAFT",
                      color = if (lesson.isPublished) SuccessGreen else ErrorRed,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                  Spacer(modifier = Modifier.width(6.dp))
                  if (lesson.isFreePreview) {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AccentAmber.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = "FREE PREVIEW",
                        color = AccentAmber,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              }

              // Edit and Delete icons
              Column {
                IconButton(onClick = { lessonToEdit = lesson }, modifier = Modifier.size(32.dp)) {
                  Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryIndigo, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = { lessonToDelete = lesson }, modifier = Modifier.size(32.dp)) {
                  Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed, modifier = Modifier.size(16.dp))
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick toggles row (Published status & Free preview toggle)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Publish", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(6.dp))
                Switch(
                  checked = lesson.isPublished,
                  onCheckedChange = { onTogglePublish(lesson.id) },
                  colors = SwitchDefaults.colors(checkedThumbColor = SuccessGreen)
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Free Preview", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(6.dp))
                Switch(
                  checked = lesson.isFreePreview,
                  onCheckedChange = { onToggleFreePreview(lesson.id) },
                  colors = SwitchDefaults.colors(checkedThumbColor = AccentAmber)
                )
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  // DIALOG: ADD LESSON
  if (showAddLessonDialog) {
    LessonFormDialog(
      modules = modules,
      initialModuleId = activeModule?.id ?: modules.firstOrNull()?.id ?: "mod_1",
      initialLesson = null,
      onDismiss = { showAddLessonDialog = false },
      onSave = { modId, title, desc, url, duration, thumb, isFree ->
        onAddLesson(modId, title, desc, url, duration, thumb, isFree)
        showAddLessonDialog = false
      }
    )
  }

  // DIALOG: EDIT LESSON
  if (lessonToEdit != null) {
    LessonFormDialog(
      modules = modules,
      initialModuleId = lessonToEdit!!.moduleId,
      initialLesson = lessonToEdit,
      onDismiss = { lessonToEdit = null },
      onSave = { modId, title, desc, url, duration, thumb, isFree ->
        val updated = lessonToEdit!!.copy(
          moduleId = modId,
          title = title,
          description = desc,
          videoUrl = url,
          duration = duration,
          thumbnailUrl = thumb,
          isFreePreview = isFree
        )
        onUpdateLesson(updated)
        lessonToEdit = null
      }
    )
  }

  // DIALOG: ADD MODULE
  if (showAddModuleDialog) {
    var modTitle by remember { mutableStateOf("") }
    var modDesc by remember { mutableStateOf("") }
    AlertDialog(
      onDismissRequest = { showAddModuleDialog = false },
      title = { Text("Create New Curriculum Module", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = modTitle,
            onValueChange = { modTitle = it },
            label = { Text("Module Title") },
            placeholder = { Text("e.g. Module 6: After Effects VFX") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = modDesc,
            onValueChange = { modDesc = it },
            label = { Text("Short Description") },
            placeholder = { Text("Summary of topics covered in this module") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (modTitle.isNotBlank()) {
              onAddModule(modTitle, modDesc)
              showAddModuleDialog = false
            }
          },
          enabled = modTitle.isNotBlank()
        ) {
          Text("Create Module")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showAddModuleDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // DIALOG: CONFIRM DELETE LESSON
  if (lessonToDelete != null) {
    AlertDialog(
      onDismissRequest = { lessonToDelete = null },
      title = { Text("Delete Lesson?") },
      text = {
        Text("Are you sure you want to remove '${lessonToDelete!!.title}'? This action cannot be undone.")
      },
      confirmButton = {
        Button(
          onClick = {
            onDeleteLesson(lessonToDelete!!.id)
            lessonToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
        ) {
          Text("Delete")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { lessonToDelete = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LessonFormDialog(
  modules: List<ModuleEntity>,
  initialModuleId: String,
  initialLesson: LessonEntity?,
  onDismiss: () -> Unit,
  onSave: (
    moduleId: String,
    title: String,
    desc: String,
    videoUrl: String,
    duration: String,
    thumbnailUrl: String,
    isFree: Boolean
  ) -> Unit
) {
  var selectedModuleId by remember { mutableStateOf(initialModuleId) }
  var title by remember { mutableStateOf(initialLesson?.title ?: "") }
  var desc by remember { mutableStateOf(initialLesson?.description ?: "") }
  var videoUrl by remember { mutableStateOf(initialLesson?.videoUrl ?: "") }
  var duration by remember { mutableStateOf(initialLesson?.duration ?: "15m 00s") }
  var thumbnailUrl by remember { mutableStateOf(initialLesson?.thumbnailUrl ?: "") }
  var isFreePreview by remember { mutableStateOf(initialLesson?.isFreePreview ?: false) }
  var moduleDropdownExpanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (initialLesson == null) "Add Video Lesson" else "Edit Lesson Details",
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Module Dropdown
        ExposedDropdownMenuBox(
          expanded = moduleDropdownExpanded,
          onExpandedChange = { moduleDropdownExpanded = it }
        ) {
          val activeMod = modules.find { it.id == selectedModuleId }
          OutlinedTextField(
            value = activeMod?.title ?: "Select Module",
            onValueChange = {},
            readOnly = true,
            label = { Text("Curriculum Module") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = moduleDropdownExpanded) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = moduleDropdownExpanded,
            onDismissRequest = { moduleDropdownExpanded = false }
          ) {
            modules.forEach { mod ->
              DropdownMenuItem(
                text = { Text(mod.title, fontSize = 12.sp) },
                onClick = {
                  selectedModuleId = mod.id
                  moduleDropdownExpanded = false
                }
              )
            }
          }
        }

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Lesson Title") },
          placeholder = { Text("e.g. Master CapCut Speed Ramping") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = desc,
          onValueChange = { desc = it },
          label = { Text("Lesson Description & Notes") },
          placeholder = { Text("Key techniques learned in this tutorial") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = videoUrl,
          onValueChange = { videoUrl = it },
          label = { Text("Video URL (Direct MP4, YouTube, Bunny.net)") },
          placeholder = { Text("https://example.com/stream.mp4") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("Duration") },
            placeholder = { Text("14m 20s") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        OutlinedTextField(
          value = thumbnailUrl,
          onValueChange = { thumbnailUrl = it },
          label = { Text("Custom Thumbnail Image URL (Optional)") },
          placeholder = { Text("https://example.com/thumb.jpg") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(text = "Free Preview Status", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text(text = "Allow non-enrolled visitors to watch this lesson", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          Switch(
            checked = isFreePreview,
            onCheckedChange = { isFreePreview = it },
            colors = SwitchDefaults.colors(checkedThumbColor = AccentAmber)
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            val finalUrl = videoUrl.ifBlank { "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" }
            onSave(selectedModuleId, title, desc, finalUrl, duration, thumbnailUrl, isFreePreview)
          }
        },
        enabled = title.isNotBlank()
      ) {
        Text("Save Lesson")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
