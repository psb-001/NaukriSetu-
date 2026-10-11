package com.naukrisetu.ui.components
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.engine.Ranked
import com.naukrisetu.ui.components.ScoreRing
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@Composable
fun CompactJobCard(
    ranked: com.naukrisetu.engine.Ranked,
    position: Int,
    total: Int,
    onHowToQualify: () -> Unit,
) {
    val opp = ranked.opportunity
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(ThemeRadii.Card.dp),
        border = BorderStroke(1.dp, ThemeColors.Hairline),
        colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ScoreRing(score = ranked.score, size = 36.dp, stroke = 4.dp, fontSize = 11)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(opp.title, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                Text("${opp.employer} · ${opp.city}", fontSize = 12.sp, color = ThemeColors.InkMute)
                Text("#$position of $total", fontSize = 11.sp, color = ThemeColors.InkMute2)
                Text("${opportunityPay(opp)} · ${"%.1f".format(opp.distanceKm)} km", fontSize = 12.sp, color = ThemeColors.Ink)
                Text(ranked.reason, fontSize = 12.sp, color = ThemeColors.InkSecondary)
            }
            androidx.compose.material3.Icon(
                imageVector = com.naukrisetu.ui.icons.Lucide.ChevronRight,
                contentDescription = null,
                tint = ThemeColors.InkMute,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

private fun opportunityPay(opp: com.naukrisetu.engine.Opportunity): String {
    val s = opp.payMin.toString()
    val out = StringBuilder()
    var count = 0
    for (i in s.length - 1 downTo 0) {
        out.append(s[i])
        count++
        if (count == 3 && i != 0) { out.append(','); count = -100 }
        else if (count > 3 && (count - 3) % 2 == 0 && i != 0) out.append(',')
    }
    return "₹" + out.reverse().toString() + "/mo"
}