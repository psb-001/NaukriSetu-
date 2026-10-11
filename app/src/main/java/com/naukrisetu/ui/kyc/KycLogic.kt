package com.naukrisetu.ui.kyc

import com.naukrisetu.engine.Seeker
import com.naukrisetu.ui.onboarding.OnboardingValidator

/**
 * 5 KYC checks: valid phone, city set, Aadhaar (12-digit format check only —
 * the demo never contacts UIDAI), address proof stored, ≥1 certificate.
 */
object KycLogic {
    fun aadhaarValid(n: String): Boolean {
        val digits = n.filter { it.isDigit() }
        return digits.length == 12 && digits == n.trim()
    }

    fun checksDone(seeker: Seeker): Int {
        var done = 0
        if (OnboardingValidator.validPhone(seeker.phone)) done++
        if (seeker.city.isNotBlank()) done++
        if (seeker.aadhaar != null && aadhaarValid(seeker.aadhaar)) done++
        if (seeker.addressProof) done++
        if (seeker.certificates.isNotEmpty()) done++
        return done
    }

    fun progressPct(seeker: Seeker): Int = checksDone(seeker) * 100 / 5
}
