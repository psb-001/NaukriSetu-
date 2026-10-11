package com.naukrisetu

import com.naukrisetu.ui.components.sweepFor
import org.junit.Assert.assertEquals
import org.junit.Test

class IconMathTest {

    @Test fun `sweep maps score to degrees`() {
        assertEquals(331.2f, sweepFor(92), 0.01f)
        assertEquals(0f, sweepFor(0), 0.01f)
        assertEquals(360f, sweepFor(100), 0.01f)
    }
}
