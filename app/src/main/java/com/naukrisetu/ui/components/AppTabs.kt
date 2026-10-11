package com.naukrisetu.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.ui.icons.Lucide
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors

/** Underlined text link — no button box, per Kombai (See all, How to qualify). */
@Composable
fun UnderlineLink(text: String, onClick: () -> Unit, fontSize: Int = 14) {
    Text(
        text = text,
        fontSize = fontSize.sp,
        color = ThemeColors.Ink,
        style = TextStyle(textDecoration = TextDecoration.Underline),
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        ),
    )
}

private data class Tab(val label: String, val route: String, val icon: ImageVector)

private val seekerTabs = listOf(
    Tab("Home", Routes.home, Lucide.Home),
    Tab("Matches", Routes.matches, Lucide.Briefcase),
    Tab("Courses", Routes.courses, Lucide.BookOpen),
    Tab("Profile", Routes.profile, Lucide.User),
)

private val employerTabs = listOf(
    Tab("Post Job", Routes.postJob, Lucide.Plus),
    Tab("My Postings", Routes.myPostings, Lucide.Briefcase),
    Tab("Profile", Routes.profile, Lucide.User),
)

private val verifierTabs = listOf(
    Tab("KYC Queue", Routes.kycQueue, Lucide.ShieldCheck),
    Tab("Jobs Queue", Routes.jobsQueue, Lucide.FileText),
    Tab("Profile", Routes.profile, Lucide.User),
)

@Composable
private fun IconTabs(tabs: List<Tab>, current: String, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = ThemeColors.Canvas) {
        tabs.forEach { t ->
            val selected = current == t.route
            NavigationBarItem(
                selected = selected,
                onClick = { if (!selected) onNavigate(t.route) },
                icon = {
                    Icon(
                        imageVector = t.icon,
                        contentDescription = t.label,
                        tint = if (selected) ThemeColors.Primary else ThemeColors.InkMute,
                        modifier = Modifier.size(22.dp),
                    )
                },
                label = { Text(t.label, fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = ThemeColors.Ink,
                    unselectedTextColor = ThemeColors.InkMute,
                    indicatorColor = ThemeColors.CanvasSoft,
                ),
            )
        }
    }
}

@Composable
fun SeekerTabs(current: String, onNavigate: (String) -> Unit) = IconTabs(seekerTabs, current, onNavigate)

@Composable
fun EmployerTabs(current: String, onNavigate: (String) -> Unit) = IconTabs(employerTabs, current, onNavigate)

@Composable
fun VerifierTabs(current: String, onNavigate: (String) -> Unit) = IconTabs(verifierTabs, current, onNavigate)

/** Icon + text row used in meta lines (pay / distance / type). */
@Composable
fun MetaItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = ThemeColors.InkMute, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 13.sp, color = ThemeColors.InkMute)
    }
}

/** Small amber info box (pending verification etc.). */
@Composable
fun AmberBox(text: String) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text("●", fontSize = 10.sp, color = ThemeColors.Warning)
            Spacer(Modifier.width(8.dp))
            Text(text, fontSize = 13.sp, color = ThemeColors.InkSecondary)
        }
    }
}

/** Grey info box with icon (demo hints). */
@Composable
fun InfoBox(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = ThemeColors.InkMute, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(text, fontSize = 13.sp, color = ThemeColors.InkMute)
        }
    }
}
