package com.naukrisetu.data

import com.naukrisetu.engine.Criterion
import com.naukrisetu.engine.OppStatus
import com.naukrisetu.engine.Opportunity
import com.naukrisetu.engine.Seeker
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Minimal REST client for Supabase PostgREST. No third-party dependencies —
 * keeps the app buildable offline and the network layer fully transparent.
 *
 * Every call sends the anon key. RLS on the four public tables currently allows
 * anon access (demo login is email + OTP 1234, verified on-device), so the
 * client needs no auth session yet.
 */
object SupabaseApi {

    /** Thrown when Supabase answers with a non-2xx status. */
    class ApiException(val status: Int, message: String) : Exception("HTTP $status: $message")

    fun get(path: String): String = request("GET", path, null)
    fun post(path: String, body: JSONObject): String = request("POST", path, body)
    fun patch(path: String, body: JSONObject): String = request("PATCH", path, body)
    fun delete(path: String): String = request("DELETE", path, null)

    private fun request(method: String, path: String, body: JSONObject?): String {
        val conn = URL("${SupabaseConfig.REST}/$path").openConnection() as HttpURLConnection
        conn.requestMethod = method
        conn.connectTimeout = SupabaseConfig.TIMEOUT_MS.toInt()
        conn.readTimeout = SupabaseConfig.TIMEOUT_MS.toInt()
        conn.setRequestProperty("apikey", SupabaseConfig.ANON_KEY)
        conn.setRequestProperty("Authorization", "Bearer ${SupabaseConfig.ANON_KEY}")
        conn.setRequestProperty("Content-Type", "application/json")
        // PostgREST returns the created/updated rows when asked.
        conn.setRequestProperty("Prefer", "return=representation")
        if (body != null) {
            conn.doOutput = true
            conn.outputStream.use { it.write(body.toString().toByteArray()) }
        }
        val status = conn.responseCode
        val stream = if (status in 200..299) conn.inputStream else conn.errorStream
        val text = stream?.let { BufferedReader(InputStreamReader(it)).readText() } ?: ""
        if (status !in 200..299) throw ApiException(status, text)
        return text
    }
}

// ---------- row <-> domain mapping (top-level so any caller can use them) ----------

fun Seeker.toJson(): JSONObject = JSONObject().apply {
    put("email", email)
    put("name", name)
    put("phone", phone)
    put("city", city)
    put("age", age ?: JSONObject.NULL)
    put("experience_years", experienceYears ?: JSONObject.NULL)
    put("skills", JSONArray(skills))
    put("certificates", JSONArray(certificates))
    put("role", role ?: "seeker")
    put("goal", goal ?: JSONObject.NULL)
    put("pay_minimum", payMinimum ?: JSONObject.NULL)
    put("aadhaar", aadhaar ?: JSONObject.NULL)
    put("address_proof", addressProof)
}

fun profileFromJson(o: JSONObject): Seeker = Seeker(
    id = "profile-${o.optString("email")}",
    name = o.optString("name"),
    email = o.optString("email"),
    phone = o.optString("phone"),
    city = o.optString("city"),
    age = o.optIntOrNull("age"),
    experienceYears = o.optIntOrNull("experience_years"),
    skills = o.optJSONArrayOrNull("skills").toStringList(),
    certificates = o.optJSONArrayOrNull("certificates").toStringList(),
    role = o.optString("role").ifBlank { null },
    goal = o.optStringOrNull("goal"),
    payMinimum = o.optIntOrNull("pay_minimum"),
    aadhaar = o.optStringOrNull("aadhaar"),
    addressProof = o.optBoolean("address_proof", false),
)

fun Opportunity.toJson(): JSONObject = JSONObject().apply {
    put("id", id)
    put("title", title)
    put("employer", employer)
    put("city", city)
    put("pay_min", payMin)
    put("pay_max", payMax)
    put("distance_km", distanceKm)
    put("required_skills", JSONArray(requiredSkills))
    put("criteria", criteriaToJson(criteria))
    put("status", status.name)
    put("description", description)
    put("work_involved", workInvolved)
    put("employer_id", employerId)
}

fun opportunityFromJson(o: JSONObject): Opportunity {
    val status = runCatching { OppStatus.valueOf(o.optString("status")) }
        .getOrDefault(OppStatus.Live)
    return Opportunity(
        id = o.optString("id"),
        title = o.optString("title"),
        employer = o.optString("employer"),
        city = o.optString("city"),
        payMin = o.optInt("pay_min"),
        payMax = o.optInt("pay_max"),
        distanceKm = o.optDouble("distance_km", 0.0),
        requiredSkills = o.optJSONArrayOrNull("required_skills").toStringList(),
        criteria = parseCriteria(o.optJSONArray("criteria")),
        status = status,
        description = o.optString("description"),
        workInvolved = o.optString("work_involved"),
        employerId = o.optString("employer_id"),
    )
}

private fun criteriaToJson(criteria: List<Criterion>): JSONArray = JSONArray().apply {
    criteria.forEach { c ->
        when (c) {
            is Criterion.City -> put(JSONObject().put("type", "City").put("city", c.city))
            is Criterion.Certificate ->
                put(JSONObject().put("type", "Certificate").put("name", c.name))
            is Criterion.MinAge -> put(JSONObject().put("type", "MinAge").put("years", c.years))
            is Criterion.MinExperience ->
                put(JSONObject().put("type", "MinExperience").put("years", c.years))
        }
    }
}

private fun parseCriteria(arr: JSONArray?): List<Criterion> {
    if (arr == null) return emptyList()
    return (0 until arr.length()).mapNotNull { i ->
        val c = arr.optJSONObject(i) ?: return@mapNotNull null
        when (c.optString("type")) {
            "City" -> Criterion.City(c.optString("city"))
            "Certificate" -> Criterion.Certificate(c.optString("name"))
            "MinAge" -> Criterion.MinAge(c.optInt("years"))
            "MinExperience" -> Criterion.MinExperience(c.optInt("years"))
            else -> null
        }
    }
}

private fun JSONObject.optIntOrNull(key: String): Int? =
    if (isNull(key) || !has(key)) null else optInt(key)

private fun JSONObject.optStringOrNull(key: String): String? =
    if (isNull(key) || !has(key)) null else optString(key).ifBlank { null }

private fun JSONObject.optJSONArrayOrNull(key: String): JSONArray? =
    if (isNull(key) || !has(key)) null else optJSONArray(key)

private fun JSONArray?.toStringList(): List<String> {
    if (this == null) return emptyList()
    return (0 until length()).map { optString(it) }
}
