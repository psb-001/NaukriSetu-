package com.naukrisetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.naukrisetu.data.DemoStore
import com.naukrisetu.data.Seed
import com.naukrisetu.engine.seekerVisibleOpps
import com.naukrisetu.engine.splitMatches
import com.naukrisetu.ui.components.CompactJobCard
import com.naukrisetu.ui.components.IneligibleJobCard
import com.naukrisetu.ui.components.SeekerTabs
import com.naukrisetu.ui.components.CompactJobCard
import com.naukrisetu.ui.components.IneligibleJobCard
import com.naukrisetu.ui.components.UnderlineLink
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors

@Composable
fun MatchesScreen(
    store: DemoStore,
    onNavigate: (String) -> Unit,
    onHowToQualify: (oppId: String) -> Unit,
) {
    val seeker = store.seeker("seeker-ravi") ?: Seed.demoSeeker
    val (eligible, ineligible) = splitMatches(seeker, seekerVisibleOpps(store))
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = { SeekerTabs(current = Routes.matches, onNavigate = onNavigate) },
        containerColor = ThemeColors.Canvas,
    ) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad).padding(16.dp)) {
            Text("Your matches", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text(
                if (tab == 0) "Hard requirements checked first — only survivors are ranked."
                else "Hard requirements checked first — fix one to move a job into Eligible.",
                fontSize = 13.sp,
                color = ThemeColors.InkMute,
            )
            Spacer(Modifier.height(8.dp))
            // Underline segment tabs (per M08/M09)
            Row {
                TextButton(onClick = { tab = 0 }) {
                    Text(
                        "Eligible (${eligible.size})",
                        fontWeight = if (tab == 0) FontWeight.Medium else null,
                        color = if (tab == 0) ThemeColors.Primary else ThemeColors.InkMute,
                        style = androidx.compose.ui.text.TextStyle(
                            textDecoration = if (tab == 0) androidx.compose.ui.text.style.TextDecoration.Underline else null,
                        ),
                    )
                }
                TextButton(onClick = { tab = 1 }) {
                    Text(
                        "Not eligible (${ineligible.size})",
                        fontWeight = if (tab == 1) FontWeight.Medium else null,
                        color = if (tab == 1) ThemeColors.Danger else ThemeColors.InkMute,
                        style = androidx.compose.ui.text.TextStyle(
                            textDecoration = if (tab == 1) androidx.compose.ui.text.style.TextDecoration.Underline else null,
                        ),
                    )
                }
            }
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (tab == 0) {
                    if (eligible.isEmpty()) {
                        // M17 - Eligible empty with illustration
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(com.naukrisetu.R.drawable.ill_empty_search),
                            contentDescription = null,
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                            modifier = Modifier.fillMaxWidth().aspectRatio(2048f / 1434f).padding(vertical = 32.dp),
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("Complete KYC to unlock matches", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                        Text("You have 0 of 5 KYC checks done. Verified documents unlock the jobs you already qualify for.", fontSize = 13.sp, color = ThemeColors.InkMute)
                        androidx.compose.material3.Button(
                            onClick = { onNavigate(Routes.profile) },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = ThemeColors.Primary, contentColor = ThemeColors.OnPrimary),
                        ) { Text("Complete KYC") }
                        androidx.compose.material3.TextButton(onClick = { onNavigate(Routes.courses) }) { Text("Browse courses meanwhile") }
                    } else {
                        Text("Ranked by fit score. Tap a card for the full breakdown.", fontSize = 13.sp, color = ThemeColors.InkMute)
                        eligible.forEachIndexed { i, r ->
                            CompactJobCard(
                                ranked = r,
                                position = i + 1,
                                total = eligible.size,
                                onHowToQualify = { onHowToQualify(r.opportunity.id) },
                            )
                        }
                    }
                } else {
                    if (ineligible.isEmpty()) {
                        // M18 - Not eligible empty
                        Text("You qualify for everything — nice.", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                        Text("Every live job's hard requirements pass with your current profile. New posts land here only if they ask for something you don't have.", fontSize = 13.sp, color = ThemeColors.InkMute)
                        androidx.compose.material3.TextButton(onClick = { tab = 0 }) { Text("See your ${eligible.size} eligible matches") }
                    } else {
                        Text("These jobs list the exact conditions that failed.", fontSize = 13.sp, color = ThemeColors.InkMute)
                        ineligible.forEach { (opp, reasons) ->
                            IneligibleJobCard(
                                title = opp.title,
                                employer = opp.employer,
                                city = opp.city,
                                payMin = opp.payMin,
                                reasons = reasons,
                                onHowToQualify = { onHowToQualify(opp.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}