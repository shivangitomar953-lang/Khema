package com.example.ui.screens.admin

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.AcademyConfigEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen

@Composable
fun AdminPricingScreen(
  config: AcademyConfigEntity,
  onSavePricing: (currentPrice: Int, originalPrice: Int?, promoBadge: String) -> Unit
) {
  var currentPriceText by remember(config.currentPriceInr) { mutableStateOf(config.currentPriceInr.toString()) }
  var originalPriceText by remember(config.originalPriceInr) { mutableStateOf(config.originalPriceInr.toString()) }
  var promoBadgeText by remember(config.promoBadgeText) { mutableStateOf(config.promoBadgeText) }

  val parsedCurrent = currentPriceText.toIntOrNull()
  val parsedOriginal = originalPriceText.toIntOrNull()

  // Real-time backend validation feedback
  val validationError = when {
    parsedCurrent == null || parsedCurrent < 1 -> "Current course price must be at least ₹1"
    parsedCurrent > 500000 -> "Price cannot exceed ₹5,00,000"
    parsedOriginal != null && parsedOriginal < parsedCurrent -> "Original price (₹$parsedOriginal) cannot be less than discounted price (₹$parsedCurrent)"
    else -> null
  }

  val discountPercent = if (parsedCurrent != null && parsedOriginal != null && parsedOriginal > parsedCurrent) {
    (((parsedOriginal - parsedCurrent).toFloat() / parsedOriginal) * 100).toInt()
  } else 0

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(rememberScrollState())
      .testTag("admin_pricing_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Column {
      Text(
        text = "Dynamic Course Pricing",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Set course pricing, discounts, and promotional banners in real-time.",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // 1. QUICK PRESET CHIPS (₹499, ₹999, ₹1499, ₹1999, ₹2499)
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Quick Price Presets",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
        Text(
          text = "Tap any preset to instantly configure the standard INR rate:",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(499, 999, 1499, 1999).forEach { preset ->
            val isSelected = parsedCurrent == preset
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant)
                .border(
                  1.dp,
                  if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                  RoundedCornerShape(8.dp)
                )
                .clickable {
                  currentPriceText = preset.toString()
                  if (parsedOriginal == null || parsedOriginal <= preset) {
                    originalPriceText = (preset * 3).toString()
                  }
                }
                .padding(vertical = 10.dp)
                .testTag("preset_price_${preset}"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "₹$preset",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isSelected) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }

    // 2. PRICING FORM INPUTS
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Custom Price & Discounts",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )

        // Current Price Input
        OutlinedTextField(
          value = currentPriceText,
          onValueChange = { currentPriceText = it.filter { ch -> ch.isDigit() } },
          label = { Text("Active Selling Price (₹ INR)") },
          placeholder = { Text("e.g. 999") },
          leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = AccentAmber) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_current_price_input"),
          shape = RoundedCornerShape(10.dp)
        )

        // Original Price Input
        OutlinedTextField(
          value = originalPriceText,
          onValueChange = { originalPriceText = it.filter { ch -> ch.isDigit() } },
          label = { Text("Original Price (for Strikethrough Display)") },
          placeholder = { Text("e.g. 4999") },
          leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_original_price_input"),
          shape = RoundedCornerShape(10.dp)
        )

        // Promo Badge Text
        OutlinedTextField(
          value = promoBadgeText,
          onValueChange = { promoBadgeText = it },
          label = { Text("Promotional Badge Text") },
          placeholder = { Text("e.g. 80% LIMITED TIME DISCOUNT") },
          leadingIcon = { Icon(Icons.Default.LocalOffer, contentDescription = null) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_promo_badge_input"),
          shape = RoundedCornerShape(10.dp)
        )

        // Calculated Discount Box
        if (discountPercent > 0) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SuccessGreen.copy(alpha = 0.15f))
              .padding(10.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Calculated Savings: $discountPercent% OFF (₹${(parsedOriginal ?: 0) - (parsedCurrent ?: 0)} saved)",
                color = SuccessGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }
        }

        // Validation Error Box if invalid
        if (validationError != null) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(ErrorRed.copy(alpha = 0.15f))
              .padding(10.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = validationError,
                color = ErrorRed,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }

    // 3. BACKEND INTEGRITY NOTICE
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        verticalAlignment = Alignment.Top
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          tint = PrimaryIndigo,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "Tamper-Proof Backend Guarantee",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = PrimaryIndigo
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "When you publish this price, all student landing pages and checkout transactions immediately charge the new amount. Client-side price modifications during checkout are rejected.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
          )
        }
      }
    }

    // 4. SAVE & PUBLISH BUTTON
    Button(
      onClick = {
        if (parsedCurrent != null && validationError == null) {
          onSavePricing(parsedCurrent, parsedOriginal, promoBadgeText)
        }
      },
      enabled = validationError == null && parsedCurrent != null,
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .testTag("admin_save_pricing_button"),
      colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
      shape = RoundedCornerShape(12.dp)
    ) {
      Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text("Save & Publish New Pricing", fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
  }
}
