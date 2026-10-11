package com.naukrisetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.data.DemoStore
import com.naukrisetu.data.Seed
import com.naukrisetu.engine.gapsFor
import com.naukrisetu.engine.homeHeroCount
import com.naukrisetu.engine.seekerVisibleOpps
import com.naukrisetu.engine.splitMatches
import com.naukrisetu.ui.components.AmberBox
import com.naukrisetu.ui.components.CompactJobCard
import com.naukrisetu.ui.components.SeekerTabs
import com.naukrisetu.ui.components.UnderlineLink
import com.naukrisetu.ui.icons.Lucide
import com.naukrisetu.ui.kyc.KycLogic
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@Composable
fun HomeScreen(
    store: DemoStore,
    onNavigate: (String) -> Unit,
    onHowToQualify: (oppId: String) -> Unit,
) {
    var refreshTrigger by remember { mutableStateOf(0) }
    val seeker = remember(refreshTrigger) { store.currentSeeker() ?: Seed.demoSeeker }
    val visible = remember(refreshTrigger) { seekerVisibleOpps(store) }
    val (eligible, _) = remember(refreshTrigger, seeker) { splitMatches(seeker, visible) }
    val hero = remember(refreshTrigger, seeker) { homeHeroCount(seeker, visible) }
    val kycPct = KycLogic.progressPct(seeker)
    val kycDone = KycLogic.checksDone(seeker)
    var query by remember { mutableStateOf("") }
    var chip by remember { mutableStateOf("All") }
    var applyFeedback by remember { mutableStateOf<String?>(null) }

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
            Text("$hero new matches qualified by RozgarLink engine", fontSize = 13.sp, color = ThemeColors.InkMute)
            Spacer(Modifier.height(10.dp))

            // RozgarLink G-01 Engine Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.CanvasSoft),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
            ) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = ThemeColors.Primary,
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "ROZGARLINK G-01 MATCHING ENGINE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ThemeColors.Primary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${seeker.name} · ${if (seeker.experienceYears == 0) "Student / Fresher friendly" else "${seeker.experienceYears}y exp (Employee)"} · ${seeker.city.ifBlank { "Pune" }}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ThemeColors.Ink,
                            modifier = Modifier.weight(1f),
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (seeker.experienceYears == 0) ThemeColors.PrimarySoft else ThemeColors.Canvas,
                            border = BorderStroke(1.dp, if (seeker.experienceYears == 0) ThemeColors.Primary else ThemeColors.Hairline),
                            modifier = Modifier.clickable {
                                val nextYears = if (seeker.experienceYears == 0) 3 else 0
                                store.upsertSeeker(seeker.copy(experienceYears = nextYears))
                                refreshTrigger++
                            }
                        ) {
                            Text(
                                text = if (seeker.experienceYears == 0) "💼 Switch to Employee" else "🎓 Switch to Student",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (seeker.experienceYears == 0) ThemeColors.PrimaryDeep else ThemeColors.Ink,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        seeker.skills.take(6).forEach { sk ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ThemeColors.Canvas,
                                border = BorderStroke(1.dp, ThemeColors.Hairline),
                            ) {
                                Text(
                                    text = "⚡ $sk",
                                    fontSize = 11.sp,
                                    color = ThemeColors.Ink,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
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

            // Selectable chips (selected = brand primary fill, unselected = soft clean pill)
            if (applyFeedback != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ThemeColors.PrimarySoft,
                    border = BorderStroke(1.dp, ThemeColors.Primary),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = applyFeedback!!,
                            color = ThemeColors.PrimaryDeep,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        TextButton(onClick = { applyFeedback = null }) {
                            Text("Dismiss", fontSize = 11.sp, color = ThemeColors.PrimaryDeep)
                        }
                    }
                }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Android", "Python", "Web Dev", "Full Stack", "Data & AI", "UI/UX Design").forEach { c ->
                    val selected = chip == c
                    Card(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) ThemeColors.Primary else ThemeColors.CanvasSoft,
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (selected) ThemeColors.Primary else ThemeColors.Hairline,
                        ),
                        onClick = { chip = c },
                    ) {
                        Text(
                            text = c,
                            color = if (selected) Color.White else ThemeColors.InkSecondary,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // Banner card with live top job + illustration
            if (eligible.isNotEmpty()) {
                val topOpp = eligible.first().opportunity
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(ThemeRadii.Card.dp),
                    border = BorderStroke(1.dp, ThemeColors.Hairline),
                    colors = CardDefaults.cardColors(containerColor = ThemeColors.CanvasSoft),
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(topOpp.title, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                            Text("${topOpp.employer} · ${topOpp.city}", fontSize = 13.sp, color = ThemeColors.InkMute)
                            UnderlineLink(text = "See the role", onClick = { onHowToQualify(topOpp.id) })
                        }
                        Image(
                            painter = painterResource(com.naukrisetu.R.drawable.ill_banner_ac),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(120.dp),
                        )
                    }
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
            if (filtered.isNotEmpty()) {
                filtered.take(3).forEachIndexed { i, r ->
                    var isApplied by remember(r.opportunity.id) {
                        mutableStateOf(store.isJobApplied(r.opportunity.id))
                    }
                    CompactJobCard(
                        ranked = r,
                        position = i + 1,
                        total = eligible.size,
                        seeker = seeker,
                        isApplied = isApplied,
                        onApply = {
                            store.applyJob(r.opportunity.id)
                            isApplied = true
                            applyFeedback = "✓ Applied for ${r.opportunity.title}!"
                        },
                        onHowToQualify = { onHowToQualify(r.opportunity.id) },
                    )
                }
            } else if (eligible.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(ThemeRadii.Card.dp),
                    border = BorderStroke(1.dp, ThemeColors.Hairline),
                    colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text("No matches for filter '$chip'", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                        Text("Switch to 'All' to see your ${eligible.size} qualified opportunities.", fontSize = 12.sp, color = ThemeColors.InkMute)
                        TextButton(onClick = { chip = "All" }) { Text("Show All Matches") }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(ThemeRadii.Card.dp),
                    border = BorderStroke(1.dp, ThemeColors.Hairline),
                    colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text("0 direct matches for current criteria", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                        Text("Opportunities are filtered by city (${seeker.city.ifBlank { "Pune" }}) and mandatory criteria. Update your profile or explore unlockable roles.", fontSize = 12.sp, color = ThemeColors.InkMute)
                        UnderlineLink(text = "View all jobs and why they failed gates →", onClick = { onNavigate(Routes.matches) })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // Unlocked skills card
            val topGap = eligible.firstOrNull()?.let { gapsFor(seeker, it.opportunity).firstOrNull() }
            if (topGap != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(ThemeRadii.Card.dp),
                    border = BorderStroke(1.dp, ThemeColors.Hairline),
                    colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("⚡ Bridge 1 Skill: ${eligible.first().opportunity.title}", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                        Text("Missing: ${topGap.label} · Bridging this moves you to top candidate rank.", fontSize = 13.sp, color = ThemeColors.InkMute)
                        UnderlineLink(text = "How to bridge & qualify →", onClick = { onHowToQualify(eligible.first().opportunity.id) })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // Student Verified Profile status card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.CanvasSoft),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = ThemeColors.Primary,
                            modifier = Modifier.size(10.dp)
                        ) {}
                        Spacer(Modifier.width(8.dp))
                        Text("Campus Verified Student Profile", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = ThemeColors.Ink)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${seeker.name} · ${seeker.certificates.firstOrNull() ?: "B.Tech Computer Science"}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ThemeColors.Ink
                    )
                    Text(
                        "All ${eligible.size} student fresher and internship opportunities are unlocked.",
                        fontSize = 12.sp,
                        color = ThemeColors.InkMute
                    )
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = { onNavigate(Routes.profile) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ThemeColors.Primary,
                            contentColor = Color.White,
                        ),
                        shape = RoundedCornerShape(ThemeRadii.Button.dp),
                    ) { Text("View & Edit Student Profile", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
            Spacer(Modifier.height(12.dp))
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
