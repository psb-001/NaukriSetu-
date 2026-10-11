package com.naukrisetu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.naukrisetu.data.DemoStore
import com.naukrisetu.data.RemoteStore
import com.naukrisetu.ui.auth.Role
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.screens.CoursesScreen
import com.naukrisetu.ui.screens.GapsScreen
import com.naukrisetu.ui.screens.HomeScreen
import com.naukrisetu.ui.screens.LoginScreen
import com.naukrisetu.ui.screens.MatchesScreen
import com.naukrisetu.ui.screens.MyPostingsScreen
import com.naukrisetu.ui.screens.OnboardingScreen
import com.naukrisetu.ui.screens.PostJobScreen
import com.naukrisetu.ui.screens.ProfileScreen
import com.naukrisetu.ui.screens.VerifierScreen
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
                    var store by remember { mutableStateOf<DemoStore>(DemoStore()) }
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
                                    // OTP verified by Supabase — load remote profile
                                    val remote = RemoteStore(pendingEmail)
                                    lifecycleScope.launch {
                                        remote.load()
                                        store = remote
                                        nav.navigate(routeFor(pendingRole)) {
                                            popUpTo(Routes.splash) { inclusive = true }
                                        }
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
                        composable(Routes.home) {
                            HomeScreen(
                                store = store,
                                onNavigate = { nav.navigate(it) },
                                onHowToQualify = { oppId -> nav.navigate("${Routes.gaps}/$oppId") },
                            )
                        }
                        composable(Routes.matches) {
                            MatchesScreen(
                                store = store,
                                onNavigate = { nav.navigate(it) },
                                onHowToQualify = { oppId -> nav.navigate("${Routes.gaps}/$oppId") },
                            )
                        }
                        composable("${Routes.gaps}/{oppId}") { backStack ->
                            GapsScreen(
                                oppId = backStack.arguments?.getString("oppId") ?: "",
                                store = store,
                                onNavigate = { nav.navigate(it) },
                                onOpenCourses = { nav.navigate(Routes.courses) },
                            )
                        }
                        composable(Routes.courses) {
                            CoursesScreen(onNavigate = { nav.navigate(it) })
                        }
                        composable(Routes.profile) {
                            ProfileScreen(
                                store = store,
                                onNavigate = { nav.navigate(it) },
                                onLogout = {
                                    store.clear()
                                    store = DemoStore()
                                    nav.navigate(Routes.login) {
                                        popUpTo(Routes.splash) { inclusive = true }
                                    }
                                },
                            )
                        }
                        composable(Routes.postJob) {
                            PostJobScreen(
                                store = store,
                                onNavigate = { nav.navigate(it) },
                                onSubmitted = { nav.navigate(Routes.myPostings) },
                            )
                        }
                        composable(Routes.myPostings) {
                            MyPostingsScreen(store = store, onNavigate = { nav.navigate(it) })
                        }
                        composable(Routes.kycQueue) {
                            VerifierScreen(store = store, onNavigate = { nav.navigate(it) })
                        }
                        composable(Routes.jobsQueue) {
                            VerifierScreen(store = store, onNavigate = { nav.navigate(it) })
                        }
                    }
                }
            }
        }
    }
}

private fun routeFor(role: Role): String = when (role) {
    Role.Seeker   -> Routes.onboarding
    Role.Employer -> Routes.postJob
    Role.Verifier -> Routes.kycQueue
}
