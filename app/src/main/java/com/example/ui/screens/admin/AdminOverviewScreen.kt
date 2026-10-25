package com.example.ui.screens.admin

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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.AcademyConfigEntity
import com.example.data.entity.LessonEntity
import com.example.data.entity.PurchaseEntity
import com.example.data.entity.StudentEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminOverviewScreen(
  config: AcademyConfigEntity,
  lessons: List<LessonEntity>,
  students: List<StudentEntity>,
  purchases: List<PurchaseEntity>,
  onNavigateToPricing: () -> Unit,
  onNavigateToLessons: () -> Unit,
  onNavigateToAppearance: () -> Unit
) {
  val totalRevenue = purchases.filter { it.status == "VERIFIED" }.sumOf { it.amountInr }
  val paidStudentsCount = students.count { it.hasPaidAccess }
  val publishedLessonsCount = lessons.count { it.isPublished }
  val freePreviewCount = lessons.count { it.isFreePreview }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(rememberScrollState())
      .testTag("admin_overview_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Headline
    Column {
      Text(
        text = "Academy Admin Dashboard",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Live analytics and platform controls for ${config.academyName}",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // 1. PRIMARY METRICS GRID
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      MetricCard(
        title = "Gross Revenue",
        value = "₹$totalRevenue",
        subtitle = "${purchases.size} verified purchases",
        icon = Icons.Default.CurrencyRupee,
        iconTint = AccentAmber,
        modifier = Modifier.weight(1f)
      )
      MetricCard(
        title = "Active Students",
        value = "$paidStudentsCount",
        subtitle = "Lifetime enrollments",
        icon = Icons.Default.People,
        iconTint = PrimaryIndigo,
        modifier = Modifier.weight(1f)
      )
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      MetricCard(
        title = "Published Lessons",
        value = "$publishedLessonsCount / ${lessons.size}",
        subtitle = "$freePreviewCount Free Previews",
        icon = Icons.Default.VideoLibrary,
        iconTint = SuccessGreen,
        modifier = Modifier.weight(1f)
      )
      MetricCard(
        title = "Current Price",
        value = "₹${config.currentPriceInr}",
        subtitle = "Orig: ₹${config.originalPriceInr}",
        icon = Icons.Default.TrendingUp,
        iconTint = AccentAmber,
        modifier = Modifier.weight(1f)
      )
    }

    // 2. QUICK MANAGEMENT ACTIONS
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
          text = "Quick Admin Operations",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onNavigateToPricing,
            modifier = Modifier
              .weight(1f)
              .testTag("overview_quick_pricing_button")
          ) {
            Icon(Icons.Default.PriceChange, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentAmber)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Edit Price", fontSize = 11.sp)
          }

          OutlinedButton(
            onClick = onNavigateToLessons,
            modifier = Modifier
              .weight(1f)
              .testTag("overview_quick_lessons_button")
          ) {
            Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryIndigo)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Lessons", fontSize = 11.sp)
          }

          OutlinedButton(
            onClick = onNavigateToAppearance,
            modifier = Modifier
              .weight(1f)
              .testTag("overview_quick_appearance_button")
          ) {
            Icon(Icons.Default.ColorLens, contentDescription = null, modifier = Modifier.size(16.dp), tint = SuccessGreen)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Branding", fontSize = 11.sp)
          }
        }
      }
    }

    // 3. RECENT VERIFIED TRANSACTIONS FEED
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Receipt,
              contentDescription = null,
              tint = AccentAmber,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Recent Verified Purchases",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }

          Text(
            text = "${purchases.size} total",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (purchases.isEmpty()) {
          Text(
            text = "No recorded purchases yet.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        } else {
          purchases.take(4).forEach { purchase ->
            val date = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(purchase.timestamp))
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.surface
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = purchase.studentName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                  Text(
                    text = "${purchase.orderId} • ${purchase.paymentMethod}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                  )
                  Text(
                    text = date,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = "+ ₹${purchase.amountInr}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = SuccessGreen
                  )
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(SuccessGreen.copy(alpha = 0.15f))
                      .padding(horizontal = 4.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "VERIFIED",
                      fontSize = 8.sp,
                      color = SuccessGreen,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MetricCard(
  title: String,
  value: String,
  subtitle: String,
  icon: ImageVector,
  iconTint: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(iconTint.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
        }
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = value,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
