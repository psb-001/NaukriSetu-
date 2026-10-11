package com.naukrisetu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.naukrisetu.ui.copy.Strings
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(
                    primary = ThemeColors.Primary,
                    onPrimary = ThemeColors.OnPrimary,
                    surface = ThemeColors.Canvas,
                    onSurface = ThemeColors.Ink,
                    background = ThemeColors.Canvas,
                    onBackground = ThemeColors.Ink,
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = ThemeColors.Canvas) {
                    val nav = rememberNavController()
                    NavHost(navController = nav, startDestination = Routes.splash) {
                        composable(Routes.splash) { Placeholder(Strings.tagline) }
                        composable(Routes.login) { Placeholder(Strings.loginTitle) }
                        composable(Routes.otp) { Placeholder(Strings.otpTitle) }
                        composable(Routes.onboarding) { Placeholder(Strings.onboardingTitle) }
                        composable(Routes.home) { Placeholder(Routes.home) }
                        composable(Routes.matches) { Placeholder(Routes.matches) }
                        composable(Routes.gaps) { Placeholder(Routes.gaps) }
                        composable(Routes.courses) { Placeholder(Routes.courses) }
                        composable(Routes.profile) { Placeholder(Routes.profile) }
                        composable(Routes.postJob) { Placeholder(Routes.postJob) }
                        composable(Routes.myPostings) { Placeholder(Routes.myPostings) }
                        composable(Routes.kycQueue) { Placeholder(Routes.kycQueue) }
                        composable(Routes.jobsQueue) { Placeholder(Routes.jobsQueue) }
                    }
                }
            }
        }
    }
}

@Composable
private fun Placeholder(text: String) {
    Text(text = text, color = ThemeColors.Ink)
}
