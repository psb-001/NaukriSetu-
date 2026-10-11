package com.naukrisetu

import com.naukrisetu.ui.auth.EmailCheck
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthLogicTest {

    @Test fun `invalid emails rejected`() {
        assertFalse(EmailCheck.valid("not-an-email"))
        assertFalse(EmailCheck.valid("ravi@"))
        assertFalse(EmailCheck.valid("@domain.com"))
        assertFalse(EmailCheck.valid(""))
        assertFalse(EmailCheck.valid("   "))
    }

    @Test fun `valid emails accepted`() {
        assertTrue(EmailCheck.valid("ravi.kadam@gmail.com"))
        assertTrue(EmailCheck.valid("employer@voltas.com"))
        assertTrue(EmailCheck.valid("verifier@yolo.org"))
        assertTrue(EmailCheck.valid("user@outlook.in"))
    }
}
