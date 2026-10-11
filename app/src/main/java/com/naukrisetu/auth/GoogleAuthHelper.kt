package com.naukrisetu.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.naukrisetu.data.SupabaseConfig

/**
 * Supabase-brokered Google OAuth via Chrome Custom Tab.
 *
 * Flow:
 *  1. App opens Supabase Google OAuth URL in a Custom Tab.
 *  2. User picks a Google account in the browser.
 *  3. Supabase redirects to `naukrisetu://auth?access_token=...&refresh_token=...`
 *  4. Android routes the deep link back to MainActivity (singleTask + intent-filter).
 *  5. MainActivity calls [parseCallback] and hands the tokens to [RemoteStore].
 *
 * No google-services.json needed — Supabase handles the Google OAuth handshake.
 */
object GoogleAuthHelper {

    /** Redirect URI registered in Supabase → Authentication → URL Configuration. */
    const val REDIRECT_URI = "naukrisetu://auth"

    /**
     * Opens the Supabase Google OAuth page in a Chrome Custom Tab.
     * After the user authenticates, the browser redirects to [REDIRECT_URI]
     * and Android relaunches MainActivity with the callback URL as its intent data.
     */
    fun launchGoogleSignIn(context: Context) {
        val url = "${SupabaseConfig.URL}/auth/v1/authorize" +
            "?provider=google" +
            "&redirect_to=${Uri.encode(REDIRECT_URI)}"
        val intent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        intent.launchUrl(context, Uri.parse(url))
    }

    /**
     * Parses the OAuth callback URI.
     * Returns [OAuthResult.Success] with the access token on success,
     * [OAuthResult.Error] with the error message otherwise.
     *
     * Supabase returns tokens as URL fragment (#) parameters.
     */
    fun parseCallback(uri: Uri): OAuthResult {
        // Supabase appends tokens as fragment params: naukrisetu://auth#access_token=...
        val fragment = uri.fragment ?: uri.query ?: return OAuthResult.Error("No token data in callback")
        val params = fragment.split("&").associate {
            val (k, v) = it.split("=", limit = 2)
            k to Uri.decode(v)
        }
        val accessToken = params["access_token"] ?: return OAuthResult.Error("Missing access_token")
        val refreshToken = params["refresh_token"] ?: ""
        val email = extractEmailFromJwt(accessToken)
        return OAuthResult.Success(
            accessToken = accessToken,
            refreshToken = refreshToken,
            email = email ?: "",
        )
    }

    /** Extracts the email claim from a Supabase JWT without a full JWT library. */
    private fun extractEmailFromJwt(token: String): String? = try {
        val payload = token.split(".").getOrNull(1) ?: return null
        val padded = payload + "=".repeat((4 - payload.length % 4) % 4)
        val json = String(android.util.Base64.decode(padded, android.util.Base64.URL_SAFE))
        val emailMatch = Regex("\"email\"\\s*:\\s*\"([^\"]+)\"").find(json)
        emailMatch?.groupValues?.get(1)
    } catch (e: Exception) {
        null
    }

    sealed interface OAuthResult {
        data class Success(val accessToken: String, val refreshToken: String, val email: String) : OAuthResult
        data class Error(val message: String) : OAuthResult
    }
}
