package com.naukrisetu

import com.naukrisetu.ui.auth.EmailCheck
import com.naukrisetu.ui.auth.OtpState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthLogicTest {

    @Test fun `non-gmail rejected`() {
        assertFalse(EmailCheck.valid("ravi@yahoo.com"))
        assertFalse(EmailCheck.valid("ravi.kadam@gmail.co"))
        assertFalse(EmailCheck.valid(""))
    }

    @Test fun `gmail accepted`() {
        assertTrue(EmailCheck.valid("ravi.kadam@gmail.com"))
    }

    @Test fun `only 1234 verifies`() {
        assertTrue(OtpState().verify("1234"))
        assertFalse(OtpState().verify("0000"))
        assertFalse(OtpState().verify("12345"))
    }
}
