package com.naukrisetu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.naukrisetu.data.DemoStore
import com.naukrisetu.ui.auth.Role
import com.naukrisetu.ui.copy.Strings
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.screens.LoginScreen
import com.naukrisetu.ui.screens.OnboardingScreen
import com.naukrisetu.ui.screens.OtpScreen
import com.naukrisetu.ui.screens.SplashScreen
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
                    val store = remember { DemoStore() }
                    var pendingEmail by remember { mutableStateOf("") }
                    var pendingRole by remember { mutableStateOf(Role.Seeker) }
                    NavHost(navController = nav, startDestination = Routes.splash) {
                        composable(Routes.splash) {
                            SplashScreen(onGetStarted = { nav.navigate(Routes.login) })
                        }
                        composable(Routes.login) {
                            LoginScreen(onLoggedIn = { email, role ->
                                pendingEmail = email
                                pendingRole = role
                                nav.navigate(Routes.otp)
                            })
                        }
                        composable(Routes.otp) {
                            OtpScreen(
                                email = pendingEmail,
                                onVerified = {
                                    nav.navigate(routeFor(pendingRole)) {
                                        popUpTo(Routes.splash) { inclusive = true }
                                    }
                                },
                                onChangeEmail = { nav.popBackStack() },
                            )
                        }
                        composable(Routes.onboarding) {
                            OnboardingScreen(
                                email = pendingEmail,
                                store = store,
                                onFinished = {
                                    nav.navigate(Routes.home) {
                                        popUpTo(Routes.splash) { inclusive = true }
                                    }
                                },
                            )
                        }
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

private fun routeFor(role: Role): String = when (role) {
    Role.Seeker -> Routes.onboarding
    Role.Employer -> Routes.postJob
    Role.Verifier -> Routes.kycQueue
}
