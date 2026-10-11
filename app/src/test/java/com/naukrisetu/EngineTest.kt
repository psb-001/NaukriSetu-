package com.naukrisetu

import com.naukrisetu.data.DemoStore
import com.naukrisetu.data.Seed
import com.naukrisetu.engine.Criterion
import com.naukrisetu.engine.OppStatus
import com.naukrisetu.engine.Opportunity
import com.naukrisetu.engine.checkEligibility
import com.naukrisetu.engine.gapsFor
import com.naukrisetu.engine.rankEligible
import com.naukrisetu.engine.seekerVisibleOpps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class EngineTest {

    private val puneSeeker = Seed.demoSeeker.copy(city = "Pune")

    private val mumbaiJob = Opportunity(
        id = "t-mumbai",
        title = "House Electrician",
        employer = "Sharma Enterprises",
        city = "Mumbai",
        payMin = 28000,
        payMax = 28000,
        distanceKm = 150.0,
        requiredSkills = listOf("House wiring"),
        criteria = listOf(Criterion.City("Mumbai")),
        status = OppStatus.Live,
    )

    @Test fun `city gate rejects non-local job`() {
        val reasons = checkEligibility(puneSeeker, mumbaiJob)
        assertTrue("expected a Mumbai reason, got: $reasons", reasons.any { it.contains("Mumbai") })
    }

    @Test fun `ranker throws on unfiltered input`() {
        try {
            rankEligible(puneSeeker, listOf(mumbaiJob))
            fail("expected IllegalArgumentException for ineligible input")
        } catch (e: IllegalArgumentException) {
            // expected — ranker only accepts gate survivors
        }
    }

    @Test fun `gaps name missing certificate`() {
        val seekerNoCert = puneSeeker.copy(certificates = emptyList())
        val acJob = Seed.opportunities.first { it.id == "opp-cooltech-ac" }
        val gaps = gapsFor(seekerNoCert, acJob)
        assertTrue("expected AC Repair gap, got: $gaps", gaps.any { it.label.contains("AC Repair") })
    }

    @Test fun `pending posts hidden from seekers`() {
        val store = DemoStore()
        assertFalse(
            seekerVisibleOpps(store).any { it.status != OppStatus.Live },
        )
        assertTrue(store.allOpportunities().any { it.status == OppStatus.Pending })
    }

    @Test fun `seed has twelve opportunities`() {
        assertEquals(12, Seed.opportunities.size)
    }
}
