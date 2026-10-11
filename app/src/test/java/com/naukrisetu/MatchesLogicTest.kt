package com.naukrisetu

import com.naukrisetu.data.Seed
import com.naukrisetu.engine.homeHeroCount
import com.naukrisetu.engine.splitMatches
import org.junit.Assert.assertEquals
import org.junit.Test

class MatchesLogicTest {

    private val seeker = Seed.demoSeeker
    private val liveOpps = Seed.opportunities

    @Test fun `matches split matches engine`() {
        val (eligible, ineligible) = splitMatches(seeker, liveOpps)
        assertEquals(5, eligible.size)
        assertEquals(3, ineligible.size)
    }

    @Test fun `home hero count is live`() {
        assertEquals(5, homeHeroCount(seeker, liveOpps))
    }
}
