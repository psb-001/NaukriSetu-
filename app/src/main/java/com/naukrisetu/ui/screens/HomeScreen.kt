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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.naukrisetu.engine.canonicalSkills
import com.naukrisetu.engine.gapsFor
import com.naukrisetu.engine.homeHeroCount
import com.naukrisetu.engine.seekerVisibleOpps
import com.naukrisetu.engine.splitMatches
import com.naukrisetu.ui.components.EligibleJobCard
import com.naukrisetu.ui.components.SeekerBottomTabs
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    store: DemoStore,
    onNavigate: (String) -> Unit,
    onHowToQualify: (oppId: String) -> Unit,
) {
    val seeker = store.seeker("seeker-ravi") ?: Seed.demoSeeker
    val visible = seekerVisibleOpps(store)
    val (eligible, _) = splitMatches(seeker, visible)
    val hero = homeHeroCount(seeker, visible)
    var query by remember { mutableStateOf("") }
    var chip by remember { mutableStateOf("All") }

    val filtered = eligible.filter { r ->
        (chip == "All" || r.opportunity.requiredSkills.any { it.contains(chip, ignoreCase = true) }) &&
            (query.isBlank() || r.opportunity.title.contains(query, ignoreCase = true) ||
                r.opportunity.requiredSkills.any { it.contains(query, ignoreCase = true) })
    }

    Scaffold(
        bottomBar = { SeekerBottomTabs(current = Routes.home, onNavigate = onNavigate) },
        containerColor = ThemeColors.Canvas,
    ) { pad ->
        Column(
            modifier = Modifier.fillMaxSize().padding(pad).padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text("Namaste, ${seeker.name.split(" ").firstOrNull() ?: "there"}", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("2 new matches since yesterday", fontSize = 13.sp, color = ThemeColors.InkMute)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search jobs, skills, categories…") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Electrician", "Plumber", "Carpenter", "Painter", "AC & Refrigeration", "Welder").forEach { c ->
                    AssistChip(onClick = { chip = c }, label = { Text(c) })
                }
            }
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.CanvasSoft),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("New AC-repair intake", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                    Text("Voltas service partners hiring in Pune this week", fontSize = 13.sp, color = ThemeColors.InkMute)
                    TextButton(onClick = { onNavigate(Routes.matches) }) { Text("See the role") }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$hero jobs you qualify for", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                TextButton(onClick = { onNavigate(Routes.matches) }) { Text("See all") }
            }
            filtered.take(3).forEachIndexed { i, r ->
                EligibleJobCard(ranked = r, position = i + 1, total = eligible.size, onHowToQualify = { onHowToQualify(r.opportunity.id) })
            }
            Spacer(Modifier.height(12.dp))
            val topGap = eligible.firstOrNull()?.let { gapsFor(seeker, it.opportunity).firstOrNull() }
            if (topGap != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(ThemeRadii.Card.dp),
                    border = BorderStroke(1.dp, ThemeColors.Hairline),
                    colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("You're 1 skill away from ${eligible.first().opportunity.title}", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                        Text("Missing: ${topGap.label}", fontSize = 13.sp, color = ThemeColors.InkMute)
                        TextButton(onClick = { onHowToQualify(eligible.first().opportunity.id) }) { Text("How to qualify") }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Profile 40% complete", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                    Text("2 of 5 checks done. Completing KYC unlocks 2 more jobs.", fontSize = 13.sp, color = ThemeColors.InkMute)
                    TextButton(onClick = { onNavigate(Routes.profile) }) { Text("Continue KYC") }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Courses for your gaps", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("${Seed.courses.size} courses", fontSize = 13.sp, color = ThemeColors.InkMute)
            Seed.courses.take(3).forEach { c ->
                Text("${c.code} — ${c.title} · ${c.provider} · ${c.weeks} weeks", fontSize = 13.sp, color = ThemeColors.InkSecondary)
            }
            Spacer(Modifier.height(8.dp))
            Text("Aadhaar pending verification — you can browse, some jobs stay locked.", fontSize = 12.sp, color = ThemeColors.InkMute2)
            Spacer(Modifier.height(8.dp))
            Text("Trades: ${canonicalSkills().take(6).joinToString(", ")}", fontSize = 12.sp, color = ThemeColors.InkMute2)
        }
    }
}
