package com.naukrisetu.ui.onboarding

object OnboardingValidator {
    /** 10 digits, starts with 6–9. */
    fun validPhone(p: String): Boolean {
        val digits = p.filter { it.isDigit() }
        return digits.length == 10 && digits[0] in '6'..'9'
    }

    fun validName(n: String): Boolean = n.trim().length >= 2
}
