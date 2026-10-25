package com.example.ui.screens.student

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.entity.AcademyConfigEntity
import com.example.data.entity.LessonEntity
import com.example.data.entity.ModuleEntity
import com.example.data.repository.AcademyRepository
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen

import androidx.compose.material.icons.filled.InstallMobile

@Composable
fun CourseLandingScreen(
  config: AcademyConfigEntity,
  modules: List<ModuleEntity>,
  lessons: List<LessonEntity>,
  isEnrolled: Boolean,
  onEnrollClick: () -> Unit,
  onGoToLearning: () -> Unit,
  onPlayLesson: (LessonEntity) -> Unit,
  onOpenInstallModal: () -> Unit
) {
  val context = LocalContext.current
  val benefits = remember(config.benefitsJson) { AcademyRepository.parseBenefits(config.benefitsJson) }
  val faqs = remember(config.faqsJson) { AcademyRepository.parseFaqs(config.faqsJson) }

  // Expanded modules state
  val expandedModules = remember { mutableStateMapOf<String, Boolean>() }
  // Expand first module by default
  remember(modules) {
    if (modules.isNotEmpty() && !expandedModules.containsKey(modules.first().id)) {
      expandedModules[modules.first().id] = true
    }
  }

  // Expanded FAQs state
  val expandedFaqs = remember { mutableStateMapOf<String, Boolean>() }

  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 76.dp)
        .testTag("course_landing_scrollable_list")
    ) {
      // 1. HERO BANNER & INTRO
      item {
        HeroBannerSection(
          config = config,
          onEnrollClick = if (isEnrolled) onGoToLearning else onEnrollClick,
          isEnrolled = isEnrolled
        )
      }

      // 2. DYNAMIC PRICING SHOWCASE CARD
      item {
        DynamicPricingCard(
          config = config,
          isEnrolled = isEnrolled,
          onEnrollClick = if (isEnrolled) onGoToLearning else onEnrollClick
        )
      }

      // INSTALL ON PHONE BANNER
      item {
        InstallAppBanner(onInstallClick = onOpenInstallModal)
      }

      // 3. STATS STRIP
      item {
        StatsStrip()
      }

      // 4. COURSE CURRICULUM SYLLABUS
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Course Curriculum",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${modules.size} Modules • ${lessons.size} In-Depth Lessons",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(PrimaryIndigo.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "Free Previews Available",
                fontSize = 11.sp,
                color = PrimaryIndigo,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
        }
      }

      // Syllabus Modules & Lessons Accordion
      items(modules) { module ->
        val isExpanded = expandedModules[module.id] ?: false
        val moduleLessons = lessons.filter { it.moduleId == module.id }.sortedBy { it.orderIndex }

        ModuleAccordionCard(
          module = module,
          lessons = moduleLessons,
          isExpanded = isExpanded,
          isEnrolled = isEnrolled,
          onToggleExpand = { expandedModules[module.id] = !isExpanded },
          onPlayLesson = onPlayLesson
        )
      }

      // 5. COURSE BENEFITS / WHAT YOU GET
      item {
        BenefitsSection(benefits = benefits)
      }

      // 6. INSTRUCTOR SHOWCASE
      item {
        InstructorCard(config = config)
      }

      // 7. FREQUENTLY ASKED QUESTIONS
      item {
        FaqSection(
          faqs = faqs,
          expandedFaqs = expandedFaqs,
          onToggleFaq = { id -> expandedFaqs[id] = !(expandedFaqs[id] ?: false) }
        )
      }

      // 8. FOOTER
      item {
        FooterSection(config = config)
      }
    }

    // STICKY BOTTOM ENROLLMENT BAR
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .testTag("sticky_bottom_enroll_bar"),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
      shadowElevation = 8.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "₹${config.currentPriceInr}",
              fontSize = 20.sp,
              fontWeight = FontWeight.ExtraBold,
              color = AccentAmber
            )
            Spacer(modifier = Modifier.width(6.dp))
            if (config.originalPriceInr > config.currentPriceInr) {
              Text(
                text = "₹${config.originalPriceInr}",
                fontSize = 13.sp,
                textDecoration = TextDecoration.LineThrough,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          Text(
            text = "Lifetime Access + Certificate",
            fontSize = 10.sp,
            color = SuccessGreen,
            fontWeight = FontWeight.Medium
          )
        }

        Button(
          onClick = if (isEnrolled) onGoToLearning else onEnrollClick,
          modifier = Modifier.testTag("sticky_enroll_cta_button"),
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(
            imageVector = if (isEnrolled) Icons.Default.PlayCircleFilled else Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isEnrolled) "Go to Learning" else "Enroll for ₹${config.currentPriceInr}",
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
private fun HeroBannerSection(
  config: AcademyConfigEntity,
  onEnrollClick: () -> Unit,
  isEnrolled: Boolean
) {
  val context = LocalContext.current
  val bannerResId = remember(config.heroBannerRes) {
    context.resources.getIdentifier(
      config.heroBannerRes,
      "drawable",
      context.packageName
    ).takeIf { it != 0 }
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(16f / 9f)
      ) {
        if (config.heroBannerUrl.isNotBlank()) {
          AsyncImage(
            model = config.heroBannerUrl,
            contentDescription = config.courseTitle,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        } else if (bannerResId != null) {
          Image(
            painter = painterResource(id = bannerResId),
            contentDescription = config.courseTitle,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        // Gradient overlay
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color.Transparent,
                  Color.Black.copy(alpha = 0.85f)
                )
              )
            )
        )

        // Rating and discount badges
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .align(Alignment.TopCenter),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(AccentOrange)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = config.promoBadgeText,
              color = Color.Black,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color.Black.copy(alpha = 0.6f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "4.9 (12.4k reviews)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Hero title overlay
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .align(Alignment.BottomStart)
        ) {
          Text(
            text = config.courseTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )
        }
      }

      // Hero Body
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = config.heroHeading,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = AccentAmber
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = config.heroSubtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 20.sp
        )
      }
    }
  }
}

@Composable
private fun DynamicPricingCard(
  config: AcademyConfigEntity,
  isEnrolled: Boolean,
  onEnrollClick: () -> Unit
) {
  val discountPercent = if (config.originalPriceInr > config.currentPriceInr) {
    (((config.originalPriceInr - config.currentPriceInr).toFloat() / config.originalPriceInr) * 100).toInt()
  } else 0

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .border(1.dp, PrimaryIndigo.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "COURSE INVESTMENT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "₹${config.currentPriceInr}",
              fontSize = 32.sp,
              fontWeight = FontWeight.ExtraBold,
              color = AccentAmber
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (config.originalPriceInr > config.currentPriceInr) {
              Text(
                text = "₹${config.originalPriceInr}",
                fontSize = 16.sp,
                textDecoration = TextDecoration.LineThrough,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
              )
            }
          }
        }

        if (discountPercent > 0) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(SuccessGreen.copy(alpha = 0.15f))
              .border(1.dp, SuccessGreen, RoundedCornerShape(10.dp))
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = "$discountPercent% OFF",
              color = SuccessGreen,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 13.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
      Spacer(modifier = Modifier.height(12.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "One-time payment • Lifetime access • All updates included", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = onEnrollClick,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("pricing_card_cta_button"),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(
          text = if (isEnrolled) "Resume Masterclass" else "${config.ctaButtonText} (₹${config.currentPriceInr})",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
private fun StatsStrip() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    StatItem("15,000+", "Graduated Students", Modifier.weight(1f))
    StatItem("4.9 / 5.0", "Highest Rating", Modifier.weight(1f))
    StatItem("100%", "Practical Projects", Modifier.weight(1f))
  }
}

@Composable
private fun StatItem(number: String, label: String, modifier: Modifier) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = number, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryIndigo)
      Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}

@Composable
private fun ModuleAccordionCard(
  module: ModuleEntity,
  lessons: List<LessonEntity>,
  isExpanded: Boolean,
  isEnrolled: Boolean,
  onToggleExpand: () -> Unit,
  onPlayLesson: (LessonEntity) -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 5.dp),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column {
      // Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onToggleExpand() }
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = module.title,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          if (module.description.isNotBlank()) {
            Text(
              text = module.description,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Lessons List inside Module
      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          lessons.forEach { lesson ->
            LessonRowItem(
              lesson = lesson,
              canWatch = isEnrolled || lesson.isFreePreview,
              onPlayClick = { onPlayLesson(lesson) }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LessonRowItem(
  lesson: LessonEntity,
  canWatch: Boolean,
  onPlayClick: () -> Unit
) {
  val context = LocalContext.current
  val thumbResId = remember(lesson.thumbnailRes) {
    context.resources.getIdentifier(
      lesson.thumbnailRes,
      "drawable",
      context.packageName
    ).takeIf { it != 0 }
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .clickable(enabled = canWatch) { onPlayClick() },
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Thumbnail
      Box(
        modifier = Modifier
          .size(width = 80.dp, height = 48.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(Color.Black)
      ) {
        if (lesson.thumbnailUrl.isNotBlank()) {
          AsyncImage(
            model = lesson.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        } else if (thumbResId != null) {
          Image(
            painter = painterResource(id = thumbResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (canWatch) PrimaryIndigo.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.7f))
            .align(Alignment.Center),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (canWatch) Icons.Default.PlayArrow else Icons.Default.Lock,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = lesson.title,
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp,
          maxLines = 2
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = lesson.duration,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          if (lesson.isFreePreview) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(SuccessGreen.copy(alpha = 0.2f))
                .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
              Text(
                text = "FREE PREVIEW",
                color = SuccessGreen,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun BenefitsSection(benefits: List<String>) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "What You Receive Inside",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(10.dp))
      benefits.forEach { benefit ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = SuccessGreen,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = benefit, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        }
      }
    }
  }
}

@Composable
private fun InstructorCard(config: AcademyConfigEntity) {
  val context = LocalContext.current
  val avatarResId = remember(config.instructorAvatarRes) {
    context.resources.getIdentifier(
      config.instructorAvatarRes,
      "drawable",
      context.packageName
    ).takeIf { it != 0 }
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .border(2.dp, AccentAmber, CircleShape)
        ) {
          if (config.instructorAvatarUrl.isNotBlank()) {
            AsyncImage(
              model = config.instructorAvatarUrl,
              contentDescription = config.instructorName,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          } else if (avatarResId != null) {
            Image(
              painter = painterResource(id = avatarResId),
              contentDescription = config.instructorName,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = config.instructorName,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(imageVector = Icons.Default.Verified, contentDescription = "Verified", tint = PrimaryIndigo, modifier = Modifier.size(16.dp))
          }
          Text(
            text = "Lead Video Director & Creator",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = config.instagramHandle,
            fontSize = 12.sp,
            color = AccentAmber,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = config.instructorBio,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(12.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = {
            try {
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(config.instagramUrl))
              context.startActivity(intent)
            } catch (e: Exception) {}
          },
          modifier = Modifier.weight(1f)
        ) {
          Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Instagram", fontSize = 11.sp)
        }

        OutlinedButton(
          onClick = {
            try {
              val uri = Uri.parse("https://wa.me/${config.whatsappSupport.filter { it.isDigit() }}")
              val intent = Intent(Intent.ACTION_VIEW, uri)
              context.startActivity(intent)
            } catch (e: Exception) {}
          },
          modifier = Modifier.weight(1f)
        ) {
          Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "WhatsApp", fontSize = 11.sp)
        }
      }
    }
  }
}

@Composable
private fun FaqSection(
  faqs: List<com.example.data.model.FaqItem>,
  expandedFaqs: Map<String, Boolean>,
  onToggleFaq: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Text(
      text = "Frequently Asked Questions",
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(10.dp))

    faqs.forEach { faq ->
      val isExp = expandedFaqs[faq.id] ?: false
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(
          modifier = Modifier
            .clickable { onToggleFaq(faq.id) }
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = faq.question,
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp,
              modifier = Modifier.weight(1f)
            )
            Icon(
              imageVector = if (isExp) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = null
            )
          }

          AnimatedVisibility(visible = isExp) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
              Text(
                text = faq.answer,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun FooterSection(config: AcademyConfigEntity) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(20.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = config.academyName,
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp,
      color = AccentAmber
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = config.footerText,
      fontSize = 11.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
  }
}

@Composable
private fun InstallAppBanner(onInstallClick: () -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .clickable { onInstallClick() }
      .testTag("install_app_banner_card"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = PrimaryIndigo.copy(alpha = 0.15f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(SuccessGreen),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.InstallMobile,
          contentDescription = null,
          tint = Color.Black,
          modifier = Modifier.size(22.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Install on Android Phone",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = PrimaryIndigo
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SuccessGreen)
              .padding(horizontal = 4.dp, vertical = 2.dp)
          ) {
            Text("FREE APK / PWA", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }
        }
        Text(
          text = "Tap here to install directly on your phone without computer or USB",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "Install",
        tint = PrimaryIndigo,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}
