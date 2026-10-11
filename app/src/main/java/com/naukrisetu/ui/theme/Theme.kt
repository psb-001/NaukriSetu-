package com.naukrisetu.ui.theme

import androidx.compose.ui.graphics.Color

/** Modern, friendly, UI-focused theme tokens. */
object ThemeColors {
    val Primary = Color(0xFF10B981) // Modern emerald
    val PrimaryDeep = Color(0xFF059669)
    val PrimarySoft = Color(0xFFECFDF5) // Soft green tint
    val OnPrimary = Color(0xFFFFFFFF) // Crisp white text on primary
    val Ink = Color(0xFF0F172A) // Modern slate-900 (not harsh solid black)
    val InkSecondary = Color(0xFF334155) // Slate-700
    val InkMute = Color(0xFF64748B) // Slate-500
    val InkMute2 = Color(0xFF94A3B8) // Slate-400
    val Canvas = Color(0xFFFFFFFF)
    val CanvasSoft = Color(0xFFF8FAFC) // Slate-50 clean soft surface
    val Hairline = Color(0xFFE2E8F0) // Slate-200 hairline
    val HairlineStrong = Color(0xFFCBD5E1) // Slate-300
    val Danger = Color(0xFFEF4444)
    val Warning = Color(0xFFF59E0B)
}

/** Corner radii: modern rounded ergonomics. */
object ThemeRadii {
    val Button = 10
    val Card = 14
}
