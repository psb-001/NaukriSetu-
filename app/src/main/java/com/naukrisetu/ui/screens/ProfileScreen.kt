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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.data.DemoStore
import com.naukrisetu.data.Seed
import com.naukrisetu.engine.seekerVisibleOpps
import com.naukrisetu.engine.splitMatches
import com.naukrisetu.ui.components.SeekerBottomTabs
import com.naukrisetu.ui.kyc.KycLogic
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@Composable
fun ProfileScreen(
    store: DemoStore,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
) {
    var seeker = store.seeker("seeker-ravi") ?: Seed.demoSeeker
    val done = KycLogic.checksDone(seeker)
    val pct = KycLogic.progressPct(seeker)
    var aadhaarInput by remember(seeker.aadhaar) { mutableStateOf(seeker.aadhaar ?: "") }
    var addressInput by remember { mutableStateOf("") }
    var certInput by remember { mutableStateOf("") }
    var aadhaarError by remember { mutableStateOf<String?>(null) }

    val (eligible, _) = splitMatches(seeker, seekerVisibleOpps(store))

    Scaffold(
        bottomBar = { SeekerBottomTabs(current = Routes.profile, onNavigate = onNavigate) },
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

            Text("Aadhaar", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text(if (seeker.aadhaar != null) "Uploaded · awaiting check" else "Pending", fontSize = 13.sp, color = ThemeColors.InkMute)
            Text("12-digit format check only — demo does not contact UIDAI.", fontSize = 12.sp, color = ThemeColors.InkMute2)
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
                    seeker = store.seeker("seeker-ravi")!!
                }
            }) { Text("Save Aadhaar") }

            Spacer(Modifier.height(8.dp))
            Text("Address", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            OutlinedTextField(value = addressInput, onValueChange = { addressInput = it }, label = { Text("Full address") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = seeker.city, onValueChange = {
                store.upsertSeeker(seeker.copy(city = it)); seeker = store.seeker("seeker-ravi")!!
            }, label = { Text("City") }, supportingText = { Text("Changing city re-runs your matches instantly.") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            TextButton(onClick = {
                if (addressInput.isNotBlank()) {
                    store.upsertSeeker(seeker.copy(addressProof = true)); seeker = store.seeker("seeker-ravi")!!
                }
            }) { Text("Take photo or upload address proof") }

            Spacer(Modifier.height(8.dp))
            Text("Certificates", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            seeker.certificates.forEach { c -> Text("• $c", fontSize = 14.sp, color = ThemeColors.Ink) }
            Text("Write the name exactly as on the certificate — it is matched letter for letter.", fontSize = 12.sp, color = ThemeColors.InkMute2)
            OutlinedTextField(value = certInput, onValueChange = { certInput = it }, label = { Text("Add certificate") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            TextButton(onClick = {
                if (certInput.isNotBlank()) {
                    store.upsertSeeker(seeker.copy(certificates = seeker.certificates + certInput.trim()))
                    seeker = store.seeker("seeker-ravi")!!
                    certInput = ""
                }
            }) { Text("Add") }
            Text("Documents stay on this device in the demo.", fontSize = 12.sp, color = ThemeColors.InkMute2)

            Spacer(Modifier.height(12.dp))
            Text("Settings", fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
            Text("App language: English (Hindi / Marathi coming)", fontSize = 13.sp, color = ThemeColors.InkMute)
            Text("About: NaukriSetu v0.1.0 (demo)", fontSize = 13.sp, color = ThemeColors.InkMute)
            Row {
                OutlinedButton(onClick = {
                    store.upsertSeeker(Seed.demoSeeker)
                    seeker = Seed.demoSeeker
                }) { Text("Demo reset") }
                TextButton(onClick = onLogout) { Text("Log out") }
            }
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
