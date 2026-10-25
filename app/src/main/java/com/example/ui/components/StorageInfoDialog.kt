package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen

@Composable
fun StorageInfoDialog(
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("storage_info_dialog"),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "Storage Guide",
          tint = AccentAmber,
          modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
          text = "Video Hosting & Storage Guide",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Learn how to host course videos cost-effectively with zero or minimal expense.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        StorageOptionCard(
          title = "1. YouTube Unlisted (100% FREE)",
          icon = Icons.Default.Videocam,
          iconTint = Color(0xFFFF0000),
          tier = "Unlimited Storage & Bandwidth",
          cost = "₹0 / Free Forever",
          details = "Upload lessons to YouTube as 'Unlisted'. Paste the link into the admin lesson manager. Ideal for course creators starting on a budget with zero hosting costs."
        )

        StorageOptionCard(
          title = "2. Cloudflare Stream / R2 (Recommended Pro)",
          icon = Icons.Default.Cloud,
          iconTint = AccentAmber,
          tier = "10 GB Free Storage + Zero Egress Fees",
          cost = "$5 / month for 1,000 video minutes",
          details = "Enterprise-grade DRM, custom video player, adaptive bitrate streaming (1080p, 720p, 480p), and fast global CDN."
        )

        StorageOptionCard(
          title = "3. Bunny.net Stream (Ultra Affordable)",
          icon = Icons.Default.Security,
          iconTint = PrimaryIndigo,
          tier = "Pay As You Go",
          cost = "$0.01 / GB stored (~₹0.80/month)",
          details = "Includes built-in media transcoding, hotlinking protection, dynamic watermarking to prevent piracy, and direct HLS/MP4 streams."
        )

        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          shape = RoundedCornerShape(10.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "💡 Best Practice for Unique_editor_ji Academy:",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = AccentAmber
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "For Free Preview lessons, use YouTube Unlisted for instant high-speed streaming. For Paid Masterclass lessons, use Bunny.net or Cloudflare Stream direct MP4/HLS links to protect your content.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        modifier = Modifier.testTag("storage_info_close_button")
      ) {
        Text("Got it")
      }
    }
  )
}

@Composable
private fun StorageOptionCard(
  title: String,
  icon: ImageVector,
  iconTint: Color,
  tier: String,
  cost: String,
  details: String
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
      }
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(text = tier, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = cost, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = details, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}
