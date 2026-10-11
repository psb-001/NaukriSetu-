package com.naukrisetu.data

/**
 * Supabase connection config. The project belongs to the YOLO organization
 * (org tppbdlhqnxfuckytiybj). Values come from the NaukriSetu project's
 * publishable keys via the Management API — never hand-checked-in secrets.
 */
object SupabaseConfig {
    const val PROJECT_REF = "miezgejnubmbcumqflab"
    const val URL = "https://$PROJECT_REF.supabase.co"

    /** Anon (publishable) key — safe to ship in the client. */
    const val ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1pZXpnZWpudWJtYmN1bXFmbGFiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTE2OTI5MDAsImV4cCI6MjEwNzI2ODkwMH0.zSB_uXwbcs0GtwRJkiaBa3Bmx--6cFKNkWkSgAUHhFU"

    const val REST = "$URL/rest/v1"
    const val AUTH = "$URL/auth/v1"
    const val TIMEOUT_MS = 15_000L
}
