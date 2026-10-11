package com.naukrisetu.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.naukrisetu.ui.auth.EmailCheck
import com.naukrisetu.ui.auth.Role
import com.naukrisetu.ui.copy.Strings
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@Composable
fun LoginScreen(onLoggedIn: (email: String, role: Role) -> Unit) {
    var email by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    fun submit(role: Role) {
        if (!EmailCheck.valid(email)) {
            error = Strings.gmailError
            return
        }
        error = null
        onLoggedIn(email.trim(), role)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))
        Text("Naukri", fontSize = 32.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
        Text("Setu", fontSize = 32.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Primary)
        Spacer(Modifier.height(8.dp))
        Text(Strings.loginTitle, fontSize = 18.sp, color = ThemeColors.Ink)
        Text(Strings.loginSubtitle, fontSize = 14.sp, color = ThemeColors.InkMute)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; error = null },
            label = { Text(Strings.gmailLabel) },
            placeholder = { Text("ravi.kadam@gmail.com") },
            isError = error != null,
            supportingText = { if (error != null) Text(error!!) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { submit(Role.Seeker) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ThemeRadii.Button.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ThemeColors.Primary,
                contentColor = ThemeColors.OnPrimary,
            ),
        ) { Text(Strings.continueWithGoogle) }
        Spacer(Modifier.height(4.dp))
        Text(Strings.demoHint, fontSize = 12.sp, color = ThemeColors.InkMute2)
        Spacer(Modifier.height(16.dp))
        Text(Strings.orContinueAs, fontSize = 13.sp, color = ThemeColors.InkMute)
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { submit(Role.Employer) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ThemeRadii.Button.dp),
        ) { Text("${Strings.continueAsEmployer} — ${Strings.employerBlurb}") }
        OutlinedButton(
            onClick = { submit(Role.Verifier) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ThemeRadii.Button.dp),
        ) { Text("${Strings.continueAsVerifier} — ${Strings.verifierBlurb}") }
        Spacer(Modifier.weight(1f))
        Text(Strings.demoOnDevice, fontSize = 12.sp, color = ThemeColors.InkMute2)
    }
}
