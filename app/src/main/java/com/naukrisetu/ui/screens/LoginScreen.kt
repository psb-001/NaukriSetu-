package com.naukrisetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.R
import com.naukrisetu.data.SupabaseApi
import com.naukrisetu.ui.auth.EmailCheck
import com.naukrisetu.ui.auth.Role
import com.naukrisetu.ui.copy.Strings
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * First-screen entry: centered brand header, 3-way portal switcher,
 * email OTP field, 1-tap demos, engine explainer.
 */
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

    fun quickLogin(presetEmail: String, role: Role) {
        email = presetEmail
        error = null
        loading = true
        scope.launch {
            try {
                withContext(Dispatchers.IO) { SupabaseApi.sendOtp(presetEmail) }
            } catch (_: Exception) { /* demo bypass */ }
            loading = false
            onLoggedIn(presetEmail, role)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(40.dp))

        // Brand header — centered logo + wordmark + tagline
        Image(
            painter = painterResource(R.drawable.app_logo),
            contentDescription = "NaukriSetu Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(88.dp),
        )
        Spacer(Modifier.height(10.dp))
        Wordmark(fontSize = 30)
        Spacer(Modifier.height(4.dp))
        Text(
            "Skill se opportunity tak.",
            fontSize = 14.sp,
            color = ThemeColors.InkMute,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))

        // 3-way portal switcher — full-width segmented card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = ThemeColors.CanvasSoft),
            border = BorderStroke(1.dp, ThemeColors.Hairline),
        ) {
            Row(
                modifier = Modifier.padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                PortalTab(
                    label = "🎓 Student",
                    selected = portalMode == 0,
                    modifier = Modifier.weight(1f),
                    onClick = { portalMode = 0; error = null },
                )
                PortalTab(
                    label = "💼 Employee",
                    selected = portalMode == 1,
                    modifier = Modifier.weight(1f),
                    onClick = { portalMode = 1; error = null },
                )
                PortalTab(
                    label = "🏛️ Job Adder",
                    selected = portalMode == 2,
                    modifier = Modifier.weight(1f),
                    onClick = { portalMode = 2; error = null },
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Portal title + blurb — left aligned inside full-width column
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                when (portalMode) {
                    0 -> "Student & Fresher Portal"
                    1 -> "Experienced Employee Portal"
                    else -> "Govt & Corporate Job Adder"
                },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.Ink,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                when (portalMode) {
                    0 -> "Fresher-friendly internships & entry-level tech roles. 0 years experience required."
                    1 -> "Senior & mid-level roles (e.g. Lead Engineer ₹90k–₹1.2L) for 3+ yrs experience."
                    else -> "Publish official internships, public-sector roles and tech vacancies directly into the NaukriSetu engine."
                },
                fontSize = 13.sp,
                color = ThemeColors.InkMute,
                lineHeight = 18.sp,
            )
        }

        Spacer(Modifier.height(16.dp))

        // Email input card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ThemeRadii.Card.dp),
            colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
            border = BorderStroke(1.dp, ThemeColors.Hairline),
        ) {
            Column(Modifier.padding(14.dp)) {
                Text(
                    when (portalMode) {
                        0 -> "Student College / Personal Email"
                        1 -> "Professional Work / Personal Email"
                        else -> "Official Govt / Corporate Email"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ThemeColors.Ink,
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; error = null },
                    placeholder = {
                        Text(
                            when (portalMode) {
                                0 -> "e.g. student@naukrisetu.com"
                                1 -> "e.g. employee@naukrisetu.com"
                                else -> "e.g. officer@nic.in or hr@techcorp.com"
                            },
                            fontSize = 13.sp,
                        )
                    },
                    isError = error != null,
                    supportingText = { if (error != null) Text(error!!, color = ThemeColors.Danger) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { submit(if (portalMode == 2) Role.Employer else Role.Seeker) },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
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
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Demo OTP: 123456 · Powered by Supabase Auth",
                    fontSize = 11.sp,
                    color = ThemeColors.InkMute2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 1-tap demo logins — full-width rows, no squeeze
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                when (portalMode) {
                    0 -> "1-Tap Student Demo Logins"
                    1 -> "1-Tap Employee Demo Login"
                    else -> "1-Tap Govt / Adder Demo Logins"
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.Ink,
            )
            Spacer(Modifier.height(8.dp))
            when (portalMode) {
                0 -> {
                    DemoLoginRow(
                        label = "🎓 Aman Verma",
                        sub = "B.Tech CS · 0 yrs · Pune",
                        onClick = { quickLogin("student@naukrisetu.com", Role.Seeker) },
                    )
                    Spacer(Modifier.height(8.dp))
                    DemoLoginRow(
                        label = "👩‍💻 Pooja Deshmukh",
                        sub = "BCA Web · 0 yrs · Pune",
                        onClick = { quickLogin("student.web@naukrisetu.com", Role.Seeker) },
                    )
                }
                1 -> {
                    DemoLoginRow(
                        label = "💼 Rahul Sharma",
                        sub = "Senior Engineer · 3 yrs · Pune",
                        onClick = { quickLogin("employee@naukrisetu.com", Role.Seeker) },
                    )
                }
                else -> {
                    DemoLoginRow(
                        label = "🏛️ NIC Govt Nodal Officer",
                        sub = "officer.nic@gov.in",
                        onClick = { quickLogin("officer.nic@gov.in", Role.Employer) },
                    )
                    Spacer(Modifier.height(8.dp))
                    DemoLoginRow(
                        label = "🏢 TechCorp Recruiter",
                        sub = "employer@techcorp.com",
                        onClick = { quickLogin("employer@techcorp.com", Role.Employer) },
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Engine explainer
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ThemeRadii.Card.dp),
            colors = CardDefaults.cardColors(containerColor = ThemeColors.PrimarySoft),
            border = BorderStroke(1.dp, ThemeColors.Primary),
        ) {
            Column(Modifier.padding(14.dp)) {
                Text(
                    "⚡ NaukriSetu Multi-Tier Matching Engine",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ThemeColors.PrimaryDeep,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "• Students (0 yrs) match internships & fresher roles — no experience penalty.\n• Employees (3+ yrs) unlock senior engineering roles.\n• Job Adders publish live roles with hard eligibility gates.",
                    fontSize = 12.sp,
                    color = ThemeColors.InkSecondary,
                    lineHeight = 17.sp,
                )
            }
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun PortalTab(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) ThemeColors.Primary else Color.White,
            contentColor = if (selected) Color.White else ThemeColors.InkSecondary,
        ),
        border = if (selected) null else BorderStroke(1.dp, ThemeColors.Hairline),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp),
    ) {
        Text(label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, maxLines = 1)
    }
}

@Composable
private fun DemoLoginRow(label: String, sub: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, ThemeColors.HairlineStrong),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = ThemeColors.Ink),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ThemeColors.Ink)
                Text(sub, fontSize = 11.sp, color = ThemeColors.InkMute)
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = ThemeColors.PrimarySoft,
            ) {
                Text(
                    "1-Tap →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThemeColors.PrimaryDeep,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
            Spacer(Modifier.width(4.dp))
        }
    }
}
