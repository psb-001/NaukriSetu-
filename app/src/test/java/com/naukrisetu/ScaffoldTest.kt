package com.naukrisetu

import androidx.compose.ui.graphics.Color
import com.naukrisetu.ui.copy.Strings
import com.naukrisetu.ui.nav.Routes
import com.naukrisetu.ui.theme.ThemeColors
import org.junit.Assert.assertEquals
import org.junit.Test

class ScaffoldTest {

    @Test fun `strings expose app name`() {
        assertEquals("NaukriSetu", Strings.appName)
    }

    @Test fun `routes expose home`() {
        assertEquals("home", Routes.home)
    }

    @Test fun `theme tokens match supabase ds`() {
        assertEquals(Color(0xFF10B981), ThemeColors.Primary)
        assertEquals(Color(0xFF0F172A), ThemeColors.Ink)
        assertEquals(Color(0xFFE2E8F0), ThemeColors.Hairline)
        assertEquals(Color(0xFFFFFFFF), ThemeColors.Canvas)
    }
}
