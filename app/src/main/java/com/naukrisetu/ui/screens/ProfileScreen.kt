package com.naukrisetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.data.DemoStore
import com.naukrisetu.data.Seed
import com.naukrisetu.engine.seekerVisibleOpps
import com.naukrisetu.engine.splitMatches
import com.naukrisetu.ui.components.AmberBox
import com.naukrisetu.ui.components.SeekerTabs
import com.naukrisetu.ui.kyc.KycLogic
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii
import com.naukrisetu.ui.icons.Lucide

@Composable
fun ProfileScreen(
    store: DemoStore,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
) {
    var seeker by remember { mutableStateOf(store.currentSeeker() ?: Seed.demoSeeker) }
    val done = KycLogic.checksDone(seeker)
    val pct = KycLogic.progressPct(seeker)
    var aadhaarInput by remember(seeker.aadhaar) { mutableStateOf(seeker.aadhaar ?: "") }
    var addressInput by remember { mutableStateOf("") }
    var cityInput by remember(seeker.city) { mutableStateOf(seeker.city) }
    var certInput by remember { mutableStateOf("") }
    var aadhaarError by remember { mutableStateOf<String?>(null) }
    var addressSaved by remember { mutableStateOf(false) }

    val (eligible, _) = splitMatches(seeker, seekerVisibleOpps(store))

    Scaffold(
        bottomBar = { SeekerTabs(current = Routes.profile, onNavigate = onNavigate) },
        containerColor = ThemeColors.Canvas,
    ) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Profile", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("${seeker.name} · ${seeker.email}", fontSize = 13.sp, color = ThemeColors.InkMute)
            Text(
                if (pct == 100) "Profile fully verified — all job matches unlocked!"
                else "Verification in progress — completing KYC unlocks all jobs.",
                fontSize = 13.sp,
                color = if (pct == 100) ThemeColors.Primary else ThemeColors.InkMute,
            )
            Spacer(Modifier.height(8.dp))
            Text("Profile $pct% complete", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("$done of 5 checks done", fontSize = 13.sp, color = ThemeColors.InkMute)
            LinearProgressIndicator(
                progress = { pct / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = ThemeColors.Primary,
                trackColor = ThemeColors.Hairline,
            )
            Text("You currently qualify for ${eligible.size} jobs — completing KYC unlocks more.", fontSize = 13.sp, color = ThemeColors.InkMute)
            Spacer(Modifier.height(12.dp))

            // KYC timeline steps
            val aadhaarDone = seeker.aadhaar != null && KycLogic.aadhaarValid(seeker.aadhaar ?: "")
            KycTimelineStep(
                number = 1,
                title = "Aadhaar",
                status = if (aadhaarDone) "Verified" else "Pending",
                subtitle = if (aadhaarDone) "Verified · format check passed" else "Pending upload",
                hint = "12-digit format check only — demo does not contact UIDAI.",
                isActive = true,
                isCompleted = aadhaarDone,
                isLast = false,
            )
            val addressDone = seeker.addressProof
            KycTimelineStep(
                number = 2,
                title = "Address & City",
                status = if (addressDone) "Verified" else "Pending",
                subtitle = if (addressDone) "${seeker.city} · Proof uploaded" else "Pending upload",
                hint = "Changing city re-runs your matches instantly.",
                isActive = aadhaarDone,
                isCompleted = addressDone,
                isLast = false,
            )
            val certsDone = seeker.certificates.isNotEmpty()
            KycTimelineStep(
                number = 3,
                title = "Certificates",
                status = if (certsDone) "Verified (${seeker.certificates.size})" else "Optional",
                subtitle = if (certsDone) seeker.certificates.joinToString(", ") else "Add trade/degree certificates",
                hint = "Write the name exactly as on the certificate — it is matched letter for letter.",
                isActive = addressDone,
                isCompleted = certsDone,
                isLast = true,
            )

            // Section 1: Aadhaar
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("1. Aadhaar Verification", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                    Spacer(Modifier.height(4.dp))
                    if (aadhaarDone) {
                        Text("✓ Aadhaar ${seeker.aadhaar?.take(4)}••••${seeker.aadhaar?.takeLast(4)} saved", fontSize = 13.sp, color = ThemeColors.Primary)
                    } else {
                        OutlinedTextField(
                            value = aadhaarInput,
                            onValueChange = { aadhaarInput = it.filter { c -> c.isDigit() }.take(12); aadhaarError = null },
                            label = { Text("12-digit Aadhaar number") },
                            singleLine = true,
                            isError = aadhaarError != null,
                            supportingText = { if (aadhaarError != null) Text(aadhaarError!!) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = {
                                if (!KycLogic.aadhaarValid(aadhaarInput)) {
                                    aadhaarError = "Enter all 12 digits"
                                } else {
                                    val updated = seeker.copy(aadhaar = aadhaarInput)
                                    store.upsertSeeker(updated)
                                    seeker = store.currentSeeker() ?: updated
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThemeColors.Primary,
                                contentColor = ThemeColors.OnPrimary,
                            ),
                        ) { Text("Verify Aadhaar") }
                    }
                }
            }

            // Section 2: Address & City
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("2. Address & Location", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = cityInput,
                        onValueChange = {
                            cityInput = it
                            val updated = seeker.copy(city = it)
                            store.upsertSeeker(updated)
                            seeker = store.currentSeeker() ?: updated
                        },
                        label = { Text("City") },
                        supportingText = { Text("Changing city re-runs your matches instantly.") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(4.dp))
                    if (addressDone) {
                        Text("✓ Address proof uploaded (${seeker.city})", fontSize = 13.sp, color = ThemeColors.Primary)
                    } else {
                        OutlinedTextField(
                            value = addressInput,
                            onValueChange = { addressInput = it },
                            label = { Text("Full Address / Utility Bill Details") },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = {
                                val updated = seeker.copy(addressProof = true, city = cityInput.ifBlank { seeker.city })
                                store.upsertSeeker(updated)
                                seeker = store.currentSeeker() ?: updated
                                addressSaved = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThemeColors.Primary,
                                contentColor = ThemeColors.OnPrimary,
                            ),
                        ) { Text("Upload Address Proof") }
                    }
                }
            }

            // Section 3: Certificates
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ThemeRadii.Card.dp),
                border = BorderStroke(1.dp, ThemeColors.Hairline),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("3. Certificates & Qualifications", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                    Spacer(Modifier.height(4.dp))
                    if (seeker.certificates.isNotEmpty()) {
                        seeker.certificates.forEach { c ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("✓ $c", fontSize = 14.sp, color = ThemeColors.Ink)
                                TextButton(onClick = {
                                    val updated = seeker.copy(certificates = seeker.certificates - c)
                                    store.upsertSeeker(updated)
                                    seeker = store.currentSeeker() ?: updated
                                }) { Text("Remove", fontSize = 12.sp, color = ThemeColors.Danger) }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = certInput,
                            onValueChange = { certInput = it },
                            label = { Text("e.g. ITI Electrician, Python, B.Tech") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )
                        Button(
                            onClick = {
                                if (certInput.isNotBlank()) {
                                    val updated = seeker.copy(certificates = seeker.certificates + certInput.trim())
                                    store.upsertSeeker(updated)
                                    seeker = store.currentSeeker() ?: updated
                                    certInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThemeColors.Primary,
                                contentColor = ThemeColors.OnPrimary,
                            ),
                        ) { Text("Add") }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Documents stay on this device in the demo.", fontSize = 12.sp, color = ThemeColors.InkMute2)

            Spacer(Modifier.height(16.dp))
            Text("Settings", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            
            SettingsRow(icon = Lucide.Globe, label = "App language", trailing = "English", subtitle = "Hindi / Marathi coming", onClick = {})
            SettingsRow(icon = Lucide.Info, label = "About", trailing = "v0.1.0 (demo)", subtitle = "NaukriSetu v0.1.0 (demo)", onClick = {})
            SettingsRow(icon = Lucide.RotateCcw, label = "Demo reset", trailing = "", subtitle = "Clears all local data", isDestructive = true, onClick = {
                store.upsertSeeker(Seed.demoSeeker)
                seeker = Seed.demoSeeker
            })
            SettingsRow(icon = Lucide.LogOut, label = "Log out", trailing = "", subtitle = "", isDestructive = true, onClick = onLogout)

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun KycTimelineStep(
    number: Int,
    title: String,
    status: String,
    subtitle: String,
    hint: String,
    isActive: Boolean,
    isCompleted: Boolean = false,
    isLast: Boolean,
) {
    val lineColor = if (isLast) androidx.compose.ui.graphics.Color.Transparent else ThemeColors.Hairline
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        // Timeline line + circle
        Column(
            modifier = Modifier
                .size(24.dp)
                .padding(top = 12.dp),
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.size(24.dp)) {
                val circleColor = if (isCompleted) ThemeColors.Primary else if (isActive) ThemeColors.Ink else ThemeColors.InkMute
                drawCircle(circleColor, radius = 12f, style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
                if (isCompleted) {
                    drawCircle(ThemeColors.Primary, radius = 6f)
                }
            }
            if (!isLast) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxHeight().width(2.dp).padding(top = 24.dp, bottom = 12.dp)) {
                    drawLine(color = lineColor, start = androidx.compose.ui.geometry.Offset(1f, 0f), end = androidx.compose.ui.geometry.Offset(1f, size.height), strokeWidth = 2f)
                }
            }
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Row {
                Text("$number. $title", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
                Spacer(Modifier.width(8.dp))
                Text(status, fontSize = 11.sp, color = if (isCompleted) ThemeColors.Primary else ThemeColors.Warning)
            }
            Text(subtitle, fontSize = 13.sp, color = ThemeColors.InkMute)
            Text(hint, fontSize = 11.sp, color = ThemeColors.InkMute2)
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    label: String,
    trailing: String,
    subtitle: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit,
) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(ThemeRadii.Card.dp),
        border = BorderStroke(1.dp, ThemeColors.Hairline),
        colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) ThemeColors.Danger else ThemeColors.InkMute,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(label, fontSize = 14.sp, color = if (isDestructive) ThemeColors.Danger else ThemeColors.Ink)
                if (subtitle.isNotBlank()) Text(subtitle, fontSize = 11.sp, color = ThemeColors.InkMute2)
            }
            Spacer(Modifier.weight(1f))
            if (trailing.isNotBlank()) Text(trailing, fontSize = 13.sp, color = ThemeColors.InkMute)
            androidx.compose.material3.Icon(
                imageVector = Lucide.ChevronRight,
                contentDescription = null,
                tint = ThemeColors.InkMute,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun EmployerProfileStub(onLogout: () -> Unit, onNavigate: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Profile", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
        Text("NaukriSetu v0.1.0 (demo)", fontSize = 13.sp, color = ThemeColors.InkMute)
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onLogout,
            shape = RoundedCornerShape(ThemeRadii.Button.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ThemeColors.Primary, contentColor = ThemeColors.OnPrimary),
        ) { Text("Log out") }
        Spacer(Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(ThemeRadii.Card.dp),
            border = BorderStroke(1.dp, ThemeColors.Hairline),
            colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
        ) {
            Text("Employer tabs live in the verifier/employer bar.", Modifier.padding(16.dp), fontSize = 13.sp, color = ThemeColors.InkMute)
        }
    }
}