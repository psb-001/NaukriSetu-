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
import com.naukrisetu.engine.OppStatus
import com.naukrisetu.ui.components.VerifierTabs
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@Composable
fun VerifierScreen(
    store: DemoStore,
    onNavigate: (String) -> Unit,
) {
    var tab by remember { mutableStateOf(0) }
    var rejectNote by remember { mutableStateOf("") }
    var rejectingId by remember { mutableStateOf<String?>(null) }
    val seeker = store.seeker("seeker-ravi") ?: Seed.demoSeeker

    // KYC docs queue: derived from the demo seeker (pending items).
    val kycDocs = buildList {
        if (seeker.aadhaar == null) add(Triple("Ravi Kadam", "Aadhaar card", "2 min ago"))
        if (!seeker.addressProof) add(Triple("Ravi Kadam", "Address proof", "just now"))
        add(Triple("Meena Iyer", "Aadhaar card", "18 min ago"))
        add(Triple("Suresh Pawar", "Certificate: ITI Electrician", "1 hr ago"))
    }
    val pendingPosts = store.allOpportunities().filter { it.status == OppStatus.Pending }
    val decidedPosts = store.allOpportunities().filter { it.status != OppStatus.Pending }

    Scaffold(
        bottomBar = { VerifierTabs(current = Routes.kycQueue, onNavigate = onNavigate) },
        containerColor = ThemeColors.Canvas,
    ) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad).padding(16.dp)) {
            Text("Verification queue", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("One trust pattern for people and jobs.", fontSize = 13.sp, color = ThemeColors.InkMute)
            Spacer(Modifier.height(8.dp))
            Row {
                TextButton(onClick = { tab = 0 }) {
                    Text("KYC docs (${kycDocs.size})", color = if (tab == 0) ThemeColors.Primary else ThemeColors.InkMute)
                }
                TextButton(onClick = { tab = 1 }) {
                    Text("Jobs (${pendingPosts.size})", color = if (tab == 1) ThemeColors.Primary else ThemeColors.InkMute)
                }
            }
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (tab == 0) {
                    Text("Approvals update that seeker's matches immediately.", fontSize = 13.sp, color = ThemeColors.InkMute)
                    kycDocs.forEach { (name, doc, ago) ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(ThemeRadii.Card.dp),
                            border = BorderStroke(1.dp, ThemeColors.Hairline),
                            colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(name, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                                Text("$doc · $ago", fontSize = 13.sp, color = ThemeColors.InkMute)
                                Row {
                                    TextButton(onClick = {}) { Text("Approve") }
                                    TextButton(onClick = {}) { Text("Reject") }
                                }
                            }
                        }
                    }
                    Text("Demo data — nothing leaves this device.", fontSize = 12.sp, color = ThemeColors.InkMute2)
                } else {
                    Text("Approve once — the post is live for every seeker instantly.", fontSize = 13.sp, color = ThemeColors.InkMute)
                    pendingPosts.forEach { opp ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(ThemeRadii.Card.dp),
                            border = BorderStroke(1.dp, ThemeColors.Hairline),
                            colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(opp.employer, fontSize = 13.sp, color = ThemeColors.InkMute)
                                Text(opp.title, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                                Text("${opp.criteria.size} criteria · pay ${opp.payMin}–${opp.payMax} · ${opp.city}", fontSize = 13.sp, color = ThemeColors.InkMute)
                                if (rejectingId == opp.id) {
                                    OutlinedTextField(
                                        value = rejectNote,
                                        onValueChange = { rejectNote = it },
                                        label = { Text("Reason (required to reject)") },
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    Row {
                                        TextButton(onClick = {
                                            if (rejectNote.isNotBlank()) {
                                                store.rejectPost(opp.id)
                                                rejectingId = null
                                                rejectNote = ""
                                            }
                                        }) { Text("Reject with note") }
                                        TextButton(onClick = { rejectingId = null }) { Text("Cancel") }
                                    }
                                } else {
                                    Row {
                                        TextButton(onClick = { store.approvePost(opp.id) }) { Text("Approve") }
                                        TextButton(onClick = { rejectingId = opp.id }) { Text("Reject") }
                                    }
                                }
                                TextButton(onClick = {}) { Text("View full post") }
                            }
                        }
                    }
                    if (decidedPosts.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("Approved today — ${decidedPosts.count { it.status == OppStatus.Live }} live", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                        decidedPosts.forEach { opp ->
                            Text(
                                "• ${opp.title} — ${opp.status}${if (opp.status == OppStatus.Rejected) " (re-submit after edit)" else " · appears in seeker matches instantly"}",
                                fontSize = 13.sp,
                                color = ThemeColors.InkSecondary,
                            )
                        }
                    }
                    Text("Pending and rejected posts never appear in seeker matches — enforced in the engine.", fontSize = 12.sp, color = ThemeColors.InkMute2)
                }
            }
        }
    }
}
