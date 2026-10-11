package com.naukrisetu

import com.naukrisetu.data.Seed
import com.naukrisetu.ui.kyc.KycLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KycTest {

    @Test fun `aadhaar needs 12 digits`() {
        assertTrue(KycLogic.aadhaarValid("123456789012"))
        assertFalse(KycLogic.aadhaarValid("1234"))
        assertFalse(KycLogic.aadhaarValid("12345678901a"))
    }

    @Test fun `progress counts checks`() {
        // 5 checks: phone, city, aadhaar, address proof, certificate.
        // Fresh onboarding (phone + city only) → 2 of 5 → 40%.
        val seeker = Seed.demoSeeker.copy(
            certificates = emptyList(),
            aadhaar = null,
            addressProof = false,
        )
        assertEquals(40, KycLogic.progressPct(seeker))
    }

    @Test fun `full profile is complete`() {
        val seeker = Seed.demoSeeker.copy(
            aadhaar = "123456789012",
            addressProof = true,
        )
        assertEquals(100, KycLogic.progressPct(seeker))
    }
}
