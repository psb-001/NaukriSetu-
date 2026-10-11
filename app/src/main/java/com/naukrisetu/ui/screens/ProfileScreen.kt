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
    var seeker = store.currentSeeker() ?: Seed.demoSeeker
    val done = KycLogic.checksDone(seeker)
    val pct = KycLogic.progressPct(seeker)
    var aadhaarInput by remember(seeker.aadhaar) { mutableStateOf(seeker.aadhaar ?: "") }
    var addressInput by remember { mutableStateOf("") }
    var certInput by remember { mutableStateOf("") }
    var aadhaarError by remember { mutableStateOf<String?>(null) }

    val (eligible, _) = splitMatches(seeker, seekerVisibleOpps(store))

    Scaffold(
        bottomBar = { SeekerTabs(current = Routes.profile, onNavigate = onNavigate) },
        containerColor = ThemeColors.Canvas,
    ) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Profile", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("${seeker.name} · ${seeker.email}", fontSize = 13.sp, color = ThemeColors.InkMute)
            Text("Verification pending — you can browse, some jobs stay locked.", fontSize = 13.sp, color = ThemeColors.InkMute)
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

            // KYC timeline steps (per M10)
            KycTimelineStep(
                number = 1,
                title = "Aadhaar",
                status = if (seeker.aadhaar != null) "Pending" else "Pending",
                subtitle = if (seeker.aadhaar != null) "Uploaded · awaiting check" else "Pending",
                hint = "12-digit format check only — demo does not contact UIDAI.",
                isActive = true,
                isLast = false,
            )
            KycTimelineStep(
                number = 2,
                title = "Address",
                status = if (seeker.addressProof) "Uploaded" else "Pending",
                subtitle = if (seeker.addressProof) "Uploaded · awaiting check" else "Pending",
                hint = "Changing city re-runs your matches instantly.",
                isActive = seeker.aadhaar != null,
                isLast = false,
            )
            KycTimelineStep(
                number = 3,
                title = "Certificates",
                status = if (seeker.certificates.isNotEmpty()) "Uploaded" else "Pending",
                subtitle = seeker.certificates.joinToString(", ") { "$it · awaiting check" },
                hint = "Write the name exactly as on the certificate — it is matched letter for letter.",
                isActive = seeker.addressProof,
                isLast = true,
            )

            // Input forms — step-by-step KYC progression
            if (seeker.aadhaar == null) {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = aadhaarInput,
                    onValueChange = { aadhaarInput = it.filter { c -> c.isDigit() }.take(12); aadhaarError = null },
                    label = { Text("12-digit Aadhaar number") },
                    singleLine = true,
                    isError = aadhaarError != null,
                    supportingText = { if (aadhaarError != null) Text(aadhaarError!!) },
                    modifier = Modifier.fillMaxWidth(),
                )
                TextButton(onClick = {
                    if (!KycLogic.aadhaarValid(aadhaarInput)) {
                        aadhaarError = "Enter all 12 digits"
                    } else {
                        store.upsertSeeker(seeker.copy(aadhaar = aadhaarInput))
                        seeker = store.currentSeeker() ?: seeker
                    }
                }) { Text("Save Aadhaar") }
            }

            if (seeker.aadhaar != null && !seeker.addressProof) {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = addressInput, onValueChange = { addressInput = it }, label = { Text("Full address") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = seeker.city, onValueChange = {
                    store.upsertSeeker(seeker.copy(city = it)); seeker = store.currentSeeker() ?: seeker
                }, label = { Text("City") }, supportingText = { Text("Changing city re-runs your matches instantly.") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                TextButton(onClick = {
                    if (addressInput.isNotBlank()) {
                        store.upsertSeeker(seeker.copy(addressProof = true)); seeker = store.currentSeeker() ?: seeker
                    }
                }) { Text("Take photo or upload address proof") }
            }

            if (seeker.addressProof && seeker.certificates.isEmpty()) {
                Spacer(Modifier.height(8.dp))
                seeker.certificates.forEach { c -> Text("• $c", fontSize = 14.sp, color = ThemeColors.Ink) }
                OutlinedTextField(value = certInput, onValueChange = { certInput = it }, label = { Text("Add certificate") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                TextButton(onClick = {
                    if (certInput.isNotBlank()) {
                        store.upsertSeeker(seeker.copy(certificates = seeker.certificates + certInput.trim()))
                        seeker = store.currentSeeker() ?: seeker
                        certInput = ""
                    }
                }) { Text("Add") }
            }
            Text("Documents stay on this device in the demo.", fontSize = 12.sp, color = ThemeColors.InkMute2)

            Spacer(Modifier.height(12.dp))
            Text("Settings", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            
            SettingsRow(icon = Lucide.Globe, label = "App language", trailing = "English", subtitle = "Hindi / Marathi coming", onClick = {})
            SettingsRow(icon = Lucide.Info, label = "About", trailing = "v0.1.0 (demo)", subtitle = "NaukriSetu v0.1.0 (demo)", onClick = {})
            SettingsRow(icon = Lucide.RotateCcw, label = "Demo reset", trailing = "", subtitle = "Clears all local data", isDestructive = true, onClick = {
                store.upsertSeeker(Seed.demoSeeker)
                seeker = Seed.demoSeeker
            })
            SettingsRow(icon = Lucide.LogOut, label = "Log out", trailing = "", subtitle = "", isDestructive = true, onClick = onLogout)

            Spacer(Modifier.height(8.dp))
            Text("Documents stay on this device in the demo.", fontSize = 12.sp, color = ThemeColors.InkMute2)
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
                val circleColor = if (number <= 2) ThemeColors.Primary else ThemeColors.InkMute
                drawCircle(circleColor, radius = 12f, style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
                if (number <= 2) {
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
                Text(status, fontSize = 11.sp, color = if (status == "Pending") ThemeColors.Warning else ThemeColors.Primary)
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