package com.naukrisetu

import com.naukrisetu.data.DemoStore
import com.naukrisetu.engine.Criterion
import com.naukrisetu.engine.OppStatus
import com.naukrisetu.engine.Opportunity
import com.naukrisetu.engine.seekerVisibleOpps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PostingFlowTest {

    private val draft = Opportunity(
        id = "opp-draft-test",
        title = "Test Electrician",
        employer = "Test Employer",
        city = "Pune",
        payMin = 20000,
        payMax = 25000,
        distanceKm = 2.0,
        requiredSkills = listOf("House wiring"),
        criteria = listOf(Criterion.City("Pune")),
        status = OppStatus.Live,
        employerId = "emp-test",
    )

    @Test fun `new post pending and invisible`() {
        val store = DemoStore()
        val p = store.submitPost(draft)
        assertEquals(OppStatus.Pending, p.status)
        assertFalse(seekerVisibleOpps(store).any { it.id == p.id })
    }

    @Test fun `approve makes post live`() {
        val store = DemoStore()
        val p = store.submitPost(draft)
        store.approvePost(p.id)
        assertTrue(seekerVisibleOpps(store).any { it.id == p.id })
    }

    @Test fun `reject hides post`() {
        val store = DemoStore()
        val p = store.submitPost(draft)
        store.rejectPost(p.id)
        assertFalse(seekerVisibleOpps(store).any { it.id == p.id })
    }
}
