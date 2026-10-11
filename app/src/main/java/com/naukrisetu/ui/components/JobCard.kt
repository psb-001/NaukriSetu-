package com.naukrisetu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.engine.Course
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

private fun inr(n: Int): String {
    val s = n.toString()
    val out = StringBuilder()
    var count = 0
    for (i in s.length - 1 downTo 0) {
        out.append(s[i])
        count++
        if (count == 3 && i != 0) { out.append(','); count = -100 }
        else if (count > 3 && (count - 3) % 2 == 0 && i != 0) out.append(',')
    }
    return "₹" + out.reverse().toString()
}

/**
 * Locked role card — shows the exact hard-gate failure reasons plus the
 * free learning bridge that unlocks it (G-01: courses are bridge pathways,
 * never a standalone platform).
 */
@Composable
fun IneligibleJobCard(
    title: String,
    employer: String,
    city: String,
    payMin: Int,
    reasons: List<String>,
    courses: List<Course> = emptyList(),
    onHowToQualify: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(ThemeRadii.Card.dp),
        border = BorderStroke(1.dp, ThemeColors.Danger),
        colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("$employer · $city", fontSize = 13.sp, color = ThemeColors.InkMute)
            Text("Not eligible", fontSize = 12.sp, color = ThemeColors.Danger)
            Text(inr(payMin) + "/mo", fontSize = 13.sp, color = ThemeColors.Ink)
            reasons.forEach { r -> Text("✕ $r", fontSize = 13.sp, color = ThemeColors.InkSecondary) }

            if (courses.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ThemeColors.PrimarySoft,
                    border = BorderStroke(1.dp, ThemeColors.Primary),
                ) {
                    Column(Modifier.padding(10.dp)) {
                        Text(
                            "📚 Unlock this role — free learning path",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ThemeColors.PrimaryDeep,
                        )
                        Spacer(Modifier.height(4.dp))
                        courses.take(3).forEach { c ->
                            Text(
                                "• ${c.title}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ThemeColors.Ink,
                            )
                            Text(
                                "  ${c.provider} · ~${c.weeks} wk · free",
                                fontSize = 11.sp,
                                color = ThemeColors.InkMute,
                            )
                        }
                    }
                }
            }

            TextButton(onClick = onHowToQualify) { Text("How to qualify →") }
        }
    }
}
