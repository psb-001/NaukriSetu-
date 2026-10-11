# NaukriSetu — G-01 Skills-to-Opportunity Matching Engine (Android)

NaukriSetu is a native Android app that matches **college students / tech freshers (0 yrs)**
and **experienced employees (3+ yrs)** to the right opportunities — and lets
**Government / Corporate Job Adders** publish roles straight into the same engine.

> **One-line pitch:** Gate first, rank survivors only. Every eligible match shows *why it ranks
> where it does*; every locked role shows *exactly which gate failed + a free bridge to unlock it*.

---

## 1. Tech stack (what's actually in this repo)

| Layer | Choice | Where |
|---|---|---|
| App | **Kotlin + Jetpack Compose (Material 3)**, single-activity + Navigation-Compose | `app/src/main/java/com/naukrisetu/MainActivity.kt`, `ui/screens/`, `ui/components/`, `ui/theme/Theme.kt` |
| Matching engine | **Pure-Kotlin G-01 engine** — `checkEligibility` → `splitMatches` → `rankEligible` → `gapsFor` | `engine/Engine.kt`, `engine/Models.kt` |
| Skill semantics | **SkillTaxonomy** — same skill in different words = same skill (threshold ≥ 0.70), ranking-only, never bypasses gates | `engine/SkillTaxonomy.kt` |
| Learning bridge | **LearningResources** — offline static catalogue (freeCodeCamp / YouTube / react.dev / NPTEL-SWAYAM / Kaggle), never empty | `engine/LearningResources.kt` |
| Data | **DemoStore** (in-memory baseline) subclassed by **RemoteStore** (Supabase-backed, optimistic cache + background sync) | `data/DemoStore.kt`, `data/RemoteStore.kt`, `data/Seed.kt` |
| Backend | **Supabase** (Postgres + Auth OTP + REST): `profiles`, `opportunities`, `courses`, `kyc_submissions` | `data/SupabaseConfig.kt`, `data/SupabaseApi.kt`, `supabase/migrations/20261011_init.sql` |
| Auth | **Supabase Email OTP** (`/auth/v1/otp` → `/auth/v1/verify`), demo bypass `123456`, session in `SharedPreferences` | `ui/screens/LoginScreen.kt`, `ui/screens/OtpScreen.kt` |
| AI (opt-in) | **OpenRouter LLM** — conversational project → canonical skill extraction during onboarding; never decides eligibility | `ai/OpenRouterAssistant.kt` (`OPENROUTER_API_KEY` from `local.properties`) |
| Network | Zero-dependency `HttpURLConnection` + `org.json` (no Retrofit/OkHttp — keeps APK ~13 MB) | `data/SupabaseApi.kt` |
| Build | Kotlin 2.3.21 · AGP 9.4.1 · Gradle 9.8.1 · compileSdk/targetSdk 37 · minSdk 26 · Compose BOM 2026.09.00 · JDK 17 | `app/build.gradle.kts`, `gradle/libs.versions.toml` |

No Google Sign-In SDK, no Custom Tabs, no Retrofit, no Hilt — deliberate, to stay lightweight
and reviewable under deadline.

---

## 2. Technical approach

### 2.1 G-01 contract: eligibility is separate from ranking
```
Seeker + Live opportunities
        │ checkEligibility(seeker, opp) → List<String> failures
        ├── failures.isEmpty()  →  ELIGIBLE   → rankEligible() → score + reason
        └── failures.nonEmpty() →  NOT ELIGIBLE → exact reasons + free bridge
```
- **Gates** (`Criterion.City / Certificate / MinAge / MinExperience`) are hard excludes.
  Fail = listed under **✕ Not eligible** with verbatim reason
  (e.g. `Needs experience ≥ 3 years — you have 0`), never ranked.
- **Ranking** applies *only* to survivors: skill 50 / commute 20 / pay 15 / experience 15.
- **Skill wording** (`SkillTaxonomy.matchScore ≥ 0.70`) informs the *skill* points only —
  it can never override a failed gate (PS G-01 anti-bypass rule).
- **Freshers** (0 yrs) are labelled *Fresher friendly*, not penalised; senior roles with
  `MinExperience(3)` correctly lock for students and unlock for employees.
- **Courses are bridges, not a platform**: `gapsFor()` maps each missing skill/certificate
  to 1–3 free resources via offline `LearningResources`; locked cards embed
  `📚 Unlock this role — free learning path` + `How to qualify →`.

### 2.2 Three portals, one engine, one Live board
| Portal | Login | Lands on | Sees |
|---|---|---|---|
| 🎓 Student (0 yrs) | `student@naukrisetu.com` | Home → Matches | 6 eligible fresher roles, 2 locked (Mumbai city / 3-yr exp) |
| 💼 Employee (3+ yrs) | `employee@naukrisetu.com` | Home → Matches | Unlocks `Senior Backend Lead ₹90k–1.2L` as #1 @ 95% |
| 🏛️ Job Adder | `officer.nic@gov.in` / `employer@techcorp.com` | PostJob → MyPostings | **Full Live board** students see (`seekerVisibleOpps`), own posts badged `YOURS`, only own posts removable |

- Adder posts go **Live instantly** (`submitPost` → `approvePost`) so the demo loop
  (adder publishes → student matches) works without a verifier round-trip.
- Verifier queue (`VerifierScreen`) still exists for KYC + Pending/Rejected audit;
  `seekerVisibleOpps` enforces Live-only visibility in the engine, not the UI.

### 2.3 Data flow
```
LoginScreen (portal switcher) → OtpScreen (Supabase verify / 123456 demo)
  → RemoteStore(email).load() → Supabase REST (profiles / opportunities / kyc_submissions)
  → DemoStore cache (optimistic writes, background push)
  → Home / Matches / Gaps (splitMatches, gapsFor) → 1-Tap Apply (DemoStore._appliedJobs → ✓ Applied)
Session: SharedPreferences (user_email + user_role) → relaunch restores Home/PostJob
```

### 2.4 AI usage (bounded)
- Onboarding Step 3: user describes a project in plain words → OpenRouter extracts
  canonical skills → merged with `SkillTaxonomy.findCanonicalEquivalents`.
- AI never gates, ranks, or filters. If the key is missing/offline, manual skill chips apply.

---

## 3. Run & demo

### Prereqs
- JDK 17 (`export JAVA_HOME=/opt/homebrew/opt/openjdk@17`), Android SDK
  (`sdk.dir` in `local.properties`), emulator (tested `Medium_Phone_API_37.0`, API 37).

### Commands
```bash
./gradlew :app:testDebugUnitTest      # unit tests (PostingFlow, MatchesLogic, Scaffold…)
./gradlew :app:assembleDebug          # APK → app/build/outputs/apk/debug/app-debug.apk
./gradlew :app:assembleRelease        # release APK → app/build/outputs/apk/release/app-release-unsigned.apk
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.naukrisetu/.MainActivity
```

### 3-tap demo script (OTP `123456` everywhere)
1. **Student**: login `student@naukrisetu.com` → Home `6 new matches` →
   Matches `[✓ Eligible (6)]` / `[✕ Not eligible (2)]` → open a locked role →
   exact gate reason + `📚 Unlock this role` bridge → `1-Tap Apply` → `✓ Applied`.
2. **Employee**: Home pill `💼 Switch to Employee` (or login `employee@naukrisetu.com`) →
   `#1 Senior Backend Lead 95%` → Apply.
3. **Adder**: login `officer.nic@gov.in` → PostJob (city + min-exp + pay + skills) →
   `Publish to NaukriSetu Engine` → MyPostings shows **all 8+ live roles** with
   `YOURS` on the new one → same role appears in student Matches instantly.

---

## 4. Releases (installable APK)

| Version | versionCode | Contents |
|---|---|---|
| [v1.0.0](https://github.com/psb-001/NaukriSetu-/releases/tag/v1.0.0) | 2 | Rebuilt login entry, NaukriSetu rebrand, learning bridges, dead-code trim |
| **v1.1.0** (this) | 3 | Adder sees **full Live board** + `YOURS` badge + instant-Live publish |

Build + sign + install:
```bash
./gradlew :app:assembleRelease
cp app/build/outputs/apk/release/app-release-unsigned.apk app/build/outputs/apk/release/NaukriSetu-v1.1.0.apk
apksigner sign --ks ~/.android/debug.keystore --ks-pass pass:android --out app/build/outputs/apk/release/NaukriSetu-v1.1.0.apk app/build/outputs/apk/release/NaukriSetu-v1.1.0.apk
adb install -r app/build/outputs/apk/release/NaukriSetu-v1.1.0.apk
```

> **Stale-APK fix:** if an old install shows no changes, `adb uninstall com.naukrisetu`
> first — Android won't auto-update a `versionCode 1` debug build to an unsigned
> release. Every release here bumps `versionCode` so updates apply cleanly.

---

## 5. Repo map (where to look)

```
app/src/main/java/com/naukrisetu/
  MainActivity.kt                  # NavHost (splash/login/otp/onboarding/home/matches/gaps/courses/profile/postJob/myPostings/…)
  engine/Engine.kt                 # checkEligibility / splitMatches / rankEligible / gapsFor / seekerVisibleOpps
  engine/Models.kt                 # Seeker / Opportunity / Criterion / Course / Ranked / Gap
  engine/SkillTaxonomy.kt          # semantic equivalence clusters + 0.70 threshold
  engine/LearningResources.kt      # offline free-course bridge catalogue
  data/DemoStore.kt                # _appliedJobs / applyJob / submitPost / approvePost / postingsByEmployer
  data/RemoteStore.kt              # Supabase sync (optimistic cache + IO scope)
  data/Seed.kt                     # 8 student/tech roles, demoSeeker (Aman, 0y) + experiencedSeeker (Rahul, 3y)
  ui/screens/LoginScreen.kt        # 3-way portal switcher + OTP + 1-tap demo rows
  ui/screens/PostJobScreen.kt      # PostJob form + MyPostings full-live-board
  ui/screens/{Home,Matches,Gaps,Onboarding,Profile,Otp,Splash,Verifier}Screen.kt
  ai/OpenRouterAssistant.kt        # project-talk → skills extractor
supabase/migrations/20261011_init.sql  # profiles / opportunities / courses / kyc_submissions
```

## Versions (pinned 2026-10-11)

Kotlin 2.3.21 · AGP 9.4.1 · Gradle 9.8.1 · compileSdk 37 · minSdk 26 ·
Compose BOM 2026.09.00. AGP 9+ has built-in Kotlin: do NOT re-add the
`kotlin-android` plugin. Java processes here need IPv4
(`-Djava.net.preferIPv4Stack=true`, already in `gradle.properties`/`gradlew`).
