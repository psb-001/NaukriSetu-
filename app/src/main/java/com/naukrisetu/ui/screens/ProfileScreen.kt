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
import com.naukrisetu.data.Seed
import com.naukrisetu.engine.seekerVisibleOpps
import com.naukrisetu.engine.splitMatches
import com.naukrisetu.ui.components.SeekerTabs
import com.naukrisetu.ui.icons.Lucide
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    store: DemoStore,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
) {
    var seeker by remember { mutableStateOf(store.currentSeeker() ?: Seed.demoSeeker) }
    var cityInput by remember(seeker.city) { mutableStateOf(seeker.city) }
    var skillInput by remember { mutableStateOf("") }
    var certInput by remember { mutableStateOf("") }
    var citySavedMsg by remember { mutableStateOf<String?>(null) }

    val (eligible, _) = splitMatches(seeker, seekerVisibleOpps(store))

    Scaffold(
        bottomBar = { SeekerTabs(current = Routes.profile, onNavigate = onNavigate) },
        containerColor = ThemeColors.Canvas,
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text("Student Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ThemeColors.Ink)
            Text("${seeker.name} · ${seeker.email}", fontSize = 14.sp, color = ThemeColors.InkMute)

            Spacer(Modifier.height(12.dp))

            // Verified Campus Status Badge Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.CanvasSoft),
                border = BorderStroke(1.dp, ThemeColors.Primary),
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Lucide.ShieldCheck,
                        contentDescription = null,
                        tint = ThemeColors.Primary,
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Verified Campus Student", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ThemeColors.Ink)
                        Text("100% Eligible · All ${eligible.size} tech opportunities unlocked", fontSize = 12.sp, color = ThemeColors.InkMute)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Section 1: Location & Preferred City
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("1. Preferred City / Location", fontWeight = FontWeight.SemiBold, color = ThemeColors.Ink)
                    Text("The matching engine filters eligible roles based on your city.", fontSize = 12.sp, color = ThemeColors.InkMute)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = cityInput,
                            onValueChange = { cityInput = it },
                            label = { Text("City") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val trimmed = cityInput.trim()
                                if (trimmed.isNotBlank()) {
                                    val updated = seeker.copy(city = trimmed)
                                    store.upsertSeeker(updated)
                                    seeker = updated
                                    citySavedMsg = "Updated to $trimmed! Matches refreshed."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ThemeColors.Ink),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text("Save")
                        }
                    }
                    citySavedMsg?.let {
                        Spacer(Modifier.height(6.dp))
                        Text("✓ $it", fontSize = 12.sp, color = ThemeColors.Primary)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Section 2: Technical Skills
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("2. Verified Skills & Technologies", fontWeight = FontWeight.SemiBold, color = ThemeColors.Ink)
                    Text("RozgarLink uses these to rank and compute your match fit score.", fontSize = 12.sp, color = ThemeColors.InkMute)
                    Spacer(Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        seeker.skills.forEach { sk ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
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
                                        modifier = Modifier.clickable {
                                            val updated = seeker.copy(skills = seeker.skills - sk)
                                            store.upsertSeeker(updated)
                                            seeker = updated
                                        },
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = skillInput,
                            onValueChange = { skillInput = it },
                            placeholder = { Text("e.g. Docker, TypeScript, React Native") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val trimmed = skillInput.trim()
                                if (trimmed.isNotBlank() && trimmed !in seeker.skills) {
                                    val updated = seeker.copy(skills = seeker.skills + trimmed)
                                    store.upsertSeeker(updated)
                                    seeker = updated
                                    skillInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ThemeColors.Ink),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text("Add")
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Section 3: Degree & Education Qualifications
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("3. College Degree & Education", fontWeight = FontWeight.SemiBold, color = ThemeColors.Ink)
                    Text("Verified qualifications for campus recruiters.", fontSize = 12.sp, color = ThemeColors.InkMute)
                    Spacer(Modifier.height(8.dp))

                    seeker.certificates.forEach { cert ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ThemeColors.CanvasSoft,
                            border = BorderStroke(1.dp, ThemeColors.Hairline),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("🎓 $cert", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink, modifier = Modifier.weight(1f))
                                Text(
                                    "Remove",
                                    fontSize = 11.sp,
                                    color = ThemeColors.Danger,
                                    modifier = Modifier.clickable {
                                        val updated = seeker.copy(certificates = seeker.certificates - cert)
                                        store.upsertSeeker(updated)
                                        seeker = updated
                                    },
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = certInput,
                            onValueChange = { certInput = it },
                            placeholder = { Text("e.g. B.Tech CS, BCA, MCA, Diploma") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val trimmed = certInput.trim()
                                if (trimmed.isNotBlank() && trimmed !in seeker.certificates) {
                                    val updated = seeker.copy(certificates = seeker.certificates + trimmed)
                                    store.upsertSeeker(updated)
                                    seeker = updated
                                    certInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ThemeColors.Ink),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text("Add")
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Settings & Logout
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("App Settings", fontWeight = FontWeight.SemiBold, color = ThemeColors.Ink)

                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onLogout() }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(imageVector = Lucide.LogOut, contentDescription = null, tint = ThemeColors.Danger, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Log Out", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Danger)
                    }
                }
            }
        }
    }
}
