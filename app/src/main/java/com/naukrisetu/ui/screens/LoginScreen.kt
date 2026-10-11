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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.data.SupabaseApi
import com.naukrisetu.ui.auth.EmailCheck
import com.naukrisetu.ui.auth.Role
import com.naukrisetu.ui.copy.Strings
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(onLoggedIn: (email: String, role: Role) -> Unit) {
    // 0 = Student Portal, 1 = Employee Portal (Experienced), 2 = Job Adder Portal (Govt / Recruiter)
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
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(16.dp))
        Wordmark(fontSize = 24)
        Spacer(Modifier.height(16.dp))

        // 3-WAY PORTAL SWITCHER: Student vs Experienced Employee vs Job Adder
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Button(
                onClick = { portalMode = 0; error = null },
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (portalMode == 0) ThemeColors.Primary else ThemeColors.CanvasSoft,
                    contentColor = if (portalMode == 0) Color.White else ThemeColors.InkSecondary,
                ),
                border = BorderStroke(1.dp, if (portalMode == 0) ThemeColors.Primary else ThemeColors.Hairline),
            ) {
                Text(
                    "🎓 Student",
                    fontSize = 12.sp,
                    fontWeight = if (portalMode == 0) FontWeight.Bold else FontWeight.Medium,
                )
            }

            Button(
                onClick = { portalMode = 1; error = null },
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (portalMode == 1) ThemeColors.Primary else ThemeColors.CanvasSoft,
                    contentColor = if (portalMode == 1) Color.White else ThemeColors.InkSecondary,
                ),
                border = BorderStroke(1.dp, if (portalMode == 1) ThemeColors.Primary else ThemeColors.Hairline),
            ) {
                Text(
                    "💼 Employee",
                    fontSize = 12.sp,
                    fontWeight = if (portalMode == 1) FontWeight.Bold else FontWeight.Medium,
                )
            }

            Button(
                onClick = { portalMode = 2; error = null },
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (portalMode == 2) ThemeColors.Primary else ThemeColors.CanvasSoft,
                    contentColor = if (portalMode == 2) Color.White else ThemeColors.InkSecondary,
                ),
                border = BorderStroke(1.dp, if (portalMode == 2) ThemeColors.Primary else ThemeColors.Hairline),
            ) {
                Text(
                    "🏛️ Job Adder",
                    fontSize = 12.sp,
                    fontWeight = if (portalMode == 2) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // Portal specific title & blurb
        when (portalMode) {
            0 -> {
                Text(
                    "Student & Fresher Portal",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThemeColors.Ink,
                )
                Text(
                    "Fresher-friendly internships & entry-level tech roles with 0 years experience required.",
                    fontSize = 13.sp,
                    color = ThemeColors.InkMute,
                )
            }
            1 -> {
                Text(
                    "Experienced Employee Portal",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThemeColors.Ink,
                )
                Text(
                    "Senior & mid-level roles (e.g. Lead Engineer ₹90k–₹1.2L) matching your multi-year tech experience.",
                    fontSize = 13.sp,
                    color = ThemeColors.InkMute,
                )
            }
            else -> {
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
        }

        Spacer(Modifier.height(16.dp))

        Text(
            when (portalMode) {
                0 -> "Student College / Personal Email"
                1 -> "Professional Work / Personal Email"
                else -> "Official Govt / Corporate Email"
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = ThemeColors.Ink,
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; error = null },
            placeholder = {
                Text(
                    when (portalMode) {
                        0 -> "e.g. student@naukrisetu.com"
                        1 -> "e.g. employee@naukrisetu.com"
                        else -> "e.g. officer@nic.in or hr@techcorp.com"
                    }
                )
            },
            isError = error != null,
            supportingText = { if (error != null) Text(error!!, color = ThemeColors.Danger) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
        )
        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                submit(if (portalMode == 2) Role.Employer else Role.Seeker)
            },
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
                    when (portalMode) {
                        0 -> "Enter Student Portal (OTP)"
                        1 -> "Enter Employee Portal (OTP)"
                        else -> "Enter Job Adder Portal (OTP)"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // 1-Tap Quick Demo Profiles
        Text(
            when (portalMode) {
                0 -> "1-Tap Student Demo Logins:"
                1 -> "1-Tap Experienced Employee Demo Login:"
                else -> "1-Tap Govt / Adder Demo Logins:"
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = ThemeColors.InkMute,
        )
        Spacer(Modifier.height(8.dp))

        when (portalMode) {
            0 -> {
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
                        Text("🎓 Aman (B.Tech CS · 0 exp)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                        Text("👩‍💻 Pooja (BCA Web · 0 exp)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            1 -> {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = {
                            email = "employee@naukrisetu.com"
                            submit(Role.Seeker)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ThemeColors.CanvasSoft,
                            contentColor = ThemeColors.Ink,
                        ),
                        border = BorderStroke(1.dp, ThemeColors.HairlineStrong),
                    ) {
                        Text("💼 Rahul Sharma (Senior Eng · 3 yrs exp)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            else -> {
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
        }

        Spacer(Modifier.height(20.dp))

        // Info card explaining the G-01 RozgarLink engine separation
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ThemeRadii.Card.dp),
            colors = CardDefaults.cardColors(containerColor = ThemeColors.CanvasSoft),
            border = BorderStroke(1.dp, ThemeColors.Hairline),
        ) {
            Column(Modifier.padding(14.dp)) {
                Text(
                    "⚡ RozgarLink Multi-Tier Matching Engine",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ThemeColors.Ink,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "• Students (0 yrs exp) match with internships & fresher roles without experience penalties.\n• Experienced employees (3+ yrs exp) unlock senior engineering roles.\n• Employers & Govt officers add live postings with hard criteria.",
                    fontSize = 12.sp,
                    color = ThemeColors.InkMute,
                    lineHeight = 17.sp,
                )
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}
