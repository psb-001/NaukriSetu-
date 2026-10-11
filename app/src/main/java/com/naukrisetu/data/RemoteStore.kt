package com.naukrisetu.data

import androidx.compose.runtime.mutableStateOf
import com.naukrisetu.engine.OppStatus
import com.naukrisetu.engine.Opportunity
import com.naukrisetu.engine.Seeker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

/**
 * Supabase-backed store. Same read/write surface as [DemoStore], but the source
 * of truth is the remote database and the local map is just a cache.
 *
 * Writes are optimistic: the cache updates instantly (so the UI never waits on
 * the network) and the change is pushed to Supabase in the background. If the
 * push fails the cache still holds the change and [lastError] surfaces why —
 * the app stays usable offline and re-syncs on next launch via [load].
 */
class RemoteStore(
    /** Email the user logged in with — the primary key of `profiles`. */
    private val email: String,
) : DemoStore() {

    /** Scoped to IO so every Supabase call is off the main thread. */
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /** True while the initial fetch from Supabase is in flight. */
    val isLoading = mutableStateOf(true)

    /** Last sync failure, if any — for a banner or debug surface. */
    val lastError = mutableStateOf<String?>(null)

    /** Pre-seed cache from local catalogue so UI renders instantly while remote syncs. */
    override fun seedLocal() {
        Seed.opportunities.forEach { opps[it.id] = it }
        seekers[Seed.demoSeeker.email] = Seed.demoSeeker
        seekers[Seed.studentSeeker.email] = Seed.studentSeeker
    }

    // ---------- remote I/O ----------

    /** Pull opportunities + profiles + KYC queue into the cache. Safe to call repeatedly. */
    suspend fun load() = kotlinx.coroutines.withContext(Dispatchers.IO) {
        isLoading.value = true
        try {
            val oppArr = JSONArray(SupabaseApi.get("opportunities?select=*&order=id"))
            val fresh = (0 until oppArr.length()).map { opportunityFromJson(oppArr.getJSONObject(it)) }
            if (fresh.isNotEmpty()) {
                opps.clear()
                fresh.forEach { opps[it.id] = it }
            }

            val profArr = JSONArray(SupabaseApi.get("profiles?select=*&order=email"))
            if (profArr.length() > 0) {
                seekers.clear()
                for (i in 0 until profArr.length()) {
                    val s = profileFromJson(profArr.getJSONObject(i))
                    seekers[s.email] = s
                }
            }

            // Load KYC submissions queue
            try {
                val kycArr = JSONArray(SupabaseApi.get("kyc_submissions?select=*&order=created_at"))
                kycQueue.clear()
                for (i in 0 until kycArr.length()) {
                    val o = kycArr.getJSONObject(i)
                    kycQueue.add(KycEntry(
                        seekerEmail = o.optString("seeker_email"),
                        seekerName = seekers[o.optString("seeker_email")]?.name ?: o.optString("seeker_email"),
                        document = buildDocLabel(o),
                        ago = formatAgo(o.optString("created_at")),
                        status = when (o.optString("status")) {
                            "approved" -> KycStatus.Approved
                            "rejected" -> KycStatus.Rejected
                            else -> KycStatus.Pending
                        },
                    ))
                }
                // If no remote KYC entries, seed with demo queue based on profiles
                if (kycQueue.isEmpty()) seedKycQueueFromProfiles()
            } catch (e: Exception) {
                // kyc_submissions might be empty — seed from profiles
                seedKycQueueFromProfiles()
            }

            // If profile for current user doesn't exist yet, insert with sensible defaults
            if (seekers[email] == null) {
                val fallback = if (email.contains("student")) Seed.studentSeeker else Seed.demoSeeker
                val cleanName = email.substringBefore("@")
                    .replace(".", " ")
                    .replace("_", " ")
                    .split(" ")
                    .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
                val newProfile = fallback.copy(
                    id = "profile-$email",
                    name = cleanName,
                    email = email,
                )
                try {
                    SupabaseApi.post("profiles", newProfile.toJson())
                    seekers[email] = newProfile
                } catch (_: Exception) {
                    seekers[email] = newProfile
                }
            }

            lastError.value = null
        } catch (e: Exception) {
            lastError.value = e.message ?: "Could not reach Supabase"
            // Ensure local seed items are present if network failed
            if (opps.isEmpty()) {
                Seed.opportunities.forEach { opps[it.id] = it }
            }
        } finally {
            isLoading.value = false
        }
    }

    private fun buildDocLabel(o: JSONObject): String {
        val certs = o.optJSONArray("certificates")
        if (certs != null && certs.length() > 0) return "Certificate: ${certs.getString(0)}"
        if (o.optString("aadhaar").isNotBlank()) return "Aadhaar card"
        if (o.optBoolean("address_proof")) return "Address proof"
        return "Documents"
    }

    private fun seedKycQueueFromProfiles() {
        kycQueue.clear()
        seekers.values.filter { it.role == "seeker" || it.role == null }.forEach { s ->
            if (s.aadhaar != null) {
                kycQueue.add(KycEntry(s.email, s.name, "Aadhaar card", "pending", KycStatus.Pending))
            }
        }
    }

    private fun formatAgo(isoTs: String): String {
        return try {
            val parsed = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US)
                .parse(isoTs.take(19)) ?: return isoTs
            val diffMs = System.currentTimeMillis() - parsed.time
            val mins = diffMs / 60_000
            when {
                mins < 2 -> "just now"
                mins < 60 -> "$mins min ago"
                mins < 1440 -> "${mins / 60} hr ago"
                else -> "${mins / 1440} days ago"
            }
        } catch (_: Exception) { isoTs }
    }

    private suspend fun pushProfile(seeker: Seeker) {
        try {
            SupabaseApi.patch("profiles?email=eq.${seeker.email}", seeker.toJson())
        } catch (e: Exception) {
            lastError.value = "Profile save failed: ${e.message}"
        }
    }

    private suspend fun pushStatus(id: String, status: OppStatus) {
        try {
            SupabaseApi.patch("opportunities?id=eq.$id", JSONObject().put("status", status.name))
        } catch (e: Exception) {
            lastError.value = "Sync failed: ${e.message}"
        }
    }

    private suspend fun pushNew(opp: Opportunity) {
        try {
            SupabaseApi.post("opportunities", opp.toJson())
        } catch (e: Exception) {
            lastError.value = "Post submit failed: ${e.message}"
        }
    }

    private suspend fun pushDelete(id: String) {
        try {
            SupabaseApi.delete("opportunities?id=eq.$id")
        } catch (e: Exception) {
            lastError.value = "Delete failed: ${e.message}"
        }
    }

    private suspend fun pushKycDecision(seekerEmail: String, status: String) {
        try {
            SupabaseApi.patch(
                "kyc_submissions?seeker_email=eq.$seekerEmail",
                JSONObject().put("status", status)
            )
            // Also update the profile's kyc_status
            SupabaseApi.patch(
                "profiles?email=eq.$seekerEmail",
                JSONObject().put("kyc_status", status)
            )
        } catch (e: Exception) {
            lastError.value = "KYC update failed: ${e.message}"
        }
    }

    // ---------- reads (cache) ----------

    /** The signed-in user's profile, with smart fallback to demo profiles. */
    override fun currentSeeker(): Seeker? {
        seekers[email]?.let { return it }
        seekers.values.firstOrNull { it.email.equals(email, ignoreCase = true) }?.let { return it }
        return if (email.contains("student")) Seed.studentSeeker else Seed.demoSeeker
    }

    override fun seeker(id: String): Seeker? {
        seekers[id]?.let { return it }
        return if (id == DEMO_SEEKER_ID) seekers[DEMO_SEEKER_EMAIL] else null
    }

    override fun allOpportunities(): List<Opportunity> = opps.values.toList()

    override fun postingsByEmployer(employerId: String): List<Opportunity> =
        opps.values.filter {
            it.employerId.equals(employerId, ignoreCase = true) ||
            it.employerId.equals("emp-$employerId", ignoreCase = true) ||
            (employerId.contains("voltas", ignoreCase = true) && it.employerId.contains("voltas", ignoreCase = true))
        }

    // ---------- writes (optimistic cache + background sync) ----------

    override fun upsertSeeker(seeker: Seeker) {
        seekers[seeker.email] = seeker
        scope.launch { pushProfile(seeker) }
    }

    override fun submitPost(opp: Opportunity): Opportunity {
        val pending = opp.copy(status = OppStatus.Pending)
        opps[pending.id] = pending
        scope.launch { pushNew(pending) }
        return pending
    }

    override fun approvePost(id: String) {
        val existing = opps[id] ?: return
        opps[id] = existing.copy(status = OppStatus.Live)
        scope.launch { pushStatus(id, OppStatus.Live) }
    }

    override fun rejectPost(id: String) {
        val existing = opps[id] ?: return
        opps[id] = existing.copy(status = OppStatus.Rejected)
        scope.launch { pushStatus(id, OppStatus.Rejected) }
    }

    override fun deletePost(id: String) {
        opps.remove(id)
        scope.launch { pushDelete(id) }
    }

    override fun approveKyc(seekerEmail: String) {
        super.approveKyc(seekerEmail)
        scope.launch { pushKycDecision(seekerEmail, "approved") }
    }

    override fun rejectKyc(seekerEmail: String) {
        super.rejectKyc(seekerEmail)
        scope.launch { pushKycDecision(seekerEmail, "rejected") }
    }

    override fun clear() {
        super.clear()
        lastError.value = null
        isLoading.value = false
    }

    companion object {
        const val DEMO_SEEKER_ID = "seeker-ravi"
        const val DEMO_SEEKER_EMAIL = "ravi.kadam@gmail.com"
    }
}
