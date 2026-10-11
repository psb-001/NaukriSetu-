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
import com.naukrisetu.engine.Criterion
import com.naukrisetu.engine.OppStatus
import com.naukrisetu.engine.Opportunity
import com.naukrisetu.engine.canonicalSkills
import com.naukrisetu.ui.components.EmployerTabs
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@Composable
fun PostJobScreen(
    store: DemoStore,
    onNavigate: (String) -> Unit,
    onSubmitted: () -> Unit,
) {
    // Use the employer's email as the canonical employerId so
    // postingsByEmployer() correctly filters their own posts.
    val employerId = store.currentSeeker()?.email ?: "employer@voltas.com"
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var skillQuery by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf(listOf<String>()) }
    var criteria by remember { mutableStateOf(listOf<Criterion>(Criterion.City("Pune"))) }
    var newCriterionKind by remember { mutableStateOf("Certificate") }
    var newCriterionValue by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Pune") }
    var payMin by remember { mutableStateOf("") }
    var payMax by remember { mutableStateOf("") }
    var work by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var confirmation by remember { mutableStateOf<String?>(null) }

    val skillSuggestions = remember(skillQuery) {
        if (skillQuery.isBlank()) emptyList()
        else canonicalSkills().filter { it.contains(skillQuery, ignoreCase = true) && it !in skills }.take(5)
    }

    Scaffold(
        bottomBar = { EmployerTabs(current = Routes.postJob, onNavigate = onNavigate) },
        containerColor = ThemeColors.Canvas,
    ) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Post a job", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("Live after a verifier approves it.", fontSize = 13.sp, color = ThemeColors.InkMute)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Job title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, placeholder = { Text("Split-AC install, servicing and gas top-ups for residential customers.") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Text("Required skills", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("Suggestions come from the standard skills list.", fontSize = 12.sp, color = ThemeColors.InkMute2)
            OutlinedTextField(value = skillQuery, onValueChange = { skillQuery = it }, label = { Text("Add a required skill") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            skillSuggestions.forEach { s ->
                TextButton(onClick = { skills = (skills + s).take(12); skillQuery = "" }) { Text(s) }
            }
            Text(skills.joinToString(", "), fontSize = 13.sp, color = ThemeColors.InkSecondary)
            Spacer(Modifier.height(8.dp))
            Text("Hard criteria", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("At least one required. Seekers who fail are excluded — not just ranked lower.", fontSize = 12.sp, color = ThemeColors.InkMute2)
            criteria.forEach { c ->
                Text("• ${criterionText(c)}", fontSize = 14.sp, color = ThemeColors.Ink)
            }
            Row {
                OutlinedTextField(value = newCriterionKind, onValueChange = {}, label = { Text("Type") }, readOnly = true, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = newCriterionValue, onValueChange = { newCriterionValue = it }, label = { Text("Certificate name / City / Years") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Row {
                TextButton(onClick = { newCriterionKind = "Certificate" }) { Text("Certificate", color = if (newCriterionKind == "Certificate") ThemeColors.Primary else ThemeColors.InkMute) }
                TextButton(onClick = { newCriterionKind = "City" }) { Text("City", color = if (newCriterionKind == "City") ThemeColors.Primary else ThemeColors.InkMute) }
                TextButton(onClick = { newCriterionKind = "MinExperience" }) { Text("Experience", color = if (newCriterionKind == "MinExperience") ThemeColors.Primary else ThemeColors.InkMute) }
                TextButton(onClick = {
                    val c = when (newCriterionKind) {
                        "City" -> Criterion.City(newCriterionValue.ifBlank { city })
                        "MinExperience" -> Criterion.MinExperience(newCriterionValue.toIntOrNull() ?: 0)
                        else -> Criterion.Certificate(newCriterionValue)
                    }
                    criteria = criteria + c
                    newCriterionValue = ""
                }) { Text("Add criterion") }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Row {
                OutlinedTextField(value = payMin, onValueChange = { payMin = it.filter { c -> c.isDigit() } }, label = { Text("Pay min (₹/mo)") }, singleLine = true, modifier = Modifier.weight(1f))
                OutlinedTextField(value = payMax, onValueChange = { payMax = it.filter { c -> c.isDigit() } }, label = { Text("Pay max (₹/mo)") }, singleLine = true, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = work, onValueChange = { work = it }, label = { Text("Work involved") }, supportingText = { Text("Shown on the job card seekers see.") }, modifier = Modifier.fillMaxWidth())
            if (error != null) Text(error!!, color = ThemeColors.Danger, fontSize = 13.sp)
            if (confirmation != null) Text(confirmation!!, color = ThemeColors.Primary, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (title.isBlank()) { error = "Job title is required"; return@Button }
                    if (criteria.isEmpty()) { error = "Add at least one hard criterion"; return@Button }
                    error = null
                    store.submitPost(
                        Opportunity(
                            id = "opp-user-${System.currentTimeMillis()}",
                            title = title.trim(),
                            employer = "You (Employer)",
                            city = city.trim(),
                            payMin = payMin.toIntOrNull() ?: 0,
                            payMax = payMax.toIntOrNull() ?: 0,
                            distanceKm = 0.0,
                            requiredSkills = skills,
                            criteria = criteria,
                            status = OppStatus.Pending,
                            description = description.trim(),
                            workInvolved = work.trim(),
                            employerId = employerId,
                        ),
                    )
                    confirmation = "Sent for verification — live after approval."
                    onSubmitted()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Button.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ThemeColors.Primary, contentColor = ThemeColors.OnPrimary),
            ) { Text("Send for verification") }
            Text("Your post is invisible to seekers until a verifier approves it.", fontSize = 12.sp, color = ThemeColors.InkMute2)
        }
    }
}

private fun criterionText(c: Criterion): String = when (c) {
    is Criterion.City -> "City equals ${c.city}"
    is Criterion.Certificate -> "Certificate requires ${c.name}"
    is Criterion.MinAge -> "Age requires ≥ ${c.years}"
    is Criterion.MinExperience -> "Experience requires ≥ ${c.years} yrs"
}

@Composable
fun MyPostingsScreen(
    store: DemoStore,
    onNavigate: (String) -> Unit,
) {
    val employerId = store.currentSeeker()?.email ?: "employer@voltas.com"
    val postings = store.postingsByEmployer(employerId)

    Scaffold(
        bottomBar = { EmployerTabs(current = Routes.myPostings, onNavigate = onNavigate) },
        containerColor = ThemeColors.Canvas,
    ) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("My postings", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("Seekers only ever see Live posts.", fontSize = 13.sp, color = ThemeColors.InkMute)
            Spacer(Modifier.height(8.dp))
            if (postings.isEmpty()) {
                Text("No postings yet — post your first job.", fontSize = 14.sp, color = ThemeColors.InkMute)
                TextButton(onClick = { onNavigate(Routes.postJob) }) { Text("Post a job") }
            }
            postings.forEach { opp ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(ThemeRadii.Card.dp),
                    border = BorderStroke(1.dp, ThemeColors.Hairline),
                    colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(opp.title, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                        val badge = when (opp.status) {
                            OppStatus.Live -> "Live"
                            OppStatus.Pending -> "Pending"
                            OppStatus.Rejected -> "Rejected"
                        }
                        Text(
                            badge,
                            fontSize = 12.sp,
                            color = when (opp.status) {
                                OppStatus.Live -> ThemeColors.Primary
                                OppStatus.Pending -> ThemeColors.InkMute
                                OppStatus.Rejected -> ThemeColors.Danger
                            },
                        )
                        Text("${opp.criteria.size} criteria · pay ${opp.payMin}–${opp.payMax}", fontSize = 13.sp, color = ThemeColors.InkMute)
                        when (opp.status) {
                            OppStatus.Live -> Text("Visible to seekers", fontSize = 13.sp, color = ThemeColors.InkSecondary)
                            OppStatus.Pending -> Text("Not visible to seekers yet", fontSize = 13.sp, color = ThemeColors.InkSecondary)
                            OppStatus.Rejected -> Text("Hidden from seekers", fontSize = 13.sp, color = ThemeColors.InkSecondary)
                        }
                        Text("Editing re-sends the post for verification.", fontSize = 12.sp, color = ThemeColors.InkMute2)
                        Row {
                            TextButton(onClick = {
                                store.deletePost(opp.id)
                                store.submitPost(opp.copy(status = OppStatus.Pending))
                                onNavigate(Routes.postJob)
                            }) { Text("Edit") }
                            TextButton(onClick = { store.deletePost(opp.id) }) { Text("Delete") }
                        }
                    }
                }
            }
        }
    }
}
