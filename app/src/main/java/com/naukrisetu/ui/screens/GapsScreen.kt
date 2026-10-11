package com.naukrisetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.data.DemoStore
import com.naukrisetu.engine.gapsFor
import com.naukrisetu.ui.components.SeekerTabs
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GapsScreen(
    oppId: String,
    store: DemoStore,
    onNavigate: (String) -> Unit,
    onOpenCourses: () -> Unit = {},
) {
    val seeker = store.currentSeeker() ?: com.naukrisetu.data.Seed.demoSeeker
    val opportunity = store.opportunity(oppId) ?: com.naukrisetu.data.Seed.opportunities.first()
    val gaps = gapsFor(seeker, opportunity)

    Scaffold(
        bottomBar = { SeekerTabs(current = Routes.matches, onNavigate = onNavigate) },
        containerColor = ThemeColors.Canvas,
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text("Skill & Eligibility Gap Analysis", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = ThemeColors.Ink)
            Spacer(Modifier.height(4.dp))
            Text("Target Role: ${opportunity.title}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("${opportunity.employer} · ${opportunity.city}", fontSize = 13.sp, color = ThemeColors.InkMute)

            Spacer(Modifier.height(16.dp))

            // What's missing
            if (gaps.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ThemeColors.CanvasSoft),
                    border = BorderStroke(1.dp, ThemeColors.Primary),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("🎉 You meet all criteria!", fontWeight = FontWeight.Bold, color = ThemeColors.Ink)
                        Text("You can apply or be ranked at top priority for this opportunity.", fontSize = 13.sp, color = ThemeColors.InkMute)
                    }
                }
            } else {
                Text("What's missing for this role:", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = ThemeColors.Ink)
                Spacer(Modifier.height(8.dp))

                gaps.forEach { gap ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = ThemeColors.CanvasSoft),
                        border = BorderStroke(1.dp, ThemeColors.Hairline),
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = ThemeColors.Warning,
                                    modifier = Modifier.size(8.dp),
                                ) {}
                                Spacer(Modifier.width(8.dp))
                                Text(gap.label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ThemeColors.Ink)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Type: ${gap.kind} · Add this project/skill to your profile or pass assessment to unlock.",
                                fontSize = 12.sp,
                                color = ThemeColors.InkMute,
                            )
                            if (gap.courses.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = ThemeColors.PrimarySoft,
                                    border = BorderStroke(1.dp, ThemeColors.Primary),
                                ) {
                                    Column(Modifier.padding(10.dp)) {
                                        Text(
                                            "📚 Free bridge to unlock",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ThemeColors.PrimaryDeep,
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        gap.courses.take(3).forEach { c ->
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
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Candidate current skills
            Text("Your current skills:", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Spacer(Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                seeker.skills.forEach { sk ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ThemeColors.CanvasSoft,
                        border = BorderStroke(1.dp, ThemeColors.Hairline),
                    ) {
                        Text(
                            "✓ $sk",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ThemeColors.Ink,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            var applied by remember(opportunity.id) {
                mutableStateOf(store.isJobApplied(opportunity.id))
            }

            Button(
                onClick = {
                    store.applyJob(opportunity.id)
                    applied = true
                },
                enabled = !applied,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(ThemeRadii.Button.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (applied) ThemeColors.PrimarySoft else ThemeColors.Primary,
                    contentColor = if (applied) ThemeColors.PrimaryDeep else Color.White,
                    disabledContainerColor = ThemeColors.PrimarySoft,
                    disabledContentColor = ThemeColors.PrimaryDeep,
                ),
            ) {
                Text(
                    text = if (applied) "✓ Application Submitted" else "1-Tap Apply to ${opportunity.employer}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = { onNavigate(Routes.matches) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Button.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ThemeColors.CanvasSoft,
                    contentColor = ThemeColors.Ink,
                ),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
            ) {
                Text("Back to All Matches", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun CoursesScreen(
    onNavigate: (String) -> Unit,
) {
    GapsScreen(
        oppId = "",
        store = com.naukrisetu.data.DemoStore(),
        onNavigate = onNavigate,
    )
}
