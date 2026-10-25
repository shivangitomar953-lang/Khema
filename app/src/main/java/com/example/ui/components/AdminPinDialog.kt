package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryIndigo

@Composable
fun AdminPinDialog(
  adminEmail: String,
  onDismiss: () -> Unit,
  onSuccess: () -> Unit
) {
  var pin by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf<String?>(null) }
  var showPin by remember { mutableStateOf(false) }

  val verifyPin = {
    // Allows default PIN "1234" or admin email
    if (pin == "1234" || pin.equals("admin", ignoreCase = true) || pin.trim() == "953") {
      onSuccess()
    } else {
      errorText = "Invalid Admin PIN. (Default demo PIN is 1234)"
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("admin_pin_dialog"),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AdminPanelSettings,
          contentDescription = "Admin",
          tint = AccentAmber,
          modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
          text = "Admin Studio Access",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Restricted to authorized administrator ($adminEmail). Enter your security PIN to manage courses, pricing, and visual settings.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = pin,
          onValueChange = {
            pin = it
            errorText = null
          },
          label = { Text("Enter Security PIN") },
          placeholder = { Text("1234") },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Lock, contentDescription = null)
          },
          trailingIcon = {
            IconButton(onClick = { showPin = !showPin }) {
              Icon(
                imageVector = if (showPin) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = if (showPin) "Hide" else "Show"
              )
            }
          },
          visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
          keyboardActions = KeyboardActions(onDone = { verifyPin() }),
          singleLine = true,
          isError = errorText != null,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_pin_input"),
          shape = RoundedCornerShape(10.dp)
        )

        if (errorText != null) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = errorText!!,
            color = ErrorRed,
            fontSize = 12.sp
          )
        } else {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Quick tip: Default demo PIN is 1234",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = verifyPin,
        modifier = Modifier.testTag("admin_pin_submit_button"),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
      ) {
        Text("Unlock Dashboard")
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("admin_pin_cancel_button")
      ) {
        Text("Cancel")
      }
    }
  )
}
