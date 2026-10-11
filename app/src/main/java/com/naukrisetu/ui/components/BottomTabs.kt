package com.naukrisetu.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors

@Composable
fun SeekerBottomTabs(current: String, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = ThemeColors.Canvas) {
        SeekerTab("Home", Routes.home, current, onNavigate)
        SeekerTab("Matches", Routes.matches, current, onNavigate)
        SeekerTab("Courses", Routes.courses, current, onNavigate)
        SeekerTab("Profile", Routes.profile, current, onNavigate)
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.SeekerTab(
    label: String,
    route: String,
    current: String,
    onNavigate: (String) -> Unit,
) {
    NavigationBarItem(
        selected = current == route,
        onClick = { if (current != route) onNavigate(route) },
        icon = { Text(if (current == route) "●" else "○", color = ThemeColors.Primary) },
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(
            selectedTextColor = ThemeColors.Ink,
            unselectedTextColor = ThemeColors.InkMute,
            indicatorColor = ThemeColors.CanvasSoft,
        ),
    )
}

@Composable
fun EmployerBottomTabs(current: String, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = ThemeColors.Canvas) {
        EmployerTab("Post Job", Routes.postJob, current, onNavigate)
        EmployerTab("My Postings", Routes.myPostings, current, onNavigate)
        EmployerTab("Profile", Routes.profile, current, onNavigate)
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.EmployerTab(
    label: String,
    route: String,
    current: String,
    onNavigate: (String) -> Unit,
) {
    NavigationBarItem(
        selected = current == route,
        onClick = { if (current != route) onNavigate(route) },
        icon = { Text(if (current == route) "●" else "○", color = ThemeColors.Primary) },
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(
            selectedTextColor = ThemeColors.Ink,
            unselectedTextColor = ThemeColors.InkMute,
            indicatorColor = ThemeColors.CanvasSoft,
        ),
    )
}

@Composable
fun VerifierBottomTabs(current: String, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = ThemeColors.Canvas) {
        VerifierTab("KYC Queue", Routes.kycQueue, current, onNavigate)
        VerifierTab("Jobs Queue", Routes.jobsQueue, current, onNavigate)
        VerifierTab("Profile", Routes.profile, current, onNavigate)
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.VerifierTab(
    label: String,
    route: String,
    current: String,
    onNavigate: (String) -> Unit,
) {
    NavigationBarItem(
        selected = current == route,
        onClick = { if (current != route) onNavigate(route) },
        icon = { Text(if (current == route) "●" else "○", color = ThemeColors.Primary) },
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(
            selectedTextColor = ThemeColors.Ink,
            unselectedTextColor = ThemeColors.InkMute,
            indicatorColor = ThemeColors.CanvasSoft,
        ),
    )
}
