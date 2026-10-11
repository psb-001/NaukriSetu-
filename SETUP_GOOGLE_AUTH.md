# Google Sign-In Setup (5 minutes)

The app is already wired for Google Sign-In via Supabase OAuth. You just need to:

## Step 1 — Google Cloud Console (2 min)

1. Go to [console.cloud.google.com](https://console.cloud.google.com)
2. Create or select a project → **APIs & Services → Credentials**
3. Click **Create Credentials → OAuth 2.0 Client ID**
4. Application type: **Web application**
5. Name: `NaukriSetu`
6. Add Authorized redirect URI:
   ```
   https://miezgejnubmbcumqflab.supabase.co/auth/v1/callback
   ```
7. Copy **Client ID** and **Client Secret**

## Step 2 — Supabase Dashboard (2 min)

1. Go to [supabase.com/dashboard/project/miezgejnubmbcumqflab/auth/providers](https://supabase.com/dashboard/project/miezgejnubmbcumqflab/auth/providers)
2. Find **Google** → toggle **Enable**
3. Paste your **Client ID** and **Client Secret**
4. Save

## Step 3 — Redirect URL allowlist (1 min)

1. Go to [supabase.com/dashboard/project/miezgejnubmbcumqflab/auth/url-configuration](https://supabase.com/dashboard/project/miezgejnubmbcumqflab/auth/url-configuration)
2. Add to **Redirect URLs**:
   ```
   naukrisetu://auth
   ```
3. Save

## Done!

The "Continue with Google" button on the login screen will now:
1. Open a Chrome Custom Tab
2. Show the Google account picker
3. Return the user to the app automatically
4. Load their profile from Supabase

> **Demo fallback**: The email + OTP 1234 login still works as before.
> Google Sign-In is additive — both methods coexist.

## What the app does with Google tokens

```
Browser → Google → Supabase (miezgejnubmbcumqflab) → naukrisetu://auth#access_token=...
     ↓
MainActivity.onNewIntent() parses the fragment
     ↓
Email extracted from JWT → RemoteStore(email).load()
     ↓
Home screen (profile auto-created in `profiles` table if new user)
```
