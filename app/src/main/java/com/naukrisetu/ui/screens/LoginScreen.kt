package com.naukrisetu.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.auth.GoogleAuthHelper
import com.naukrisetu.ui.auth.EmailCheck
import com.naukrisetu.ui.auth.Role
import com.naukrisetu.ui.components.InfoBox
import com.naukrisetu.ui.copy.Strings
import com.naukrisetu.ui.icons.GoogleG
import com.naukrisetu.ui.icons.Lucide
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@Composable
fun LoginScreen(onLoggedIn: (email: String, role: Role) -> Unit) {
    var email by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    fun submit(role: Role) {
        if (!EmailCheck.valid(email)) {
            error = Strings.gmailError
            return
        }
        error = null
        onLoggedIn(email.trim(), role)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(24.dp))
        Wordmark(fontSize = 22)
        Spacer(Modifier.height(24.dp))
        Text(Strings.loginTitle, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = ThemeColors.Ink)
        Text(Strings.loginSubtitle, fontSize = 14.sp, color = ThemeColors.InkMute)
        Spacer(Modifier.height(20.dp))
        // Real Google OAuth via Supabase Custom Tab (falls back to demo if not configured)
        Button(
            onClick = { GoogleAuthHelper.launchGoogleSignIn(context) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(ThemeRadii.Button.dp),
            border = BorderStroke(1.dp, ThemeColors.HairlineStrong),
            colors = ButtonDefaults.buttonColors(
                containerColor = ThemeColors.Canvas,
                contentColor = ThemeColors.Ink,
            ),
        ) {
            Icon(imageVector = GoogleG.Logo, contentDescription = null, tint = androidx.compose.ui.graphics.Color.Unspecified, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(Strings.continueWithGoogle, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        InfoBox(icon = Lucide.Info, text = "${Strings.demoHintPrefix} ${Strings.demoOtp}")
        Spacer(Modifier.height(12.dp))
        Text(Strings.gmailLabel, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; error = null },
            placeholder = { Text("ravi.kadam@gmail.com") },
            isError = error != null,
            supportingText = { Text(error ?: Strings.gmailHint) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { submit(Role.Seeker) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(ThemeRadii.Button.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ThemeColors.Primary,
                contentColor = ThemeColors.OnPrimary,
            ),
        ) { Text(Strings.continueLabel, fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = ThemeColors.Hairline)
            Text(Strings.orContinueAs, fontSize = 12.sp, color = ThemeColors.InkMute2, modifier = Modifier.padding(horizontal = 12.dp))
            HorizontalDivider(modifier = Modifier.weight(1f), color = ThemeColors.Hairline)
        }
        Spacer(Modifier.height(16.dp))
        RoleCard(icon = Lucide.BriefcaseBusiness, title = Strings.continueAsEmployer, blurb = Strings.employerBlurb) { submit(Role.Employer) }
        Spacer(Modifier.height(12.dp))
        RoleCard(icon = Lucide.ShieldCheck, title = Strings.continueAsVerifier, blurb = Strings.verifierBlurb) { submit(Role.Verifier) }
        Spacer(Modifier.weight(1f))
        Text(
            Strings.demoOnDevice,
            fontSize = 12.sp,
            color = ThemeColors.InkMute2,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp),
        )
    }
}

@Composable
private fun RoleCard(icon: ImageVector, title: String, blurb: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ThemeRadii.Card.dp),
        border = BorderStroke(1.dp, ThemeColors.HairlineStrong),
        colors = CardDefaults.cardColors(containerColor = ThemeColors.Canvas),
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = ThemeColors.CanvasSoft),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ThemeColors.Ink,
                    modifier = Modifier.padding(10.dp).size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = ThemeColors.Ink)
                Text(blurb, fontSize = 13.sp, color = ThemeColors.InkMute)
            }
        }
    }
}
