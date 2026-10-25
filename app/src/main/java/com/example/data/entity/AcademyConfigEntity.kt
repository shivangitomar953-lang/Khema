package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "academy_config")
data class AcademyConfigEntity(
  @PrimaryKey
  val id: String = "singleton_config",
  val academyName: String = "Unique Editor Ji Academy",
  val logoUrl: String = "",
  val heroHeading: String = "Master Cinematic Video Editing & Reels Growth",
  val heroSubtitle: String = "From Mobile CapCut to Pro Premiere & DaVinci Resolve — Create viral videos, build a massive audience, and earn high-paying editing gigs.",
  val heroBannerRes: String = "academy_hero_banner_1792954771092",
  val heroBannerUrl: String = "",
  val primaryColorHex: String = "#6366F1",
  val accentColorHex: String = "#F59E0B",
  val ctaButtonText: String = "Enroll in Masterclass",
  val ctaButtonColorHex: String = "#6366F1",
  val courseTitle: String = "Full-Stack Video Editing & Viral Content Blueprint",
  val courseDescription: String = "Complete step-by-step masterclass covering mobile pacing, dynamic captions, sound design, color grading LUTs, and client pitching templates.",
  val instructorName: String = "Unique_editor_ji",
  val instructorBio: String = "Founder of Unique_editor_ji Academy with 500K+ creator community. Mentored 15,000+ editors worldwide.",
  val instructorAvatarRes: String = "instructor_profile_1792954806015",
  val instructorAvatarUrl: String = "",
  val instagramHandle: String = "@unique_editor_ji",
  val instagramUrl: String = "https://instagram.com/unique_editor_ji",
  val whatsappSupport: String = "+91 98765 43210",
  val supportEmail: String = "support@uniqueeditorji.com",
  val footerText: String = "© 2026 Unique_editor_ji Academy. Empowering editors and content creators across the globe.",
  val benefitsJson: String = "",
  val faqsJson: String = "",
  val currentPriceInr: Int = 999,
  val originalPriceInr: Int = 4999,
  val promoBadgeText: String = "80% DISCOUNT • LIMITED SEATS",
  val isRegistrationOpen: Boolean = true
)
