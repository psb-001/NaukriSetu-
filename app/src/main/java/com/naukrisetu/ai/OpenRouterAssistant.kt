package com.naukrisetu.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class ExtractedProfile(
    val extractedSkills: List<String>,
    val suggestedRole: String? = null,
    val estimatedExpYears: Int? = null,
    val summary: String,
)

/**
 * OpenRouter AI Assistant for parsing natural conversational text or voice transcripts into
 * canonical job skills and profile details.
 *
 * Uses free-tier LLM models on OpenRouter (e.g. meta-llama/llama-3.3-70b-instruct:free
 * or google/gemini-2.0-flash-lite:free).
 */
object OpenRouterAssistant {

    private const val OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions"

    suspend fun analyzeCandidateSpeech(
        userInput: String,
        apiKey: String,
        model: String = "google/gemini-2.0-flash-lite:free",
    ): Result<ExtractedProfile> = withContext(Dispatchers.IO) {
        if (userInput.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Input text cannot be blank"))
        }

        val prompt = """
You are RozgarLink's Skill Assistant for Indian blue-collar workers, technicians, and ITI students.
Extract the candidate's skills, trade role, and experience from what they say (English or Hindi/Hinglish).

Standard available canonical skills:
[House wiring, Fan repair, Fan Motor Repair, Basic plumbing, AC Repair & Servicing, AC gas charging, Carpentry, Painting, Welding, Masonry, Pump repair, Refrigeration, Split-AC install, Switchboard fitting, Pipe fitting]

User input:
"$userInput"

Return ONLY a valid JSON object with this exact structure (no markdown fences, no explanation):
{
  "skills": ["Canonical Skill 1", "Canonical Skill 2"],
  "suggested_role": "Electrician / Plumber / Student / Technician",
  "estimated_exp_years": 0,
  "summary": "Brief 1-line friendly summary of what was understood"
}
""".trimIndent()

        try {
            val url = URL(OPENROUTER_URL)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.setRequestProperty("HTTP-Referer", "https://github.com/psb-001/NaukriSetu-")
            conn.setRequestProperty("X-Title", "NaukriSetu RozgarLink")
            conn.connectTimeout = 12000
            conn.readTimeout = 15000
            conn.doOutput = true

            val requestBody = JSONObject().apply {
                put("model", model)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "You are a JSON-only skill extractor for Indian vocational job seekers.")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
                put("temperature", 0.1)
            }

            OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(requestBody.toString())
                writer.flush()
            }

            val statusCode = conn.responseCode
            val responseStream = if (statusCode in 200..299) conn.inputStream else conn.errorStream
            val raw = BufferedReader(InputStreamReader(responseStream)).use { it.readText() }

            if (statusCode !in 200..299) {
                return@withContext Result.failure(Exception("OpenRouter error ($statusCode): $raw"))
            }

            val json = JSONObject(raw)
            val choices = json.optJSONArray("choices")
            val message = choices?.optJSONObject(0)?.optJSONObject("message")
            val content = message?.optString("content") ?: ""

            // Strip possible ```json fences
            val cleaned = content.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val parsed = JSONObject(cleaned)
            val skillsArray = parsed.optJSONArray("skills")
            val skillsList = mutableListOf<String>()
            if (skillsArray != null) {
                for (i in 0 until skillsArray.length()) {
                    val s = skillsArray.optString(i)
                    if (s.isNotBlank()) skillsList.add(s)
                }
            }

            val profile = ExtractedProfile(
                extractedSkills = skillsList,
                suggestedRole = parsed.optString("suggested_role").takeIf { it.isNotBlank() },
                estimatedExpYears = if (parsed.has("estimated_exp_years")) parsed.optInt("estimated_exp_years") else null,
                summary = parsed.optString("summary", "Skills identified successfully."),
            )

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
