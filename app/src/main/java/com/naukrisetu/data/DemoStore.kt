package com.naukrisetu.data

import com.naukrisetu.engine.OppStatus
import com.naukrisetu.engine.Opportunity
import com.naukrisetu.engine.Seeker

/** In-memory demo store. Data stays on device; nothing leaves it. */
class DemoStore {
    private val seekers = mutableMapOf<String, Seeker>()
    private val opps = mutableMapOf<String, Opportunity>()

    init {
        seekers[Seed.demoSeeker.id] = Seed.demoSeeker
        for (opp in Seed.opportunities) opps[opp.id] = opp
        // One pending + one rejected post so visibility rules have data.
        opps["opp-pending-fan"] = Opportunity(
            id = "opp-pending-fan",
            title = "Fan Repair Technician",
            employer = "RBI Colony Society",
            city = "Pune",
            payMin = 18000,
            payMax = 21000,
            distanceKm = 1.4,
            requiredSkills = listOf("Fan repair"),
            criteria = listOf(com.naukrisetu.engine.Criterion.City("Pune")),
            status = OppStatus.Pending,
            employerId = "emp-rbi",
        )
        opps["opp-rejected-plumb"] = Opportunity(
            id = "opp-rejected-plumb",
            title = "Plumbing Helper",
            employer = "Rao Constructions",
            city = "Pune",
            payMin = 13000,
            payMax = 16000,
            distanceKm = 5.0,
            requiredSkills = listOf("Basic plumbing"),
            criteria = listOf(com.naukrisetu.engine.Criterion.City("Pune")),
            status = OppStatus.Rejected,
            employerId = "emp-rao",
        )
    }

    fun upsertSeeker(seeker: Seeker) {
        seekers[seeker.id] = seeker
    }

    fun seeker(id: String): Seeker? = seekers[id]

    fun allOpportunities(): List<Opportunity> = opps.values.toList()

    fun postingsByEmployer(employerId: String): List<Opportunity> =
        opps.values.filter { it.employerId == employerId }

    fun submitPost(opp: Opportunity): Opportunity {
        val pending = opp.copy(status = OppStatus.Pending)
        opps[pending.id] = pending
        return pending
    }

    fun approvePost(id: String) {
        opps[id] = requireNotNull(opps[id]) { "unknown posting $id" }.copy(status = OppStatus.Live)
    }

    fun rejectPost(id: String) {
        opps[id] = requireNotNull(opps[id]) { "unknown posting $id" }.copy(status = OppStatus.Rejected)
    }

    fun deletePost(id: String) {
        opps.remove(id)
    }
}
