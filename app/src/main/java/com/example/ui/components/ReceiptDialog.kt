package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
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
import com.example.data.entity.PurchaseEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReceiptDialog(
  purchase: PurchaseEntity,
  onDismiss: () -> Unit,
  onStartLearning: () -> Unit
) {
  val dateFormatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    .format(Date(purchase.timestamp))

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("receipt_dialog"),
    title = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(SuccessGreen.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = SuccessGreen,
            modifier = Modifier.size(36.dp)
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Payment Verified!",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = SuccessGreen
        )
        Text(
          text = "You have full lifetime access to the Masterclass",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    },
    text = {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ReceiptRow(label = "Order ID", value = purchase.orderId, isMonospace = true)
          ReceiptRow(label = "Date & Time", value = dateFormatted)
          ReceiptRow(label = "Student", value = purchase.studentName)
          ReceiptRow(label = "Email", value = purchase.studentEmail)
          ReceiptRow(label = "Payment Mode", value = purchase.paymentMethod)
          ReceiptRow(label = "Transaction Ref", value = purchase.paymentId, isMonospace = true)

          HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Amount Paid",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "₹${purchase.amountInr}",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = AccentAmber
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Verified,
              contentDescription = null,
              tint = SuccessGreen,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
              text = "Status: ${purchase.status} • Cryptographically Signed",
              fontSize = 11.sp,
              color = SuccessGreen,
              fontWeight = FontWeight.Medium
            )
          }

          Text(
            text = "Signature: ${purchase.signature.take(24)}...",
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onDismiss()
          onStartLearning()
        },
        modifier = Modifier.testTag("receipt_start_learning_button"),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
      ) {
        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.size(6.dp))
        Text("Start Learning Now")
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("receipt_close_button")
      ) {
        Text("Close")
      }
    }
  )
}

@Composable
private fun ReceiptRow(
  label: String,
  value: String,
  isMonospace: Boolean = false
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      fontSize = 12.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default
    )
  }
}
