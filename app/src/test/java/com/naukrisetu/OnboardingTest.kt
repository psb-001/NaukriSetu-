package com.naukrisetu

import com.naukrisetu.ui.onboarding.OnboardingValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingTest {

    @Test fun `phone rule`() {
        assertTrue(OnboardingValidator.validPhone("9876543210"))
        assertFalse(OnboardingValidator.validPhone("12345"))
        assertFalse(OnboardingValidator.validPhone("5876543210"))
        assertFalse(OnboardingValidator.validPhone("987654321"))
    }

    @Test fun `name rule`() {
        assertTrue(OnboardingValidator.validName("Ravi"))
        assertFalse(OnboardingValidator.validName("R"))
        assertFalse(OnboardingValidator.validName("  "))
    }
}
