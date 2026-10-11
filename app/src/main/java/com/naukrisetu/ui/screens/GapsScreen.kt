package com.naukrisetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.data.DemoStore
import com.naukrisetu.data.Seed
import com.naukrisetu.engine.gapsFor
import com.naukrisetu.engine.seekerVisibleOpps
import com.naukrisetu.engine.splitMatches
import com.naukrisetu.ui.components.SeekerTabs
import com.naukrisetu.ui.kyc.KycLogic
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@Composable
fun GapsScreen(
    oppId: String,
    store: DemoStore,
    onNavigate: (String) -> Unit,
    onOpenCourses: () -> Unit,
) {
    val seeker = store.seeker("seeker-ravi") ?: Seed.demoSeeker
    val opp = store.allOpportunities().firstOrNull { it.id == oppId }
        ?: Seed.opportunities.firstOrNull { it.id == oppId }

    Scaffold(
        bottomBar = { SeekerTabs(current = Routes.matches, onNavigate = onNavigate) },
        containerColor = ThemeColors.Canvas,
    ) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("How to qualify", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            if (opp == null) {
                Text("Pick a job from Matches to see what it needs.", fontSize = 14.sp, color = ThemeColors.InkMute)
                TextButton(onClick = { onNavigate(Routes.matches) }) { Text("Go to Matches") }
                return@Column
            }
            Text("Target", fontSize = 13.sp, color = ThemeColors.InkMute)
            Text(opp.title, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("${opp.employer} · ${opp.city}", fontSize = 13.sp, color = ThemeColors.InkMute)
            val total = opp.criteria.size + opp.requiredSkills.size
            val gaps = gapsFor(seeker, opp)
            val met = total - gaps.size
            Text("You meet $met of $total hard requirements", fontSize = 13.sp, color = ThemeColors.InkMute)
            Spacer(Modifier.height(12.dp))
            Text("What's missing", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            if (gaps.isEmpty()) {
                Text("Nothing — you qualify for this job.", fontSize = 14.sp, color = ThemeColors.Ink)
            }
            gaps.forEach { gap ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(ThemeRadii.Card.dp),
                    border = BorderStroke(1.dp, ThemeColors.Hairline),
                    colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(gap.label, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                        Text(gap.kind, fontSize = 12.sp, color = ThemeColors.InkMute2)
                        if (gap.kind == "Criterion" && gap.courses.isEmpty()) {
                            Text("Comes with time on the job — no course adds this.", fontSize = 13.sp, color = ThemeColors.InkMute)
                        }
                        gap.courses.forEach { c ->
                            Spacer(Modifier.height(8.dp))
                            Text("${c.code} — ${c.title}", fontSize = 14.sp, color = ThemeColors.Ink)
                            Text("${c.provider} · ${c.weeks} weeks", fontSize = 13.sp, color = ThemeColors.InkMute)
                            Row {
                                TextButton(onClick = {}) { Text("Save") }
                                TextButton(onClick = {}) { Text("Notify me") }
                            }
                        }
                        if ((gap.kind == "Skill" || gap.kind == "Certificate") && gap.courses.isEmpty()) {
                            Text("No course available yet — check back soon.", fontSize = 13.sp, color = ThemeColors.InkMute)
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Saving tracks it — the demo never enrolls you.", fontSize = 12.sp, color = ThemeColors.InkMute2)
            Text("Courses come from the verified catalogue — nothing is invented.", fontSize = 12.sp, color = ThemeColors.InkMute2)
            Spacer(Modifier.height(8.dp))
            // Live unlock preview: how many more jobs KYC would open.
            val (eligible, _) = splitMatches(seeker, seekerVisibleOpps(store))
            Text("You currently qualify for ${eligible.size} jobs.", fontSize = 13.sp, color = ThemeColors.InkMute)
            TextButton(onClick = onOpenCourses) { Text("Browse all courses") }
        }
    }
}

@Composable
fun CoursesScreen(
    onNavigate: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var trade by remember { mutableStateOf("All") }
    var duration by remember { mutableStateOf("Any") }

    val results = remember(query, trade, duration) {
        Seed.courses.filter { c ->
            (query.isBlank() || c.title.contains(query, ignoreCase = true) ||
                c.skillsTaught.any { it.contains(query, ignoreCase = true) } ||
                c.code.contains(query, ignoreCase = true)) &&
                (trade == "All" || c.skillsTaught.any { it.contains(trade, ignoreCase = true) }) &&
                when (duration) {
                    "<4" -> c.weeks < 4
                    "4–8" -> c.weeks in 4..8
                    "8+" -> c.weeks > 8
                    else -> true
                }
        }.sortedBy { it.title }
    }

    Scaffold(
        bottomBar = { SeekerTabs(current = Routes.courses, onNavigate = onNavigate) },
        containerColor = ThemeColors.Canvas,
    ) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad).padding(16.dp)) {
            Text("Courses", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("From the verified catalogue — never invented.", fontSize = 13.sp, color = ThemeColors.InkMute)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search courses or skills — try “wiring”") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Text("Skills taught are matched through the standard taxonomy — “wiring” finds ELEC-101.", fontSize = 12.sp, color = ThemeColors.InkMute2)
            Row {
                TextButton(onClick = { trade = "All" }) { Text("Trade: $trade", color = if (trade == "All") ThemeColors.Primary else ThemeColors.InkMute) }
                TextButton(onClick = {
                    duration = when (duration) {
                        "Any" -> "<4"; "<4" -> "4–8"; "4–8" -> "8+"; else -> "Any"
                    }
                }) { Text(if (duration == "Any") "Duration: any" else "$duration wks") }
                TextButton(onClick = {}) { Text("Sort: A–Z") }
            }
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                results.forEach { c ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(ThemeRadii.Card.dp),
                        border = BorderStroke(1.dp, ThemeColors.Hairline),
                        colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(c.code, fontSize = 12.sp, color = ThemeColors.InkMute2)
                            Text(c.title, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                            Text("${c.provider} · ${c.weeks} weeks", fontSize = 13.sp, color = ThemeColors.InkMute)
                            Row {
                                TextButton(onClick = {}) { Text("Save") }
                                TextButton(onClick = {}) { Text("Notify me") }
                            }
                        }
                    }
                }
            }
        }
    }
}
