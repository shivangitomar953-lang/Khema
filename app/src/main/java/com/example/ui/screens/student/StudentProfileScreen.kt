package com.example.ui.screens.student

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.AcademyConfigEntity
import com.example.data.entity.PurchaseEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.filled.InstallMobile

@Composable
fun StudentProfileScreen(
  config: AcademyConfigEntity,
  studentName: String,
  studentEmail: String,
  isEnrolled: Boolean,
  purchases: List<PurchaseEntity>,
  onOpenInstallModal: () -> Unit
) {
  val studentPurchases = purchases.filter {
    it.studentEmail.equals(studentEmail, ignoreCase = true) || it.studentName.equals(studentName, ignoreCase = true)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(rememberScrollState())
      .testTag("student_profile_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Profile Header Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(PrimaryIndigo),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(
            text = studentName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = studentEmail,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (isEnrolled) Icons.Default.Verified else Icons.Default.School,
              contentDescription = null,
              tint = if (isEnrolled) SuccessGreen else AccentAmber,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isEnrolled) "Verified Academy Student" else "Free Account (No Course)",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = if (isEnrolled) SuccessGreen else AccentAmber
            )
          }
        }
      }
    }

    // Install on Phone Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onOpenInstallModal() }
        .testTag("profile_install_card"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = PrimaryIndigo.copy(alpha = 0.15f))
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(SuccessGreen),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.InstallMobile,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
          )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Install App on Android",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = PrimaryIndigo
          )
          Text(
            text = "Install APK directly or Add to Home Screen (No computer needed)",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // Purchase History & Invoices
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.ReceiptLong,
            contentDescription = null,
            tint = AccentAmber,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Payment Receipts & Invoices",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (studentPurchases.isEmpty()) {
          Text(
            text = "No recorded purchases found for this account. Enroll in the masterclass to generate your official tax invoice.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        } else {
          studentPurchases.forEach { purchase ->
            val date = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(purchase.timestamp))
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = purchase.orderId,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                  )
                  Text(
                    text = "₹${purchase.amountInr}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = AccentAmber
                  )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = purchase.courseTitle,
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Mode: ${purchase.paymentMethod} • Ref: ${purchase.paymentId}",
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = date,
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }
  }
}
