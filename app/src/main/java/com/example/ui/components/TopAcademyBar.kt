package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.entity.AcademyConfigEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.StudioDarkSurface
import androidx.compose.material.icons.filled.InstallMobile
import com.example.ui.theme.SuccessGreen

@Composable
fun TopAcademyBar(
  config: AcademyConfigEntity,
  isAdminMode: Boolean,
  onToggleAdmin: () -> Unit,
  onOpenStorageGuide: () -> Unit,
  onOpenInstallModal: () -> Unit
) {
  val context = LocalContext.current

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("top_academy_bar"),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 4.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Academy Logo & Title
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        val iconResId = remember {
          context.resources.getIdentifier(
            "unique_editor_icon_1792954692313",
            "drawable",
            context.packageName
          ).takeIf { it != 0 }
        }

        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(PrimaryIndigo),
          contentAlignment = Alignment.Center
        ) {
          if (iconResId != null) {
            Image(
              painter = painterResource(id = iconResId),
              contentDescription = "Logo",
              modifier = Modifier.size(38.dp)
            )
          } else {
            Icon(
              imageVector = Icons.Default.School,
              contentDescription = "Logo",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Text(
            text = config.academyName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (isAdminMode) AccentAmber else SuccessGreen)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isAdminMode) "Admin Studio Active" else "Online Academy",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Action Buttons
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Install App button
        IconButton(
          onClick = onOpenInstallModal,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(SuccessGreen.copy(alpha = 0.15f))
            .testTag("top_bar_install_app_button")
        ) {
          Icon(
            imageVector = Icons.Default.InstallMobile,
            contentDescription = "Install App on Android",
            tint = SuccessGreen,
            modifier = Modifier.size(20.dp)
          )
        }

        // Storage & Hosting Guide button
        IconButton(
          onClick = onOpenStorageGuide,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .testTag("storage_guide_icon_button")
        ) {
          Icon(
            imageVector = Icons.Default.CloudQueue,
            contentDescription = "Storage Guide",
            tint = AccentAmber,
            modifier = Modifier.size(20.dp)
          )
        }

        // Mode Switcher Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isAdminMode) AccentAmber.copy(alpha = 0.2f) else PrimaryIndigo.copy(alpha = 0.15f))
            .clickable { onToggleAdmin() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("toggle_admin_mode_pill"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (isAdminMode) Icons.Default.AdminPanelSettings else Icons.Default.School,
              contentDescription = null,
              tint = if (isAdminMode) AccentAmber else PrimaryIndigo,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isAdminMode) "Admin View" else "Student View",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isAdminMode) AccentAmber else PrimaryIndigo
            )
          }
        }
      }
    }
  }
}
