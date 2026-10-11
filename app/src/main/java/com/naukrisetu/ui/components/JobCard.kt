package com.naukrisetu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.engine.Ranked
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

@Composable
fun EligibleJobCard(
    ranked: Ranked,
    position: Int,
    total: Int,
    onHowToQualify: () -> Unit,
    onSave: (() -> Unit)? = null,
    isSaved: Boolean = false,
) {
    val opp = ranked.opportunity
    var expanded by remember { mutableStateOf(false) }
    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(ThemeRadii.Card.dp),
        border = BorderStroke(1.dp, ThemeColors.Hairline),
        colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            ScoreRing(ranked.score)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(opp.title, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                Text("${opp.employer} · ${opp.city}", fontSize = 13.sp, color = ThemeColors.InkMute)
                Text("#$position of $total", fontSize = 12.sp, color = ThemeColors.InkMute2)
                Text("${inr(opp.payMin)}/mo · ${"%.1f".format(opp.distanceKm)} km", fontSize = 13.sp, color = ThemeColors.Ink)
                Text(ranked.reason, fontSize = 13.sp, color = ThemeColors.InkSecondary)
                if (expanded) {
                    Spacer(Modifier.height(8.dp))
                    Text("Skill match ${ranked.skillPct}%", fontSize = 13.sp, color = ThemeColors.Ink)
                    LinearProgressIndicator(
                        progress = { ranked.skillPct / 100f },
                        modifier = Modifier.fillMaxWidth(),
                        color = ThemeColors.Primary,
                        trackColor = ThemeColors.Hairline,
                    )
                    Text("Commute ${"%.1f".format(opp.distanceKm)} km", fontSize = 13.sp, color = ThemeColors.Ink)
                    Text("Pay ${inr(opp.payMax)} vs minimum", fontSize = 13.sp, color = ThemeColors.Ink)
                    Row {
                        TextButton(onClick = onHowToQualify) { Text("How to qualify") }
                        TextButton(onClick = { onSave?.invoke() }) {
                            Text(if (isSaved) "Saved ✓" else "Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreRing(score: Int) {
    Card(
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(2.dp, ThemeColors.Primary),
        colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
        modifier = Modifier.size(52.dp),
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("$score", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
        }
    }
}

@Composable
fun IneligibleJobCard(
    title: String,
    employer: String,
    city: String,
    payMin: Int,
    reasons: List<String>,
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
            reasons.forEach { r -> Text(r, fontSize = 13.sp, color = ThemeColors.InkSecondary) }
            TextButton(onClick = onHowToQualify) { Text("How to qualify →") }
        }
    }
}
