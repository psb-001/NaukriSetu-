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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.naukrisetu.engine.Seeker
import com.naukrisetu.engine.canonicalSkills
import com.naukrisetu.ui.copy.Strings
import com.naukrisetu.ui.onboarding.OnboardingValidator
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@Composable
fun OnboardingScreen(
    email: String,
    store: DemoStore,
    onFinished: () -> Unit,
) {
    var step by remember { mutableStateOf(1) }
    var profileRole by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf(listOf<String>()) }
    var goal by remember { mutableStateOf("") }
    var basicsError by remember { mutableStateOf<String?>(null) }

    fun persist() {
        val existing = store.seeker("seeker-ravi")
        store.upsertSeeker(
            (existing ?: Seeker(id = "seeker-ravi", name = "", email = email, phone = "", city = "")).copy(
                name = name.ifBlank { existing?.name ?: "" },
                email = email,
                phone = phone.ifBlank { existing?.phone ?: "" },
                city = city.ifBlank { existing?.city ?: "" },
                age = age.toIntOrNull() ?: existing?.age,
                experienceYears = experience.toIntOrNull() ?: existing?.experienceYears,
                skills = skills.ifEmpty { existing?.skills ?: emptyList() },
                role = profileRole ?: existing?.role,
                goal = goal.ifBlank { existing?.goal },
            ),
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(Strings.onboardingTitle, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
        Text("$step of 3", fontSize = 13.sp, color = ThemeColors.InkMute)
        Spacer(Modifier.height(16.dp))
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            when (step) {
                1 -> RoleStep(selected = profileRole, onSelect = { profileRole = it; persist() })
                2 -> BasicsStep(
                    name = name, onName = { name = it },
                    phone = phone, onPhone = { phone = it },
                    city = city, onCity = { city = it },
                    age = age, onAge = { age = it },
                    experience = experience, onExperience = { experience = it },
                    error = basicsError,
                )
                3 -> SkillsStep(
                    profileRole = profileRole,
                    skills = skills,
                    onSkills = { skills = it },
                    goal = goal,
                    onGoal = { goal = it },
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (step > 1) {
                OutlinedButton(onClick = { persist(); step-- }) { Text("Back") }
            } else {
                Spacer(Modifier.weight(1f))
            }
            if (step < 3) {
                Button(
                    onClick = {
                        if (step == 2) {
                            if (!OnboardingValidator.validName(name)) { basicsError = "Enter your full name"; return@Button }
                            if (!OnboardingValidator.validPhone(phone)) { basicsError = "Enter a 10-digit mobile number starting 6–9"; return@Button }
                            if (city.isBlank()) { basicsError = "City is needed to find jobs near you"; return@Button }
                            basicsError = null
                        }
                        persist()
                        step++
                    },
                    shape = RoundedCornerShape(ThemeRadii.Button.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeColors.Primary,
                        contentColor = ThemeColors.OnPrimary,
                    ),
                ) { Text("Next") }
            } else {
                Button(
                    onClick = { persist(); onFinished() },
                    shape = RoundedCornerShape(ThemeRadii.Button.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeColors.Primary,
                        contentColor = ThemeColors.OnPrimary,
                    ),
                ) { Text("Finish setup") }
            }
        }
        Text(Strings.onboardingSaved, fontSize = 12.sp, color = ThemeColors.InkMute2)
    }
}

@Composable
private fun RoleStep(selected: String?, onSelect: (String) -> Unit) {
    Text("What describes you best?", fontSize = 16.sp, color = ThemeColors.Ink)
    Spacer(Modifier.height(12.dp))
    roleCard("Experienced worker", "I have hands-on skills and want jobs now.", selected, onSelect)
    roleCard("Student", "I'm studying and picking a goal.", selected, onSelect)
    roleCard("Trainee", "I'm learning a trade.", selected, onSelect)
}

@Composable
private fun roleCard(title: String, blurb: String, selected: String?, onSelect: (String) -> Unit) {
    val isSel = selected == title
    Card(
        onClick = { onSelect(title) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(ThemeRadii.Card.dp),
        border = BorderStroke(1.dp, if (isSel) ThemeColors.Primary else ThemeColors.Hairline),
        colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text(blurb, fontSize = 13.sp, color = ThemeColors.InkMute)
        }
    }
}

@Composable
private fun BasicsStep(
    name: String, onName: (String) -> Unit,
    phone: String, onPhone: (String) -> Unit,
    city: String, onCity: (String) -> Unit,
    age: String, onAge: (String) -> Unit,
    experience: String, onExperience: (String) -> Unit,
    error: String?,
) {
    Text("The basics", fontSize = 16.sp, color = ThemeColors.Ink)
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(value = name, onValueChange = onName, label = { Text("Full name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(value = phone, onValueChange = onPhone, label = { Text("Phone") }, prefix = { Text("+91 ") }, supportingText = { Text("10 digits, starts with 6–9.") }, singleLine = true, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(value = city, onValueChange = onCity, label = { Text("City") }, supportingText = { Text("Needed to find jobs near you — matches are filtered by city.") }, singleLine = true, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = age, onValueChange = onAge, label = { Text("Age (optional)") }, singleLine = true, modifier = Modifier.weight(1f))
        OutlinedTextField(value = experience, onValueChange = onExperience, label = { Text("Experience, yrs (optional)") }, singleLine = true, modifier = Modifier.weight(1f))
    }
    Text("Some jobs need this — add it anytime in Profile.", fontSize = 12.sp, color = ThemeColors.InkMute2)
    if (error != null) {
        Spacer(Modifier.height(8.dp))
        Text(error, color = ThemeColors.Danger, fontSize = 13.sp)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillsStep(
    profileRole: String?,
    skills: List<String>,
    onSkills: (List<String>) -> Unit,
    goal: String,
    onGoal: (String) -> Unit,
) {
    if (profileRole == "Student") {
        Text("What are you studying?", fontSize = 16.sp, color = ThemeColors.Ink)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = goal, onValueChange = onGoal, label = { Text("What do you want to become?") }, supportingText = { Text("Pick from the standard skills list.") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        return
    }
    if (profileRole == "Trainee") {
        Text("Your interests", fontSize = 16.sp, color = ThemeColors.Ink)
        Text("Pick the trades you want to learn.", fontSize = 13.sp, color = ThemeColors.InkMute)
        Spacer(Modifier.height(8.dp))
        InterestGrid(all = canonicalSkills(), selected = skills, onToggle = { s ->
            onSkills(if (s in skills) skills - s else (skills + s).take(12))
        })
        return
    }
    var query by remember { mutableStateOf("") }
    val suggestions = remember(query) {
        if (query.isBlank()) emptyList()
        else canonicalSkills().filter { it.contains(query, ignoreCase = true) && it !in skills }.take(5)
    }
    Text("Your skills", fontSize = 16.sp, color = ThemeColors.Ink)
    Text("Type to add — suggestions come from the standard skills list.", fontSize = 13.sp, color = ThemeColors.InkMute)
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Add a skill") }, singleLine = true, modifier = Modifier.fillMaxWidth())
    suggestions.forEach { s ->
        TextButton(onClick = { onSkills((skills + s).take(12)); query = "" }) {
            Text("$s — matched from canonical skills", color = ThemeColors.Ink)
        }
    }
    Spacer(Modifier.height(8.dp))
    Text("${skills.size} of 12 skills", fontSize = 13.sp, color = ThemeColors.InkMute)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        skills.forEach { s ->
            AssistChip(onClick = { onSkills(skills - s) }, label = { Text(s) })
        }
    }
    Text("Matches update instantly when skills change.", fontSize = 12.sp, color = ThemeColors.InkMute2)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InterestGrid(all: List<String>, selected: List<String>, onToggle: (String) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        all.forEach { s ->
            val sel = s in selected
            if (sel) {
                AssistChip(onClick = { onToggle(s) }, label = { Text("✓ $s") })
            } else {
                TextButton(onClick = { onToggle(s) }) { Text(s, color = ThemeColors.InkMute) }
            }
        }
    }
}
