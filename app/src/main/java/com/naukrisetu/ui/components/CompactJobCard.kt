package com.naukrisetu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.engine.Ranked
import com.naukrisetu.engine.Seeker
import com.naukrisetu.engine.SkillTaxonomy
import com.naukrisetu.ui.components.ScoreRing
import com.naukrisetu.ui.icons.Lucide
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompactJobCard(
    ranked: Ranked,
    position: Int,
    total: Int,
    seeker: Seeker? = null,
    isApplied: Boolean = false,
    onApply: () -> Unit = {},
    onHowToQualify: () -> Unit,
) {
    val opp = ranked.opportunity
    val candidateSkills = seeker?.skills.orEmpty()

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        shape = RoundedCornerShape(ThemeRadii.Card.dp),
        border = BorderStroke(1.dp, ThemeColors.Hairline),
        colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                ScoreRing(score = ranked.score, size = 44.dp, stroke = 4.dp, fontSize = 13)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(opp.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = ThemeColors.Ink)
                    Text("${opp.employer} · ${opp.city}", fontSize = 12.sp, color = ThemeColors.InkMute)
                    Text("#$position of $total · ${opportunityPay(opp)} · ${"%.1f".format(opp.distanceKm)} km", fontSize = 11.sp, color = ThemeColors.InkMute2)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Semantic Skill Matching Badges (G-01 NaukriSetu engine evidence)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                opp.requiredSkills.forEach { req ->
                    val isMatched = candidateSkills.any { cSkill ->
                        SkillTaxonomy.matchScore(cSkill, req) >= 0.70f
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isMatched) ThemeColors.PrimarySoft else ThemeColors.CanvasSoft,
                        border = BorderStroke(1.dp, if (isMatched) ThemeColors.Primary else ThemeColors.Hairline),
                    ) {
                        Text(
                            text = if (isMatched) "✓ $req" else req,
                            fontSize = 11.sp,
                            fontWeight = if (isMatched) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isMatched) ThemeColors.PrimaryDeep else ThemeColors.InkMute,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))
            Text(
                text = "⚡ Engine: " + ranked.reason,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = ThemeColors.PrimaryDeep
            )

            Spacer(Modifier.height(8.dp))

            // Action Row: Gap Breakdown Link + 1-Tap Apply Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onHowToQualify) {
                    Text("Fit Analysis →", fontSize = 12.sp, color = ThemeColors.InkMute)
                }

                Button(
                    onClick = onApply,
                    enabled = !isApplied,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isApplied) ThemeColors.PrimarySoft else ThemeColors.Primary,
                        contentColor = if (isApplied) ThemeColors.PrimaryDeep else Color.White,
                        disabledContainerColor = ThemeColors.PrimarySoft,
                        disabledContentColor = ThemeColors.PrimaryDeep,
                    ),
                    modifier = Modifier.height(34.dp),
                ) {
                    Text(
                        text = if (isApplied) "✓ Applied" else "Apply Now",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
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
