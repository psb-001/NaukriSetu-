package com.naukrisetu.ui.auth

/** Demo auth: any @gmail.com address, OTP is always 1234. No backend. */
object EmailCheck {
    fun valid(email: String): Boolean =
        email.trim().lowercase().endsWith("@gmail.com") &&
            email.trim().length > "@gmail.com".length
}

class OtpState {
    fun verify(code: String): Boolean = code == "1234"
}

enum class Role { Seeker, Employer, Verifier }
