package com.naukrisetu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.R
import com.naukrisetu.ui.copy.Strings
import com.naukrisetu.ui.theme.ThemeColors
import com.naukrisetu.ui.theme.ThemeRadii

@Composable
fun Wordmark(fontSize: Int = 40, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text("Naukri", fontSize = fontSize.sp, fontWeight = FontWeight.SemiBold, color = ThemeColors.Ink)
        Text(" Setu", fontSize = fontSize.sp, fontWeight = FontWeight.SemiBold, color = ThemeColors.Primary)
    }
}

@Composable
fun SplashScreen(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))
        Image(
            painter = painterResource(R.drawable.app_logo),
            contentDescription = "NaukriSetu Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(160.dp),
        )
        Spacer(Modifier.height(24.dp))
        Wordmark()
        Spacer(Modifier.height(4.dp))
        Text(Strings.tagline, fontSize = 16.sp, color = ThemeColors.InkMute)
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onGetStarted,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ThemeRadii.Button.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ThemeColors.Primary,
                contentColor = ThemeColors.OnPrimary,
            ),
        ) { Text(Strings.getStarted, fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
        Spacer(Modifier.height(8.dp))
        Text(Strings.splashAutoHome, fontSize = 13.sp, color = ThemeColors.InkMute2)
        Spacer(Modifier.height(16.dp))
    }
}
