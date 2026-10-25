package com.example.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.PurchaseEntity
import com.example.data.entity.StudentEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminStudentsPurchasesScreen(
  students: List<StudentEntity>,
  purchases: List<PurchaseEntity>,
  onToggleStudentAccess: (studentId: String, hasAccess: Boolean) -> Unit
) {
  var activeTab by remember { mutableIntStateOf(0) } // 0 = Purchases Ledger, 1 = Students Directory
  var searchQuery by remember { mutableStateOf("") }

  val filteredPurchases = remember(purchases, searchQuery) {
    if (searchQuery.isBlank()) purchases
    else purchases.filter {
      it.orderId.contains(searchQuery, ignoreCase = true) ||
        it.studentName.contains(searchQuery, ignoreCase = true) ||
        it.studentEmail.contains(searchQuery, ignoreCase = true)
    }
  }

  val filteredStudents = remember(students, searchQuery) {
    if (searchQuery.isBlank()) students
    else students.filter {
      it.name.contains(searchQuery, ignoreCase = true) ||
        it.email.contains(searchQuery, ignoreCase = true)
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .testTag("admin_students_purchases_screen")
  ) {
    TabRow(selectedTabIndex = activeTab) {
      Tab(
        selected = activeTab == 0,
        onClick = { activeTab = 0 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Verified Purchases (${purchases.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
          }
        }
      )
      Tab(
        selected = activeTab == 1,
        onClick = { activeTab = 1 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Student Directory (${students.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
          }
        }
      )
    }

    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      label = { Text("Search by student name, email, or Order ID") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      singleLine = true,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      shape = RoundedCornerShape(10.dp)
    )

    if (activeTab == 0) {
      // PURCHASES LEDGER
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        if (filteredPurchases.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "No verified purchases found matching '$searchQuery'.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        items(filteredPurchases) { purchase ->
          val date = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(purchase.timestamp))
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = purchase.orderId,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(SuccessGreen.copy(alpha = 0.15f))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "VERIFIED",
                      color = SuccessGreen,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }

                Text(
                  text = "₹${purchase.amountInr}",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 16.sp,
                  color = AccentAmber
                )
              }

              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "${purchase.studentName} (${purchase.studentEmail})",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              )
              Text(
                text = "Gateway Method: ${purchase.paymentMethod} • Ref: ${purchase.paymentId}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "Recorded at: $date",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              Spacer(modifier = Modifier.height(6.dp))
              HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "Crypto Signature: ${purchase.signature}",
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                maxLines = 1
              )
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    } else {
      // STUDENTS DIRECTORY
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        if (filteredStudents.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "No students found matching '$searchQuery'.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        items(filteredStudents) { student ->
          val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(student.enrolledTimestamp))
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (student.hasPaidAccess) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = null,
                    tint = if (student.hasPaidAccess) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(
                    text = student.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                  Text(
                    text = student.email,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  if (student.phone.isNotBlank()) {
                    Text(
                      text = student.phone,
                      fontSize = 10.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  Text(
                    text = "Joined: $date",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = if (student.hasPaidAccess) "Full Access" else "Locked",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (student.hasPaidAccess) SuccessGreen else AccentAmber
                )
                Switch(
                  checked = student.hasPaidAccess,
                  onCheckedChange = { onToggleStudentAccess(student.id, it) },
                  colors = SwitchDefaults.colors(checkedThumbColor = SuccessGreen)
                )
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }
}
