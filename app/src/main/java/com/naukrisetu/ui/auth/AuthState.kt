package com.naukrisetu.ui.auth

/** Accepts any valid-looking email (e.g. user@domain.com). */
object EmailCheck {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun valid(email: String): Boolean = EMAIL_REGEX.matches(email.trim())
}

enum class Role { Seeker, Employer, Verifier }
