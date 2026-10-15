package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// --- SAHARA NIGHT DARK THEME SYSTEM (Guelmim Delivery Design System) ---
// ============================================================================

// Primary Sahara Night High-Contrast Tokens
val AppBackground = Color(0xFF070B14)      // Deepest Obsidian Background
val CardSurface = Color(0xFF111827)        // Elevated Navy Card Surface
val CardSurfaceVariant = Color(0xFF1E293B) // Input fields, inner pills, unselected vehicle cards
val PrimaryText = Color(0xFFF9FAFB)        // Crisp White - Headings, titles, primary prices
val SecondaryText = Color(0xFF9CA3AF)      // Muted Silver - Arabic subtitles, timestamps, step labels
val AccentAmber = Color(0xFFF59E0B)        // Selected vehicle borders, star ratings, alert badges
val AccentEmerald = Color(0xFF10B981)      // Delivered badges, active status dots, completed steps
val AccentBlue = Color(0xFF3B82F6)         // Primary CTA buttons, active route polylines
val BorderStroke = Color(0xFF334155)       // 1.dp subtle borders for cards and chips

// Frosted Glass & Blur Effect Colors (Dark-Locked)
val GlassFrostedSurface = Color(0xEB111827)       // Translucent elevated navy glass
val GlassFrostedSubtle = Color(0xCC1E293B)        // Translucent inner pill glass
val GlassFrostedBorder = Color(0x3338BDF8)        // Delicate luminous glass border
val GlassFrostedDark = Color(0xF2070B14)          // 95% obsidian translucent HUD
val GlassFrostedDarkBorder = Color(0x33475569)    // Delicate slate glass border

// Vector Map Palette (High-Contrast Night Mode)
val MapCanvasBg = Color(0xFF070B14)          // Deep obsidian ground
val MapCanvasBgDark = Color(0xFF070B14)      // Deep obsidian ground
val MapBlockFootprint = Color(0xFF111827)    // Urban blocks elevated navy
val MapBlockFootprintDark = Color(0xFF111827)
val MapBlockStroke = Color(0xFF1E293B)       // Block subtle stroke
val MapBlockStrokeDark = Color(0xFF1E293B)

val MapAvenueWhite = Color(0xFF1F293D)       // Paved night boulevards
val MapAvenueCasing = Color(0xFF334155)      // Avenue border
val MapAvenueCasingDark = Color(0xFF334155)
val MapDividerDash = Color(0xFF64748B)

val MapParkMint = Color(0xFF064E3B)          // Oasis Tighmert deep lush mint
val MapParkMintDark = Color(0xFF064E3B)
val MapRiverAzure = Color(0xFF0C243B)        // Oued Noun deep river ribbon
val MapRiverAzureDark = Color(0xFF0C243B)

val MapRouteTraveled = Color(0xFF1E293B)     // Traveled track
val MapRouteGlowCore = AccentBlue           // Electric cobalt core
val MapRouteFuture = Color(0xFF64748B)       // Dashed remaining path
val MapBeaconPulse = AccentBlue             // Pulsing radar ripple

// ============================================================================
// --- BACKWARDS-COMPATIBLE ALIASES FOR EXISTING CODE REFERENCES ---
// ============================================================================
val ModernBlack = AppBackground
val ModernDarkGray = CardSurface
val ModernMediumGray = CardSurfaceVariant
val ModernMuted = SecondaryText
val ModernSubtle = SecondaryText
val ModernBorder = BorderStroke
val ModernSurfaceSubtle = CardSurfaceVariant
val ModernBackground = AppBackground
val ModernWhite = PrimaryText

val ModernElectricBlue = AccentBlue
val ModernEmerald = AccentEmerald
val ModernAmber = AccentAmber
val ModernCoral = Color(0xFFEF4444)

val ModernDarkBackground = AppBackground
val ModernDarkSurface = CardSurface
val ModernDarkSurfaceVariant = CardSurfaceVariant
val ModernDarkBorder = BorderStroke

val WarmOchrePrimary = AccentAmber
val WarmOchrePrimaryDark = AccentAmber
val WarmOchreContainer = CardSurfaceVariant
val WarmOchreOnContainer = PrimaryText

val DeepSandSecondary = SecondaryText
val DeepSandSecondaryDark = SecondaryText
val DeepSandContainer = CardSurfaceVariant
val DeepSandOnContainer = PrimaryText

val DesertSandLight = AppBackground
val SaharaAmber = AccentAmber
val OasisGreen = AccentEmerald
val TerracottaPrimary = AccentBlue
val TerracottaDark = AccentBlue
val SandMuted = SecondaryText
val CardSand = CardSurface
val CardSandBorder = BorderStroke
val TextPrimarySand = PrimaryText
val TextSecondarySand = SecondaryText
val StarRatingGold = AccentAmber
val DriverBadgeBg = CardSurfaceVariant
val MetricChipBg = CardSurfaceVariant
