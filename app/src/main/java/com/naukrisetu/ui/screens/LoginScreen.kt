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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.data.SupabaseApi
import com.naukrisetu.ui.auth.EmailCheck
import com.naukrisetu.ui.auth.Role
import com.naukrisetu.ui.copy.Strings
import com.naukrisetu.ui.icons.Lucide
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(onLoggedIn: (email: String, role: Role) -> Unit) {
    // 0 = Student Portal, 1 = Govt / Employer Adder Portal
    var portalMode by remember { mutableStateOf(0) }
    var email by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun submit(role: Role) {
        if (!EmailCheck.valid(email)) {
            error = Strings.emailError
            return
        }
        error = null
        loading = true
        scope.launch {
            try {
                withContext(Dispatchers.IO) { SupabaseApi.sendOtp(email.trim()) }
            } catch (_: Exception) {
                // Rate-limited fallback on Supabase free tier (demo bypass available)
            }
            loading = false
            onLoggedIn(email.trim(), role)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(20.dp))
        Wordmark(fontSize = 24)
        Spacer(Modifier.height(18.dp))

        // PORTAL SWITCHER: Student vs Govt / Adder Portal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = { portalMode = 0; error = null },
                modifier = Modifier.weight(1f).height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (portalMode == 0) ThemeColors.Primary else ThemeColors.CanvasSoft,
                    contentColor = if (portalMode == 0) Color.White else ThemeColors.InkSecondary,
                ),
                border = BorderStroke(1.dp, if (portalMode == 0) ThemeColors.Primary else ThemeColors.Hairline),
            ) {
                Text(
                    "🎓 Student Portal",
                    fontSize = 13.sp,
                    fontWeight = if (portalMode == 0) FontWeight.Bold else FontWeight.Medium,
                )
            }

            Button(
                onClick = { portalMode = 1; error = null },
                modifier = Modifier.weight(1f).height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (portalMode == 1) ThemeColors.Primary else ThemeColors.CanvasSoft,
                    contentColor = if (portalMode == 1) Color.White else ThemeColors.InkSecondary,
                ),
                border = BorderStroke(1.dp, if (portalMode == 1) ThemeColors.Primary else ThemeColors.Hairline),
            ) {
                Text(
                    "🏛️ Job Adder Portal",
                    fontSize = 13.sp,
                    fontWeight = if (portalMode == 1) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Portal specific title & blurb
        if (portalMode == 0) {
            Text(
                "Student & Fresher Access",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.Ink,
            )
            Text(
                "Log in to find verified internships & tech jobs mapped directly to your skill graph.",
                fontSize = 13.sp,
                color = ThemeColors.InkMute,
            )
        } else {
            Text(
                "Govt & Corporate Job Adder",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.Ink,
            )
            Text(
                "Publish official government internships, public sector roles, and tech vacancies directly into the RozgarLink engine.",
                fontSize = 13.sp,
                color = ThemeColors.InkMute,
            )
        }

        Spacer(Modifier.height(18.dp))

        Text(
            if (portalMode == 0) "Student College / Personal Email" else "Official Govt / Corporate Email",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = ThemeColors.Ink,
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; error = null },
            placeholder = {
                Text(if (portalMode == 0) "e.g. student@naukrisetu.com" else "e.g. officer@nic.in or hr@techcorp.com")
            },
            isError = error != null,
            supportingText = { if (error != null) Text(error!!, color = ThemeColors.Danger) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
        )
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { submit(if (portalMode == 0) Role.Seeker else Role.Employer) },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(ThemeRadii.Button.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ThemeColors.Primary,
                contentColor = Color.White,
            ),
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    if (portalMode == 0) "Enter Student Portal (OTP)" else "Enter Job Adder Portal (OTP)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // 1-Tap Quick Demo Profiles
        Text(
            if (portalMode == 0) "1-Tap Student Demo Logins:" else "1-Tap Govt / Adder Demo Logins:",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = ThemeColors.InkMute,
        )
        Spacer(Modifier.height(8.dp))

        if (portalMode == 0) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = {
                        email = "student@naukrisetu.com"
                        submit(Role.Seeker)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeColors.CanvasSoft,
                        contentColor = ThemeColors.Ink,
                    ),
                    border = BorderStroke(1.dp, ThemeColors.HairlineStrong),
                ) {
                    Text("🎓 Aman (B.Tech CS Student)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Button(
                    onClick = {
                        email = "student.web@naukrisetu.com"
                        submit(Role.Seeker)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeColors.CanvasSoft,
                        contentColor = ThemeColors.Ink,
                    ),
                    border = BorderStroke(1.dp, ThemeColors.HairlineStrong),
                ) {
                    Text("👩‍💻 Pooja (BCA Web Intern)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = {
                        email = "officer.nic@gov.in"
                        submit(Role.Employer)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeColors.CanvasSoft,
                        contentColor = ThemeColors.Ink,
                    ),
                    border = BorderStroke(1.dp, ThemeColors.HairlineStrong),
                ) {
                    Text("🏛️ NIC Govt Nodal Officer", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Button(
                    onClick = {
                        email = "employer@techcorp.com"
                        submit(Role.Employer)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ThemeColors.CanvasSoft,
                        contentColor = ThemeColors.Ink,
                    ),
                    border = BorderStroke(1.dp, ThemeColors.HairlineStrong),
                ) {
                    Text("🏢 TechCorp Recruiter", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Info card explaining the G-01 RozgarLink engine separation
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ThemeRadii.Card.dp),
            colors = CardDefaults.cardColors(containerColor = ThemeColors.CanvasSoft),
            border = BorderStroke(1.dp, ThemeColors.Hairline),
        ) {
            Column(Modifier.padding(14.dp)) {
                Text(
                    "⚡ RozgarLink G-01 Two-Sided Platform",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ThemeColors.Ink,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "1. Government/employers add requirements with mandatory hard gates (city, min experience, qualification).\n2. Students specify tech skills and projects.\n3. The engine computes strict eligibility first, then ranks eligible matches with plain-language explanations.",
                    fontSize = 12.sp,
                    color = ThemeColors.InkMute,
                    lineHeight = 17.sp,
                )
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}
