package com.naukrisetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import com.naukrisetu.data.DemoStore
import com.naukrisetu.data.Seed
import com.naukrisetu.engine.canonicalSkills
import com.naukrisetu.engine.gapsFor
import com.naukrisetu.engine.homeHeroCount
import com.naukrisetu.engine.seekerVisibleOpps
import com.naukrisetu.engine.splitMatches
import com.naukrisetu.ui.components.AmberBox
import com.naukrisetu.ui.components.CompactJobCard
import com.naukrisetu.ui.components.ScoreRing
import com.naukrisetu.ui.components.SeekerTabs
import com.naukrisetu.ui.components.UnderlineLink
import com.naukrisetu.ui.icons.Lucide
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

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
        bottomBar = { SeekerTabs(current = Routes.home, onNavigate = onNavigate) },
        containerColor = ThemeColors.Canvas,
    ) { pad ->
        Column(
            modifier = Modifier.fillMaxSize().padding(pad).padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text("Namaste, ${seeker.name.split(" ").firstOrNull() ?: "there"}", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("2 new matches since yesterday", fontSize = 13.sp, color = ThemeColors.InkMute)
            Spacer(Modifier.height(12.dp))
            
            // Pill search bar with search icon
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search jobs, skills, categories…") },
                leadingIcon = { Icon(imageVector = Lucide.Search, contentDescription = null, tint = ThemeColors.InkMute, modifier = Modifier.size(20.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            
            // Selectable chips (selected = black fill)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Electrician", "Plumber", "Carpenter", "Painter", "AC & Refrigeration", "Welder").forEach { c ->
                    val selected = chip == c
                    val container = if (selected) ThemeColors.Ink else ThemeColors.CanvasSoft
                    val labelCol = if (selected) ThemeColors.Canvas else ThemeColors.Ink
                    androidx.compose.material3.Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = container),
                        border = BorderStroke(1.dp, if (selected) ThemeColors.Ink else ThemeColors.Hairline),
                        onClick = { chip = c },
                    ) {
                        Text(c, color = if (selected) ThemeColors.Canvas else ThemeColors.Ink, fontSize = 13.sp)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            
            // Banner card with illustration on right
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.CanvasSoft),
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("New AC-repair intake", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                        Text("Voltas service partners hiring in Pune this week", fontSize = 13.sp, color = ThemeColors.InkMute)
                        UnderlineLink(text = "See the role", onClick = { onNavigate(Routes.matches) })
                    }
                    // Illustration on right
                    Image(
                        painter = painterResource(com.naukrisetu.R.drawable.ill_banner_ac),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(120.dp),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            
            // Hero number + inline label + underlined See all
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$hero", fontSize = 36.sp, fontWeight = FontWeight.SemiBold, color = ThemeColors.Ink)
                    Spacer(Modifier.width(8.dp))
                    Text("jobs you qualify for", fontSize = 16.sp, color = ThemeColors.Ink)
                }
                UnderlineLink(text = "See all", onClick = { onNavigate(Routes.matches) })
            }
            Spacer(Modifier.height(8.dp))
            
            // Top 3 eligible as compact ranked rows
            filtered.take(3).forEachIndexed { i, r ->
                CompactJobCard(
                    ranked = r,
                    position = i + 1,
                    total = eligible.size,
                    onHowToQualify = { onHowToQualify(r.opportunity.id) },
                )
            }
            Spacer(Modifier.height(12.dp))
            
            // Gap card with underlined How to qualify
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
                        UnderlineLink(text = "How to qualify", onClick = { onHowToQualify(eligible.first().opportunity.id) })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            
            // KYC card with bar + small green Continue KYC + amber warning
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Profile 40% complete", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                    Text("2 of 5 checks done. Completing KYC unlocks 2 more jobs.", fontSize = 13.sp, color = ThemeColors.InkMute)
                    LinearProgressIndicator(
                        progress = 0.4f,
                        modifier = Modifier.fillMaxWidth(),
                        color = ThemeColors.Primary,
                        trackColor = ThemeColors.Hairline,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            onClick = { onNavigate(Routes.profile) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThemeColors.Primary,
                                contentColor = ThemeColors.OnPrimary,
                            ),
                            modifier = Modifier.padding(top = 8.dp),
                        ) { Text("Continue KYC", fontSize = 14.sp) }
                    }
                    AmberBox(text = "Aadhaar pending verification — you can browse, some jobs stay locked.")
                }
            }
            Spacer(Modifier.height(12.dp))
            
            // Courses horizontal strip
            Text("Courses for your gaps", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("${Seed.courses.size} courses", fontSize = 13.sp, color = ThemeColors.InkMute)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(vertical = 8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            ) {
                items(Seed.courses.take(3)) { c ->
                    CourseCard(course = c)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Aadhaar pending verification — you can browse, some jobs stay locked.", fontSize = 12.sp, color = ThemeColors.InkMute2)
        }
    }
}


@Composable
fun CourseCard(course: com.naukrisetu.engine.Course) {
    Card(
        modifier = Modifier.size(160.dp, 140.dp),
        shape = RoundedCornerShape(ThemeRadii.Card.dp),
        border = BorderStroke(1.dp, ThemeColors.Hairline),
        colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(course.code, fontSize = 11.sp, color = ThemeColors.InkMute2)
            Text(course.title, fontWeight = FontWeight.Medium, color = ThemeColors.Ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("${course.provider} · ${course.weeks} weeks", fontSize = 11.sp, color = ThemeColors.InkMute)
            Row {
                TextButton(onClick = {}) { Text("Save") }
                TextButton(onClick = {}) { Text("Notify") }
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
