package com.naukrisetu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.ui.copy.Strings
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@Composable
fun SplashScreen(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Naukri", fontSize = 40.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Ink)
        Text("Setu", fontSize = 40.sp, fontWeight = FontWeight.Medium, color = ThemeColors.Primary)
        Spacer(Modifier.height(8.dp))
        Text(Strings.tagline, fontSize = 16.sp, color = ThemeColors.InkMute)
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onGetStarted,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ThemeRadii.Button.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ThemeColors.Primary,
                contentColor = ThemeColors.OnPrimary,
            ),
        ) { Text(Strings.getStarted) }
        Spacer(Modifier.height(12.dp))
        Text(Strings.splashAutoHome, fontSize = 13.sp, color = ThemeColors.InkMute2)
    }
}
