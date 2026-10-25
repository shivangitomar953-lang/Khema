package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.entity.LessonEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerModal(
  lesson: LessonEntity,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var isPlaying by remember { mutableStateOf(true) }
  var progress by remember { mutableFloatStateOf(0.15f) }
  var playbackSpeed by remember { mutableStateOf("1.0x") }
  var selectedQuality by remember { mutableStateOf("1080p 60fps") }
  var showControls by remember { mutableStateOf(true) }

  // Simulate smooth playback progress
  LaunchedEffect(isPlaying, playbackSpeed) {
    val speedMultiplier = when (playbackSpeed) {
      "1.25x" -> 1.25f
      "1.5x" -> 1.5f
      "2.0x" -> 2.0f
      else -> 1.0f
    }
    while (isPlaying) {
      delay(500)
      progress = (progress + 0.005f * speedMultiplier).coerceAtMost(1f)
      if (progress >= 1f) {
        isPlaying = false
      }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .background(StudioDarkBg)
        .testTag("video_player_modal"),
      color = StudioDarkBg
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
      ) {
        // Video Stage Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black)
            .clickable { showControls = !showControls }
        ) {
          // Video Frame / Thumbnail backdrop
          val imageResId = remember(lesson.thumbnailRes) {
            context.resources.getIdentifier(
              lesson.thumbnailRes,
              "drawable",
              context.packageName
            ).takeIf { it != 0 }
          }

          if (lesson.thumbnailUrl.isNotBlank()) {
            AsyncImage(
              model = lesson.thumbnailUrl,
              contentDescription = lesson.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          } else if (imageResId != null) {
            AsyncImage(
              model = imageResId,
              contentDescription = lesson.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }

          // Dark overlay for controls
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    Color.Black.copy(alpha = 0.7f),
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.85f)
                  )
                )
              )
          )

          // Top action bar inside video
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp)
              .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (lesson.isFreePreview) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SuccessGreen)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = "FREE PREVIEW",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
              }
              Text(
                text = selectedQuality,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .testTag("close_video_player_button")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Player",
                tint = Color.White
              )
            }
          }

          // Center Play/Pause button
          if (showControls) {
            Row(
              modifier = Modifier.align(Alignment.Center),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
              IconButton(
                onClick = { progress = (progress - 0.05f).coerceAtLeast(0f) },
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(Color.Black.copy(alpha = 0.5f))
              ) {
                Icon(
                  imageVector = Icons.Default.FastRewind,
                  contentDescription = "Rewind 10s",
                  tint = Color.White
                )
              }

              IconButton(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier
                  .size(64.dp)
                  .clip(CircleShape)
                  .background(PrimaryIndigo)
                  .testTag("player_play_pause_button")
              ) {
                Icon(
                  imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = if (isPlaying) "Pause" else "Play",
                  tint = Color.White,
                  modifier = Modifier.size(36.dp)
                )
              }

              IconButton(
                onClick = { progress = (progress + 0.05f).coerceAtMost(1f) },
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(Color.Black.copy(alpha = 0.5f))
              ) {
                Icon(
                  imageVector = Icons.Default.FastForward,
                  contentDescription = "Forward 10s",
                  tint = Color.White
                )
              }
            }
          }

          // Bottom scrub bar
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .align(Alignment.BottomCenter)
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Slider(
              value = progress,
              onValueChange = { progress = it },
              modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
              colors = SliderDefaults.colors(
                thumbColor = AccentAmber,
                activeTrackColor = PrimaryIndigo,
                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
              )
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              val currentSeconds = (progress * 860).toInt()
              val curMin = currentSeconds / 60
              val curSec = currentSeconds % 60
              Text(
                text = "%02d:%02d / %s".format(curMin, curSec, lesson.duration),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )

              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Speed,
                  contentDescription = null,
                  tint = AccentAmber,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = playbackSpeed,
                  color = AccentAmber,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        // Lesson Info & Controls below player
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Text(
            text = lesson.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${lesson.viewsCount + 1} views",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            // Speed switcher chips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf("1.0x", "1.25x", "1.5x", "2.0x").forEach { speed ->
                FilterChip(
                  selected = playbackSpeed == speed,
                  onClick = { playbackSpeed = speed },
                  label = { Text(text = speed, fontSize = 11.sp) }
                )
              }
            }
          }

          // Description Card
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "Lesson Notes & Summary",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = AccentAmber
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = lesson.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
              )
            }
          }

          // Video Source Details
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.HighQuality,
                  contentDescription = null,
                  tint = PrimaryIndigo,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Direct Video Stream",
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 13.sp
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = lesson.videoUrl,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
              )
            }
          }
        }
      }
    }
  }
}
