package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen

@Composable
fun InstallAppModal(
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Direct Native APK, 1 = Browser / PWA
  var copyFeedback by remember { mutableStateOf(false) }

  val sharedUrl = "https://ais-pre-r7r4iqa64ckpsii36ue3x6-435699159801.asia-east1.run.app"

  val copyToClipboard = {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Unique Editor Ji Academy URL", sharedUrl)
    clipboard.setPrimaryClip(clip)
    copyFeedback = true
  }

  val shareLink = {
    try {
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Unique Editor Ji Academy App")
        putExtra(Intent.EXTRA_TEXT, "Install Unique Editor Ji Academy directly on your phone: $sharedUrl")
      }
      context.startActivity(Intent.createChooser(intent, "Share App Link to Phone"))
    } catch (e: Exception) {}
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("install_app_modal"),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(SuccessGreen.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.InstallMobile,
            contentDescription = null,
            tint = SuccessGreen,
            modifier = Modifier.size(24.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "Install on Android Phone",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "100% Free • No Computer or USB Required",
            fontSize = 11.sp,
            color = SuccessGreen,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Tab Selector (Native APK vs Browser PWA)
        TabRow(selectedTabIndex = selectedTab) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("Native APK (Recommended)", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("Browser / PWA", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
          )
        }

        if (selectedTab == 0) {
          // TAB 0: NATIVE APK DIRECT INSTALL
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Actual APK Successfully Built",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = SuccessGreen
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "File: app-debug.apk (~30 MB)\nPackage: com.aistudio.uniqueeditor.kxyza\nOutput: app/build/outputs/apk/debug/app-debug.apk",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Text(
            text = "How to install on your phone in 3 steps:",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )

          InstallStepCard(
            number = "1",
            title = "Download the APK",
            description = "On your phone's browser, open your AI Studio project menu (or Settings / Export in top right) and tap 'Download APK' (or 'Download app-debug.apk')."
          )

          InstallStepCard(
            number = "2",
            title = "Tap to Install",
            description = "Once downloaded, tap the download notification or find 'app-debug.apk' in your phone's Downloads folder. Tap 'Install'."
          )

          InstallStepCard(
            number = "3",
            title = "Allow Unknown Sources (If prompted)",
            description = "If Android asks 'Install unknown apps', toggle 'Allow from this source' for Chrome/Files. The app installs directly onto your phone home screen!"
          )
        } else {
          // TAB 1: BROWSER / PWA ADD TO HOME SCREEN
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Web PWA Manifest & Service Worker Ready",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = AccentAmber
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "App Name: Unique Editor Ji Academy\nShort Name: Unique Academy\nDisplay: standalone\nHTTPS: Enabled",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Text(
            text = "How to install via Chrome on your Android phone:",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )

          InstallStepCard(
            number = "1",
            title = "Open App Link in Chrome",
            description = "Open the Shared URL in Google Chrome on your phone."
          )

          InstallStepCard(
            number = "2",
            title = "Tap Chrome Menu (⋮)",
            description = "Tap the three vertical dots (⋮) in the top right corner of the Chrome browser."
          )

          InstallStepCard(
            number = "3",
            title = "Select 'Install app' or 'Add to Home screen'",
            description = "Tap 'Install app' (or 'Add to Home screen'). Unique Editor Ji Academy will be installed as a standalone fullscreen app on your phone with its custom app icon!"
          )
        }

        // Quick Share / Copy URL Section
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "Send link to your Android phone:",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = sharedUrl,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = copyToClipboard,
                modifier = Modifier
                  .weight(1f)
                  .testTag("copy_install_link_button")
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (copyFeedback) "Copied!" else "Copy Link", fontSize = 11.sp)
              }

              Button(
                onClick = shareLink,
                modifier = Modifier
                  .weight(1f)
                  .testTag("share_install_link_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
              ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share to Phone", fontSize = 11.sp)
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        modifier = Modifier.testTag("close_install_modal_button")
      ) {
        Text("Done")
      }
    }
  )
}

@Composable
private fun InstallStepCard(
  number: String,
  title: String,
  description: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .clip(CircleShape)
        .background(PrimaryIndigo),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = number,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
      )
    }
    Spacer(modifier = Modifier.width(10.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = description,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 16.sp
      )
    }
  }
}
