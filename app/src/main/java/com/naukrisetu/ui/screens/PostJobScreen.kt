package com.naukrisetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.data.DemoStore
import com.naukrisetu.engine.Criterion
import com.naukrisetu.engine.OppStatus
import com.naukrisetu.engine.Opportunity
import com.naukrisetu.engine.seekerVisibleOpps
import com.naukrisetu.ui.components.EmployerTabs
import com.naukrisetu.ui.icons.Lucide
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostJobScreen(
    store: DemoStore,
    onNavigate: (String) -> Unit,
    onSubmitted: () -> Unit,
) {
    val employerId = store.currentSeeker()?.email ?: "officer.nic@gov.in"
    var orgName by remember { mutableStateOf("National Informatics Centre (NIC)") }
    var title by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Pune") }
    var minExp by remember { mutableStateOf("0") }
    var payAmount by remember { mutableStateOf("30000") }
    var workDescription by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf(listOf("Python", "SQL", "Git")) }
    var skillInput by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf<String?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val presetSkills = listOf("Python", "Android", "Kotlin", "React", "JavaScript", "SQL", "Git", "Figma", "Problem Solving", "Node.js")

    Scaffold(
        bottomBar = { EmployerTabs(current = Routes.postJob, onNavigate = onNavigate) },
        containerColor = ThemeColors.Canvas,
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        "Govt & Corporate Job Adder",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ThemeColors.Ink,
                    )
                    Text(
                        "Add opportunities directly to the NaukriSetu matching engine",
                        fontSize = 13.sp,
                        color = ThemeColors.InkMute,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Notice Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.PrimarySoft),
                border = BorderStroke(1.dp, ThemeColors.Primary),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Lucide.PlusCircle,
                        contentDescription = null,
                        tint = ThemeColors.PrimaryDeep,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Jobs published here go Live instantly for matching students who pass your eligibility gates.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ThemeColors.PrimaryDeep,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Form Section 1: Organization & Role
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("1. Department / Company Details", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ThemeColors.Ink)
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = orgName,
                        onValueChange = { orgName = it },
                        label = { Text("Organization / Department Name") },
                        placeholder = { Text("e.g. NIC Govt Portal, TechCorp India") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Role Title") },
                        placeholder = { Text("e.g. Junior Python & Cloud Intern") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Form Section 2: Hard Eligibility Gates
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("2. Hard Eligibility Criteria (PS G-01)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ThemeColors.Ink)
                    Text("Candidates failing these are strictly excluded before ranking.", fontSize = 12.sp, color = ThemeColors.InkMute)
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("Mandatory City") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                        )
                        OutlinedTextField(
                            value = minExp,
                            onValueChange = { minExp = it.filter { c -> c.isDigit() } },
                            label = { Text("Min Exp (yrs)") },
                            placeholder = { Text("0 for freshers") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = payAmount,
                        onValueChange = { payAmount = it.filter { c -> c.isDigit() } },
                        label = { Text("Monthly Stipend / Pay (₹/mo)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Form Section 3: Required Tech Skills
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("3. Required Tech Skills", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ThemeColors.Ink)
                    Text("NaukriSetu engine uses these for fit scoring and semantic ranking.", fontSize = 12.sp, color = ThemeColors.InkMute)
                    Spacer(Modifier.height(8.dp))

                    // Preset quick toggle chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        presetSkills.forEach { sk ->
                            val sel = sk in skills
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (sel) ThemeColors.PrimarySoft else ThemeColors.CanvasSoft,
                                border = BorderStroke(1.dp, if (sel) ThemeColors.Primary else ThemeColors.Hairline),
                                modifier = Modifier.clickable {
                                    skills = if (sel) skills - sk else (skills + sk)
                                },
                            ) {
                                Text(
                                    text = if (sel) "✓ $sk" else "+ $sk",
                                    fontSize = 11.sp,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (sel) ThemeColors.PrimaryDeep else ThemeColors.Ink,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Custom skill adder
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = skillInput,
                            onValueChange = { skillInput = it },
                            placeholder = { Text("e.g. AWS, Django, Docker") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val trimmed = skillInput.trim()
                                if (trimmed.isNotBlank() && trimmed !in skills) {
                                    skills = skills + trimmed
                                    skillInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThemeColors.Primary,
                                contentColor = Color.White,
                            ),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text("Add")
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Form Section 4: Work Description
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("4. Role & Work Description", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ThemeColors.Ink)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = workDescription,
                        onValueChange = { workDescription = it },
                        placeholder = { Text("Describe the projects, tasks, and mentoring available for this role...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                    )
                }
            }

            if (errorMsg != null) {
                Spacer(Modifier.height(8.dp))
                Text(errorMsg!!, color = ThemeColors.Danger, fontSize = 13.sp)
            }

            if (successMsg != null) {
                Spacer(Modifier.height(8.dp))
                Text(successMsg!!, color = ThemeColors.PrimaryDeep, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))

            // Publish Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMsg = "Please enter the Role Title"
                        return@Button
                    }
                    if (skills.isEmpty()) {
                        errorMsg = "Please add at least one required skill"
                        return@Button
                    }
                    errorMsg = null

                    val expYears = minExp.toIntOrNull() ?: 0
                    val criteriaList = mutableListOf<Criterion>(Criterion.City(city.trim()))
                    if (expYears > 0) {
                        criteriaList.add(Criterion.MinExperience(expYears))
                    }

                    val newOpp = Opportunity(
                        id = "opp-added-${System.currentTimeMillis()}",
                        title = title.trim(),
                        employer = orgName.trim(),
                        city = city.trim(),
                        payMin = payAmount.toIntOrNull() ?: 25000,
                        payMax = (payAmount.toIntOrNull() ?: 25000) + 5000,
                        distanceKm = 2.5,
                        requiredSkills = skills,
                        criteria = criteriaList,
                        status = OppStatus.Live,
                        description = workDescription.ifBlank { "Exciting tech role at ${orgName.trim()}" },
                        workInvolved = "Software development, code review, and project delivery.",
                        employerId = employerId,
                    )

                    val pending = store.submitPost(newOpp)
                    // Adder portal publishes Live instantly (same board students see)
                    store.approvePost(pending.id)
                    successMsg = "✓ Role published Live to NaukriSetu matching engine!"
                    onSubmitted()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(ThemeRadii.Button.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ThemeColors.Primary,
                    contentColor = Color.White,
                ),
            ) {
                Text("Publish to NaukriSetu Engine", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onNavigate(Routes.myPostings) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Button.dp),
                border = BorderStroke(1.dp, ThemeColors.HairlineStrong),
            ) {
                Text("View All Published Roles", color = ThemeColors.InkSecondary)
            }
        }
    }
}

@Composable
fun MyPostingsScreen(
    store: DemoStore,
    onNavigate: (String) -> Unit,
) {
    var refreshTrigger by remember { mutableStateOf(0) }
    val employerId = remember(refreshTrigger) { store.currentSeeker()?.email ?: "officer.nic@gov.in" }
    // Same Live board that students & employees see — engine filters Live only.
    // Adders see EVERYTHING live (seed + every adder's posts), with "Yours" badge on their own.
    val allLive = remember(refreshTrigger) { seekerVisibleOpps(store.allOpportunities()) }
    val pendingMine = remember(refreshTrigger) {
        store.allOpportunities().filter { it.status == OppStatus.Pending }
    }

    Scaffold(
        bottomBar = { EmployerTabs(current = Routes.myPostings, onNavigate = onNavigate) },
        containerColor = ThemeColors.Canvas,
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("All Live Opportunities", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ThemeColors.Ink)
                    Text("Same board students & employees see · ${allLive.size} live", fontSize = 13.sp, color = ThemeColors.InkMute)
                }
                Button(
                    onClick = { onNavigate(Routes.postJob) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeColors.Primary,
                        contentColor = Color.White,
                    ),
                ) {
                    Text("+ Add Job", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(12.dp))

            if (pendingMine.isNotEmpty()) {
                Text("⏳ Pending approval (${pendingMine.size})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = ThemeColors.InkSecondary)
                Spacer(Modifier.height(6.dp))
            }

            allLive.forEach { opp ->
                val isMine = opp.employerId.equals(employerId, ignoreCase = true)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    shape = RoundedCornerShape(ThemeRadii.Card.dp),
                    border = BorderStroke(1.dp, ThemeColors.Hairline),
                    colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(opp.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ThemeColors.Ink, modifier = Modifier.weight(1f))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (isMine) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = ThemeColors.Primary) {
                                        Text("YOURS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Surface(shape = RoundedCornerShape(4.dp), color = ThemeColors.PrimarySoft) {
                                    Text("LIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ThemeColors.PrimaryDeep, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }

                        Text("${opp.employer} · ${opp.city}", fontSize = 13.sp, color = ThemeColors.InkMute)
                        Spacer(Modifier.height(6.dp))

                        Text(
                            "Required: ${opp.requiredSkills.joinToString(", ")}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ThemeColors.InkSecondary,
                        )

                        Text(
                            "Hard gates: City ${opp.city} · Pay ₹${opp.payMin}/mo",
                            fontSize = 12.sp,
                            color = ThemeColors.InkMute,
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("⚡ Live for matched students", fontSize = 12.sp, color = ThemeColors.PrimaryDeep)
                            if (isMine) {
                                TextButton(onClick = { store.deletePost(opp.id); refreshTrigger++ }) {
                                    Text("Remove", color = ThemeColors.Danger, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
