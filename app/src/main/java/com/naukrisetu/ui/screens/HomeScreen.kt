package com.naukrisetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
    val seeker = store.currentSeeker() ?: Seed.demoSeeker
    val visible = seekerVisibleOpps(store)
    val (eligible, _) = splitMatches(seeker, visible)
    val hero = homeHeroCount(seeker, visible)
    val kycPct = KycLogic.progressPct(seeker)
    val kycDone = KycLogic.checksDone(seeker)
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
                    Text(
                        text = "${seeker.name} · ${if (seeker.experienceYears == 0) "Student / Fresher friendly" else "${seeker.experienceYears}y experience"} · ${seeker.city.ifBlank { "Pune" }}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ThemeColors.Ink
                    )
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

            // Selectable chips (selected = black fill)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Android", "Python", "Web Dev", "Full Stack", "Data & AI", "UI/UX Design").forEach { c ->
                    val selected = chip == c
                    Card(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = if (selected) ThemeColors.Ink else ThemeColors.CanvasSoft),
                        border = BorderStroke(1.dp, if (selected) ThemeColors.Ink else ThemeColors.Hairline),
                        onClick = { chip = c },
                    ) {
                        Text(c, color = if (selected) ThemeColors.Canvas else ThemeColors.Ink, fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
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
                    CompactJobCard(
                        ranked = r,
                        position = i + 1,
                        total = eligible.size,
                        seeker = seeker,
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

            // KYC card with real live progress bar + amber warning
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Profile $kycPct% complete", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                    Text("$kycDone of 5 checks done. Completing KYC unlocks verified employer listings.", fontSize = 13.sp, color = ThemeColors.InkMute)
                    LinearProgressIndicator(
                        progress = { kycPct / 100f },
                        modifier = Modifier.fillMaxWidth(),
                        color = ThemeColors.Primary,
                        trackColor = ThemeColors.Hairline,
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { onNavigate(Routes.profile) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ThemeColors.Primary,
                            contentColor = ThemeColors.OnPrimary,
                        ),
                        modifier = Modifier.padding(top = 4.dp),
                    ) { Text("Continue KYC", fontSize = 14.sp) }
                    AmberBox(text = "Aadhaar pending verification — you can browse, some jobs stay locked.")
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
