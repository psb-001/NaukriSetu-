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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.BuildConfig
import com.naukrisetu.ai.OpenRouterAssistant
import com.naukrisetu.data.DemoStore
import com.naukrisetu.engine.Seeker
import com.naukrisetu.engine.SkillTaxonomy
import com.naukrisetu.ui.icons.Lucide
import com.naukrisetu.ui.onboarding.OnboardingValidator
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    email: String,
    store: DemoStore,
    onFinished: () -> Unit,
) {
    var step by remember { mutableStateOf(1) }
    var degreeTrack by remember { mutableStateOf("B.Tech / B.E. (Computer Science / IT)") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Pune") }
    var studyYear by remember { mutableStateOf("Final Year") }
    var experience by remember { mutableStateOf("0") }
    var skills by remember { mutableStateOf(listOf("Python", "Problem Solving", "Git")) }
    var careerGoal by remember { mutableStateOf("Junior Software / Mobile Engineer") }
    var basicsError by remember { mutableStateOf<String?>(null) }

    fun persist() {
        val existing = store.seeker(email)
        store.upsertSeeker(
            (existing ?: Seeker(id = "seeker-$email", name = "", email = email, phone = "", city = city)).copy(
                name = name.ifBlank { existing?.name ?: "Student" },
                email = email,
                phone = phone.ifBlank { existing?.phone ?: "" },
                city = city.ifBlank { existing?.city ?: "Pune" },
                age = 21,
                experienceYears = experience.toIntOrNull() ?: 0,
                skills = skills.ifEmpty { existing?.skills ?: listOf("Python", "Git") },
                certificates = listOf(degreeTrack),
                role = "Student",
                goal = careerGoal,
            ),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        // Step progress header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    "Student Profile Setup",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThemeColors.Ink,
                )
                Text(
                    "Step $step of 3 · Skills-first matching",
                    fontSize = 13.sp,
                    color = ThemeColors.InkMute,
                )
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = ThemeColors.PrimarySoft,
            ) {
                Text(
                    "$step / 3",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ThemeColors.PrimaryDeep,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Step content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            when (step) {
                1 -> DegreeTrackStep(
                    selected = degreeTrack,
                    onSelect = { degreeTrack = it },
                )
                2 -> BasicsStep(
                    name = name, onName = { name = it },
                    phone = phone, onPhone = { phone = it },
                    city = city, onCity = { city = it },
                    studyYear = studyYear, onStudyYear = { studyYear = it },
                    experience = experience, onExperience = { experience = it },
                    error = basicsError,
                )
                3 -> SkillsStep(
                    skills = skills,
                    onSkills = { skills = it },
                    careerGoal = careerGoal,
                    onCareerGoal = { careerGoal = it },
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Bottom navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (step > 1) {
                OutlinedButton(
                    onClick = { persist(); step-- },
                    shape = RoundedCornerShape(ThemeRadii.Button.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ThemeColors.InkSecondary),
                    border = BorderStroke(1.dp, ThemeColors.HairlineStrong),
                ) {
                    Text("Back", fontSize = 14.sp)
                }
            } else {
                Spacer(Modifier.width(1.dp))
            }

            if (step < 3) {
                Button(
                    onClick = {
                        if (step == 2) {
                            if (!OnboardingValidator.validName(name)) {
                                basicsError = "Enter your full name"
                                return@Button
                            }
                            if (!OnboardingValidator.validPhone(phone)) {
                                basicsError = "Enter a valid 10-digit mobile number starting 6–9"
                                return@Button
                            }
                            if (city.isBlank()) {
                                basicsError = "City is required to match nearby roles"
                                return@Button
                            }
                            basicsError = null
                        }
                        persist()
                        step++
                    },
                    shape = RoundedCornerShape(ThemeRadii.Button.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeColors.Primary,
                        contentColor = Color.White,
                    ),
                ) {
                    Text("Continue", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Button(
                    onClick = {
                        persist()
                        onFinished()
                    },
                    shape = RoundedCornerShape(ThemeRadii.Button.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeColors.Primary,
                        contentColor = Color.White,
                    ),
                ) {
                    Text("Complete & Match Jobs", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun DegreeTrackStep(
    selected: String,
    onSelect: (String) -> Unit,
) {
    Text(
        "What is your degree or education track?",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = ThemeColors.Ink,
    )
    Text(
        "Recruiters look for student status for internships and entry-level positions.",
        fontSize = 13.sp,
        color = ThemeColors.InkMute,
    )
    Spacer(Modifier.height(14.dp))

    val tracks = listOf(
        "B.Tech / B.E. (Computer Science / IT)" to "Focus on software engineering, algorithms, and development.",
        "BCA / MCA / B.Sc Computer Science" to "Web and software applications, database and cloud administration.",
        "Diploma / Polytechnic (Tech / Electronics)" to "Hands-on technical implementation, systems, and hardware.",
        "Self-Taught / Other Engineering Branch" to "Building tech projects and transitioning into software.",
    )

    tracks.forEach { (title, blurb) ->
        val isSel = selected == title
        Card(
            onClick = { onSelect(title) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            shape = RoundedCornerShape(ThemeRadii.Card.dp),
            border = BorderStroke(
                if (isSel) 2.dp else 1.dp,
                if (isSel) ThemeColors.Primary else ThemeColors.Hairline,
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isSel) ThemeColors.PrimarySoft else ThemeColors.Canvas,
            ),
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = if (isSel) ThemeColors.Primary else ThemeColors.Hairline,
                    modifier = Modifier.size(10.dp),
                ) {}
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ThemeColors.Ink)
                    Spacer(Modifier.height(2.dp))
                    Text(blurb, fontSize = 12.sp, color = ThemeColors.InkMute)
                }
            }
        }
    }
}

@Composable
private fun BasicsStep(
    name: String, onName: (String) -> Unit,
    phone: String, onPhone: (String) -> Unit,
    city: String, onCity: (String) -> Unit,
    studyYear: String, onStudyYear: (String) -> Unit,
    experience: String, onExperience: (String) -> Unit,
    error: String?,
) {
    Text("Personal & Campus Info", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = ThemeColors.Ink)
    Text("Used to calculate location match and fresher eligibility.", fontSize = 13.sp, color = ThemeColors.InkMute)
    Spacer(Modifier.height(12.dp))

    OutlinedTextField(
        value = name,
        onValueChange = onName,
        label = { Text("Full Name") },
        placeholder = { Text("e.g. Aman Verma") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
    )
    Spacer(Modifier.height(10.dp))

    OutlinedTextField(
        value = phone,
        onValueChange = onPhone,
        label = { Text("Mobile Number") },
        prefix = { Text("+91 ") },
        placeholder = { Text("9823012345") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
    )
    Spacer(Modifier.height(10.dp))

    OutlinedTextField(
        value = city,
        onValueChange = onCity,
        label = { Text("Preferred Job / Internship City") },
        placeholder = { Text("e.g. Pune, Mumbai, Bangalore") },
        supportingText = { Text("Hard eligibility gate — you'll see jobs in this city.") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
    )
    Spacer(Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OutlinedTextField(
            value = studyYear,
            onValueChange = onStudyYear,
            label = { Text("Year of Study") },
            placeholder = { Text("e.g. Final Year") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
        )
        OutlinedTextField(
            value = experience,
            onValueChange = { onExperience(it.filter { c -> c.isDigit() }.take(2)) },
            label = { Text("Experience (years)") },
            placeholder = { Text("0 for Fresher") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
        )
    }

    if (error != null) {
        Spacer(Modifier.height(8.dp))
        Text(error, color = ThemeColors.Danger, fontSize = 13.sp)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillsStep(
    skills: List<String>,
    onSkills: (List<String>) -> Unit,
    careerGoal: String,
    onCareerGoal: (String) -> Unit,
) {
    var naturalSpeechInput by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var aiStatusMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Text("Skills & Projects", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = ThemeColors.Ink)
    Text("The matching engine scores your fit based on these skills.", fontSize = 13.sp, color = ThemeColors.InkMute)

    Spacer(Modifier.height(12.dp))

    // AI Natural Language Skill Extractor
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ThemeRadii.Card.dp),
        border = BorderStroke(1.dp, ThemeColors.Hairline),
        colors = CardDefaults.cardColors(containerColor = ThemeColors.CanvasSoft),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Lucide.Sparkles,
                    contentDescription = null,
                    tint = ThemeColors.PrimaryDeep,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "AI Skill & Project Extractor",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = ThemeColors.Ink,
                )
            }
            Text(
                "Describe your college projects, coursework, or languages in your own words. AI will map them into verified skills.",
                fontSize = 12.sp,
                color = ThemeColors.InkMute,
                modifier = Modifier.padding(vertical = 4.dp),
            )
            OutlinedTextField(
                value = naturalSpeechInput,
                onValueChange = { naturalSpeechInput = it },
                placeholder = {
                    Text(
                        "e.g. 'I built an Android app with Kotlin and Firebase, and know Python, SQL, and Git.'",
                        fontSize = 12.sp,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3,
                shape = RoundedCornerShape(8.dp),
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (aiStatusMessage != null) {
                    Text(aiStatusMessage!!, fontSize = 11.sp, color = ThemeColors.PrimaryDeep, modifier = Modifier.weight(1f))
                } else {
                    Spacer(Modifier.weight(1f))
                }
                Button(
                    enabled = !isAnalyzing && naturalSpeechInput.isNotBlank(),
                    onClick = {
                        val apiKey = BuildConfig.OPENROUTER_API_KEY
                        if (apiKey.isNotBlank()) {
                            isAnalyzing = true
                            aiStatusMessage = "Analyzing projects with AI..."
                            scope.launch {
                                val result = OpenRouterAssistant.analyzeCandidateSpeech(naturalSpeechInput, apiKey)
                                isAnalyzing = false
                                result.onSuccess { profile ->
                                    val newSkills = (skills + profile.extractedSkills).distinct().take(12)
                                    onSkills(newSkills)
                                    aiStatusMessage = "✓ Extracted: ${profile.extractedSkills.joinToString(", ")}"
                                }.onFailure {
                                    val matched = SkillTaxonomy.findCanonicalEquivalents(naturalSpeechInput)
                                    if (matched.isNotEmpty()) {
                                        onSkills((skills + matched).distinct().take(12))
                                        aiStatusMessage = "✓ Matched ${matched.size} tech skills"
                                    } else {
                                        aiStatusMessage = "Mention programming languages or frameworks."
                                    }
                                }
                            }
                        } else {
                            val matched = SkillTaxonomy.findCanonicalEquivalents(naturalSpeechInput)
                            if (matched.isNotEmpty()) {
                                onSkills((skills + matched).distinct().take(12))
                                aiStatusMessage = "✓ Matched ${matched.size} skills"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeColors.Primary,
                        contentColor = Color.White,
                    ),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(if (isAnalyzing) "Analyzing..." else "Auto-Extract", fontSize = 12.sp)
                }
            }
        }
    }

    Spacer(Modifier.height(14.dp))

    // Quick Add Popular Tech Skills
    Text("Quick Add Student Tech Skills:", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
    Spacer(Modifier.height(6.dp))

    val popular = listOf("Python", "Android", "Kotlin", "React", "JavaScript", "SQL", "Git", "Figma", "Problem Solving", "Machine Learning")
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        popular.forEach { sk ->
            val isAdded = sk in skills
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isAdded) ThemeColors.PrimarySoft else ThemeColors.Canvas,
                border = BorderStroke(1.dp, if (isAdded) ThemeColors.Primary else ThemeColors.Hairline),
                modifier = Modifier.clickable {
                    if (isAdded) onSkills(skills - sk) else onSkills((skills + sk).take(12))
                },
            ) {
                Text(
                    text = if (isAdded) "✓ $sk" else "+ $sk",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isAdded) ThemeColors.PrimaryDeep else ThemeColors.Ink,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
        }
    }

    Spacer(Modifier.height(14.dp))

    // Currently Selected Skills
    Text("Your Active Skills (${skills.size}):", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
    Spacer(Modifier.height(6.dp))

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        skills.forEach { sk ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ThemeColors.CanvasSoft,
                border = BorderStroke(1.dp, ThemeColors.Hairline),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text("⚡ $sk", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "✕",
                        fontSize = 11.sp,
                        color = ThemeColors.InkMute,
                        modifier = Modifier.clickable { onSkills(skills - sk) },
                    )
                }
            }
        }
    }
}
