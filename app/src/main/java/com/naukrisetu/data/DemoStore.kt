package com.naukrisetu.data

import com.naukrisetu.engine.OppStatus
import com.naukrisetu.engine.Opportunity
import com.naukrisetu.engine.Seeker

/**
 * In-memory store — the demo baseline. Data stays on device; nothing leaves it.
 *
 * `open` so [RemoteStore] can subclass it and swap the backing store for
 * Supabase while keeping the exact same read/write surface the screens use.
 */
open class DemoStore {
    /** Local cache. DemoStore keys seekers by Seeker.id; RemoteStore by email. */
    protected val seekers = mutableMapOf<String, Seeker>()
    protected val opps = mutableMapOf<String, Opportunity>()

    /** Saved job IDs for the current seeker. */
    protected val _savedJobs = mutableSetOf<String>()
    /** Applied job IDs for the current seeker. */
    protected val _appliedJobs = mutableSetOf<String>()
    /** Saved course codes for the current seeker. */
    protected val _savedCourses = mutableSetOf<String>()

    /** KYC submission queue: seeker email → pending/approved/rejected */
    protected val kycQueue = mutableListOf<KycEntry>()

    init { seedLocal() }

    /** Seeds the local demo dataset. RemoteStore overrides this to load from Supabase. */
    open fun seedLocal() {
        seekers[Seed.demoSeeker.id] = Seed.demoSeeker
        seekers[Seed.experiencedSeeker.id] = Seed.experiencedSeeker
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
        // Seed a KYC queue with demo entries
        kycQueue.addAll(listOf(
            KycEntry("ravi.kadam@gmail.com", "Ravi Kadam", "Aadhaar card", "2 min ago", KycStatus.Pending),
            KycEntry("meena.iyer@gmail.com", "Meena Iyer", "Aadhaar card", "18 min ago", KycStatus.Pending),
            KycEntry("suresh.pawar@gmail.com", "Suresh Pawar", "Certificate: ITI Electrician", "1 hr ago", KycStatus.Pending),
        ))
    }

    /** The signed-in user's own profile. Demo uses the fixed demo seeker. */
    open fun currentSeeker(): Seeker? = seekers[Seed.demoSeeker.id] ?: seekers.values.firstOrNull() ?: Seed.demoSeeker

    open fun upsertSeeker(seeker: Seeker) {
        seekers[seeker.id] = seeker
        seekers[seeker.email] = seeker
    }

    open fun seeker(id: String): Seeker? = seekers[id]

    open fun allOpportunities(): List<Opportunity> = opps.values.toList()

    open fun opportunity(id: String): Opportunity? = opps[id] ?: Seed.opportunities.firstOrNull { it.id == id }

    open fun postingsByEmployer(employerId: String): List<Opportunity> =
        opps.values.filter { it.employerId == employerId }

    open fun submitPost(opp: Opportunity): Opportunity {
        val pending = opp.copy(status = OppStatus.Pending)
        opps[pending.id] = pending
        return pending
    }

    open fun approvePost(id: String) {
        opps[id] = requireNotNull(opps[id]) { "unknown posting $id" }.copy(status = OppStatus.Live)
    }

    open fun rejectPost(id: String) {
        opps[id] = requireNotNull(opps[id]) { "unknown posting $id" }.copy(status = OppStatus.Rejected)
    }

    open fun deletePost(id: String) {
        opps.remove(id)
    }

    // ---------- saved jobs ----------

    open fun savedJobIds(): Set<String> = _savedJobs.toSet()

    open fun toggleSaveJob(id: String) {
        if (id in _savedJobs) _savedJobs.remove(id) else _savedJobs.add(id)
    }

    // ---------- applied jobs ----------

    open fun appliedJobIds(): Set<String> = _appliedJobs.toSet()

    open fun isJobApplied(id: String): Boolean = id in _appliedJobs

    open fun applyJob(id: String) {
        _appliedJobs.add(id)
    }

    // ---------- saved courses ----------

    open fun savedCourseCodes(): Set<String> = _savedCourses.toSet()

    open fun toggleSaveCourse(code: String) {
        if (code in _savedCourses) _savedCourses.remove(code) else _savedCourses.add(code)
    }

    // ---------- KYC queue ----------

    open fun kycSubmissions(): List<KycEntry> = kycQueue.toList()

    open fun approveKyc(seekerEmail: String) {
        val idx = kycQueue.indexOfFirst { it.seekerEmail == seekerEmail && it.status == KycStatus.Pending }
        if (idx >= 0) kycQueue[idx] = kycQueue[idx].copy(status = KycStatus.Approved)
        // If the seeker is in the local store, mark their kyc_status as verified
        val s = seekers.values.firstOrNull { it.email == seekerEmail }
        if (s != null) seekers[s.id] = s.copy(addressProof = true)
    }

    open fun rejectKyc(seekerEmail: String) {
        val idx = kycQueue.indexOfFirst { it.seekerEmail == seekerEmail && it.status == KycStatus.Pending }
        if (idx >= 0) kycQueue[idx] = kycQueue[idx].copy(status = KycStatus.Rejected)
    }

    /** Clear all session state — called on logout so the next login starts clean. */
    open fun clear() {
        seekers.clear()
        opps.clear()
        _savedJobs.clear()
        _appliedJobs.clear()
        _savedCourses.clear()
        kycQueue.clear()
    }
}

data class KycEntry(
    val seekerEmail: String,
    val seekerName: String,
    val document: String,
    val ago: String,
    val status: KycStatus,
)

enum class KycStatus { Pending, Approved, Rejected }
