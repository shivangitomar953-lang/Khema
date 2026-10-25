package com.example.ui.screens.student

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.AcademyConfigEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen

@Composable
fun CheckoutScreen(
  config: AcademyConfigEntity,
  studentName: String,
  studentEmail: String,
  isLoading: Boolean,
  onPayNow: (name: String, email: String, phone: String, paymentMethod: String) -> Unit
) {
  var name by remember(studentName) { mutableStateOf(studentName) }
  var email by remember(studentEmail) { mutableStateOf(studentEmail) }
  var phone by remember { mutableStateOf("+91 98765 12340") }
  var selectedMethod by remember { mutableStateOf("UPI (Google Pay)") }
  var upiId by remember { mutableStateOf("") }
  var cardNumber by remember { mutableStateOf("") }
  var cardExpiry by remember { mutableStateOf("") }
  var cardCvv by remember { mutableStateOf("") }

  val paymentMethods = listOf(
    PaymentOption("UPI (Google Pay)", "Fast 1-click payment with GPay", Icons.Default.QrCode),
    PaymentOption("UPI (PhonePe)", "Direct bank authorization via PhonePe", Icons.Default.QrCode),
    PaymentOption("UPI (Paytm / BHIM)", "Scan & Pay or UPI VPA", Icons.Default.QrCode),
    PaymentOption("Credit / Debit Card", "Visa, Mastercard, RuPay cards", Icons.Default.CreditCard),
    PaymentOption("NetBanking", "HDFC, ICICI, SBI, Axis & all major banks", Icons.Default.AccountBalance)
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .verticalScroll(rememberScrollState())
      .testTag("checkout_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Spacer(modifier = Modifier.height(6.dp))

    // Header with Security Trust
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Secure Checkout",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "256-Bit SSL Encrypted Payment Gate",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(SuccessGreen.copy(alpha = 0.15f))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = null,
            tint = SuccessGreen,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "VERIFIED", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
      }
    }

    // 1. ORDER SUMMARY & DYNAMIC PRICE BREAKDOWN (PULLED FROM DATABASE)
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Order Summary",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = config.courseTitle,
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Instructor: ${config.instructorName} • Lifetime Access",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(10.dp))

        // Price Breakdown
        PriceRow("Standard Course Fee", "₹${config.originalPriceInr}")
        val discountAmount = (config.originalPriceInr - config.currentPriceInr).coerceAtLeast(0)
        if (discountAmount > 0) {
          PriceRow("Creator Special Discount", "- ₹$discountAmount", isDiscount = true)
        }
        PriceRow("Taxes & Platform Fees", "₹0 (Waived)", isFree = true)

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Total Payable (INR)",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = "Server Authoritative Price",
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Text(
            text = "₹${config.currentPriceInr}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = AccentAmber
          )
        }
      }
    }

    // 2. STUDENT DETAILS FORM
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "Student Information",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Course access and completion certificate will be linked to this profile.",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Full Name") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("checkout_name_input"),
          shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Email Address") },
          leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("checkout_email_input"),
          shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Mobile Number (for WhatsApp Community)") },
          leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("checkout_phone_input"),
          shape = RoundedCornerShape(10.dp)
        )
      }
    }

    // 3. PAYMENT METHOD SELECTION
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "Select Payment Gateway",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )

        paymentMethods.forEach { method ->
          val isSelected = selectedMethod == method.name
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSelected) PrimaryIndigo.copy(alpha = 0.12f) else Color.Transparent)
              .border(
                1.dp,
                if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(10.dp)
              )
              .clickable { selectedMethod = method.name }
              .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            RadioButton(
              selected = isSelected,
              onClick = { selectedMethod = method.name },
              colors = RadioButtonDefaults.colors(selectedColor = PrimaryIndigo)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = method.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              )
              Text(
                text = method.subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Icon(
              imageVector = method.icon,
              contentDescription = null,
              tint = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }

    // 4. PAY NOW BUTTON & SECURITY FOOTNOTE
    Button(
      onClick = {
        onPayNow(name, email, phone, selectedMethod)
      },
      enabled = !isLoading && name.isNotBlank() && email.isNotBlank(),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("checkout_pay_button"),
      colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
      shape = RoundedCornerShape(12.dp)
    ) {
      if (isLoading) {
        CircularProgressIndicator(
          color = Color.White,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text("Verifying Payment with Server...")
      } else {
        Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Pay ₹${config.currentPriceInr} & Unlock Masterclass",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 24.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Security,
        contentDescription = null,
        tint = SuccessGreen,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Guaranteed 7-day unconditional refund policy. Zero risk.",
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

private data class PaymentOption(
  val name: String,
  val subtitle: String,
  val icon: ImageVector
)

@Composable
private fun PriceRow(
  label: String,
  amount: String,
  isDiscount: Boolean = false,
  isFree: Boolean = false
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      fontSize = 12.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = amount,
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      color = when {
        isDiscount -> SuccessGreen
        isFree -> SuccessGreen
        else -> MaterialTheme.colorScheme.onSurface
      }
    )
  }
}
