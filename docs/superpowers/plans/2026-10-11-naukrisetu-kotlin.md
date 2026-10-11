# NaukriSetu — Implementation Plan (native Kotlin, Kombai UI)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the NaukriSetu Android app in native Kotlin + Jetpack Compose, implementing the 18 mobile Kombai designs (M01–M18) with demo-local state.

**Architecture:** Single-activity Compose app. `ui/theme` = Supabase DS tokens (emerald #3ECF8E, near-black #171717, hairline #DFDFDF, Inter+PJS). `ui/screens` = one file per Kombai design. `data/` = in-memory DemoStore + seed data. `engine/` = pure Kotlin matching logic (gate → rank → explain → gaps). Navigation = Jetpack Navigation Compose with role-based bottom tabs.

**Tech Stack:** Kotlin 2.3.21 (verified stable on Maven Central 2026-10-11; NOT 2.4.21 — unverifiable, Central latest tag is 2.5.0-Beta1), AGP 9.4.1, Gradle 9.8.1 (9.3.0 dist missing ProjectTypeBinding class — broken build), compileSdk 37, minSdk 26, Compose BOM 2026.09.00, activity-compose, navigation-compose, lifecycle-viewmodel-compose. No Firebase, no network, no Hilt (manual wiring, "kotlin simple").

**Spec:** `Y:/UI-prompts.md` (P0–P9 locked) + `docs/design-text/M*.txt` (extracted Kombai copy, 18 screens) + Kombai canvas `naukrisetu-ui` (visual reference via `mcp__kombai__canvas_read`).

## Global Constraints

- Design system is Supabase DS (emerald #3ECF8E / deep #24B47E / soft #4ADE80, ink #171717, ink-mute #707070, hairline #DFDFDF, canvas #FFFFFF, canvas-soft #FAFAFA), NOT the Airbnb tokens in UI-prompts.md — Kombai memory `naukrisetu-ui-theme.md` is binding.
- Buttons 6px radius, cards 12px, near-black (#171717) type on emerald CTA (never white-on-green).
- Every user-facing string in `ui/copy/Strings.kt`; no hard-coded strings in screens.
- Login: any `@gmail.com` + OTP `1234` (30s resend timer); roles: seeker / employer / verifier.
- Engine rules: eligibility gate first, rank survivors only; pending/rejected postings invisible to seekers in BOTH tabs; certificates matched EXACTLY (case-insensitive); city gate filters by profile city.
- Demo-local only: data stays on device; no backend, no OAuth, no UIDAI.
- SDK at `C:/Users/Asus/AppData/Local/Android/Sdk`; test device `Medium_Phone_API_37.0` (API 37).

## Review Focus

- OTP input accepting non-`1234` codes would break the demo contract — expect only `1234` to succeed.
- Phone validation (`10 digits, starts 6–9`) blocking valid input or passing invalid — expect strict accept/reject.
- Pending/rejected jobs leaking into seeker Matches tabs — expect them invisible in both tabs.
- City change not re-running matches — expect instant match recompute on city edit.
- Certificate name matching case-sensitively — expect case-insensitive exact match with the "exact as on certificate" hint.

---

### Task 1: Gradle scaffold + theme + copy + navigation shell

**Files:**
- Create: `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `gradle/wrapper/gradle-wrapper.properties`, `gradle/libs.versions.toml`, `app/build.gradle.kts`, `app/src/main/AndroidManifest.xml`, `app/src/main/java/com/naukrisetu/ui/theme/Theme.kt`, `app/src/main/java/com/naukrisetu/ui/copy/Strings.kt`, `app/src/main/java/com/naukrisetu/ui/nav/Routes.kt`, `app/src/main/java/com/naukrisetu/MainActivity.kt`, `app/src/main/res/values/themes.xml`, `.gitignore`
- Test: `app/src/test/java/com/naukrisetu/ScaffoldTest.kt`

**Interfaces:**
- Consumes: Supabase DS tokens (see Global Constraints); design-text M01.
- Produces: `NaukriSetuTheme` composable; `Strings` object; `Routes` object with route constants `splash, login, otp, onboarding, home, matches, gaps, courses, profile, postJob, myPostings, kycQueue, jobsQueue`; `MainActivity` hosting `NavHost`.

- [ ] **Step 1: Write the failing test** — `ScaffoldTest.kt` asserting `Strings.appName == "NaukriSetu"`, `Routes.home == "home"`, theme colors `Primary == 0xFF3ECF8E`, `Ink == 0xFF171717`:
```kotlin
@Test fun `strings expose app name`() { assertEquals("NaukriSetu", Strings.appName) }
@Test fun `routes expose home`() { assertEquals("home", Routes.home) }
@Test fun `theme tokens match supabase ds`() { assertEquals(0xFF3ECF8E, ThemeColors.Primary.value.toLong() or 0xFF000000) }
```
- [ ] **Step 2: Run test to verify it fails**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.ScaffoldTest" 2>&1 | tail -n 15`
Expected: FAIL — files do not exist.

- [ ] **Step 3: Implement** Gradle 9.3.0 wrapper + version catalog (Kotlin 2.4.21, AGP 9.4.1, Compose BOM 2026.09.00), `MainActivity` + `NavHost`, `Theme.kt` (Supabase tokens), `Strings.kt` (appName, tagline "Skill se opportunity tak.", login/onboarding/home copy from design-text M01–M06), `Routes.kt`.
- [ ] **Step 4: Run test to verify it passes**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.ScaffoldTest" 2>&1 | tail -n 8`
Expected: PASS 3/3, BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
cd Y:/NaukriSetu && git add -A && git commit -m "feat: gradle scaffold with supabase theme, copy deck, nav shell"
```

### Task 2: Engine (gate → rank → gaps) + seed data

**Files:**
- Create: `app/src/main/java/com/naukrisetu/engine/Models.kt`, `app/src/main/java/com/naukrisetu/engine/Canonical.kt`, `app/src/main/java/com/naukrisetu/engine/Engine.kt`, `app/src/main/java/com/naukrisetu/data/Seed.kt`, `app/src/main/java/com/naukrisetu/data/DemoStore.kt`
- Test: `app/src/test/java/com/naukrisetu/EngineTest.kt`

**Interfaces:**
- Consumes: UI-prompts P5/P6/P8 data rules (gate-first, rank-only-survivors, live-only visibility).
- Produces: `data class Seeker(...)`, `data class Opportunity(...)`, `data class Course(...)`; `fun checkEligibility(seeker, opp): List<String>` (empty = eligible); `fun rankEligible(seeker, opps): List<Ranked>` (throws on any ineligible input); `fun gapsFor(seeker, opp): List<Gap>`; `object Seed` (8 opportunities, 12 courses, demo seeker Ravi); `DemoStore` (in-memory CRUD + postingsByEmployer).

- [ ] **Step 1: Write the failing test** — gate rejects Mumbai job for Pune seeker, ranker throws on unfiltered input, gaps name the missing certificate, live-only store visibility:
```kotlin
@Test fun `city gate rejects non-local job`() { assertTrue(checkEligibility(puneSeeker, mumbaiJob).any { it.contains("Mumbai") }) }
@Test fun `ranker throws on unfiltered input`() { assertFailsWith<IllegalArgumentException> { rankEligible(puneSeeker, listOf(mumbaiJob)) } }
@Test fun `gaps name missing certificate`() { assertTrue(gapsFor(seekerNoCert, acJob).any { it.label.contains("AC Repair") }) }
@Test fun `pending posts hidden from seekers`() { assertFalse(seekerVisibleOpps(storeWithPending).any { it.status != OppStatus.Live }) }
```
- [ ] **Step 2: Run test to verify it fails**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.EngineTest" 2>&1 | tail -n 8`
Expected: FAIL — engine does not exist.

- [ ] **Step 3: Implement** `Models.kt` (Seeker/Opportunity/Course/Gap/Ranked + OppStatus), `Canonical.kt` (canonicalSkills list from design-text M06), `Engine.kt` (checkEligibility: city, certificates exact case-insensitive, age, experience; rankEligible: skill 50/commute 20/pay 15/experience 15, throws unless all eligible; gapsFor), `Seed.kt` (8 opps incl. Mumbai + CoolTech cert-gated + Sharma exp-gated, 12 courses incl. ELEC-101/HVAC-201/ELEC-104/PLMB-102, Ravi), `DemoStore.kt`.
- [ ] **Step 4: Run test to verify it passes**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.EngineTest" 2>&1 | tail -n 8`
Expected: PASS, BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
cd Y:/NaukriSetu && git add -A && git commit -m "feat: matching engine with gate-rank-gaps and seed data"
```

### Task 3: Splash + Login + OTP (M01–M03)

**Files:**
- Create: `app/src/main/java/com/naukrisetu/ui/screens/SplashScreen.kt`, `app/src/main/java/com/naukrisetu/ui/screens/LoginScreen.kt`, `app/src/main/java/com/naukrisetu/ui/screens/OtpScreen.kt`
- Modify: `app/src/main/java/com/naukrisetu/MainActivity.kt` (wire routes)
- Test: `app/src/test/java/com/naukrisetu/AuthLogicTest.kt` + `app/src/main/java/com/naukrisetu/ui/auth/AuthState.kt`

**Interfaces:**
- Consumes: `Strings` (login copy), `Routes`, `DemoStore` (Task 2).
- Produces: `object EmailCheck { fun valid(email: String): Boolean }` (ends `@gmail.com`); `class OtpState { fun verify(code: String): Boolean }` (only `1234`); `enum class Role { Seeker, Employer, Verifier }`; composables `SplashScreen(onGetStarted)`, `LoginScreen(onLoggedIn)`, `OtpScreen(email, onVerified)`.

- [ ] **Step 1: Write the failing test** — gmail-only validation, OTP `1234`-only:
```kotlin
@Test fun `non-gmail rejected`() { assertFalse(EmailCheck.valid("ravi@yahoo.com")) }
@Test fun `gmail accepted`() { assertTrue(EmailCheck.valid("ravi.kadam@gmail.com")) }
@Test fun `only 1234 verifies`() { assertTrue(OtpState().verify("1234")); assertFalse(OtpState().verify("0000")) }
```
- [ ] **Step 2: Run test to verify it fails**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.AuthLogicTest" 2>&1 | tail -n 8`
Expected: FAIL — auth logic does not exist.

- [ ] **Step 3: Implement** `AuthState.kt` + three screens per design-text M01–M03 (wordmark, tagline, Google button, demo hint "OTP is 1234", 3 role entries, OTP auto-advance boxes, 30s resend timer, session → Home auto-advance).
- [ ] **Step 4: Run test to verify it passes**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.AuthLogicTest" 2>&1 | tail -n 8`
Expected: PASS, BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
cd Y:/NaukriSetu && git add -A && git commit -m "feat: splash login otp with demo auth"
```

### Task 4: Onboarding 3 steps (M04–M06)

**Files:**
- Create: `app/src/main/java/com/naukrisetu/ui/screens/OnboardingScreen.kt`, `app/src/main/java/com/naukrisetu/ui/onboarding/OnboardingValidator.kt`
- Test: `app/src/test/java/com/naukrisetu/OnboardingTest.kt`

**Interfaces:**
- Consumes: `canonicalSkills()` (Task 2), `DemoStore.upsertSeeker`, `Strings` onboarding copy.
- Produces: `object OnboardingValidator { fun validPhone(p: String): Boolean; fun validName(n: String): Boolean }`; composable `OnboardingScreen(...)` with 3 steps, step dots, Back/Next, role-aware step 3 (worker autocomplete chips max 12 / student goal / trainee interests).

- [ ] **Step 1: Write the failing test** — phone rule `10 digits starts 6–9`, name ≥ 2 chars:
```kotlin
@Test fun `phone rule`() { assertTrue(OnboardingValidator.validPhone("9876543210")); assertFalse(OnboardingValidator.validPhone("12345")); assertFalse(OnboardingValidator.validPhone("5876543210")) }
@Test fun `name rule`() { assertTrue(OnboardingValidator.validName("Ravi")); assertFalse(OnboardingValidator.validName("R")) }
```
- [ ] **Step 2: Run test to verify it fails**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.OnboardingTest" 2>&1 | tail -n 8`
Expected: FAIL.

- [ ] **Step 3: Implement** validator + 3-step screen per design-text M04–M06 (role cards, basics with city gate hint, skills autocomplete from canonical list with chips max 12, persist each step via upsertSeeker).
- [ ] **Step 4: Run test to verify it passes**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.OnboardingTest" 2>&1 | tail -n 8`
Expected: PASS, BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
cd Y:/NaukriSetu && git add -A && git commit -m "feat: 3-step role-first onboarding"
```

### Task 5: Home + Matches (M07–M09, M17–M18)

**Files:**
- Create: `app/src/main/java/com/naukrisetu/ui/screens/HomeScreen.kt`, `app/src/main/java/com/naukrisetu/ui/screens/MatchesScreen.kt`, `app/src/main/java/com/naukrisetu/ui/components/JobCard.kt`, `app/src/main/java/com/naukrisetu/ui/components/BottomTabs.kt`
- Test: extend `EngineTest` — home counts derive from `getMatches()` (no hard-coded counts); matches tabs split eligible/ineligible with live counts.

**Interfaces:**
- Consumes: `DemoStore` + `rankEligible/checkEligibility` (Task 2).
- Produces: `HomeScreen` (greeting, search, category chips, banner, top-3 preview + See all, gap-of-week, KYC card, courses strip, bottom tabs), `MatchesScreen` (Eligible/Not-eligible tabs with counts, score ring cards, expand with factor bars, How-to-qualify → gaps, empty states M17/M18).

- [ ] **Step 1: Write the failing test** — tab counts from engine, hero count live:
```kotlin
@Test fun `matches split matches engine`() { val (e, n) = splitMatches(seeker, liveOpps); assertEquals(5, e.size); assertEquals(3, n.size) }
@Test fun `home hero count is live`() { assertEquals(eligibleCount(seeker, liveOpps), homeHeroCount(seeker, liveOpps)) }
```
- [ ] **Step 2: Run test to verify it fails**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.MatchesLogicTest" 2>&1 | tail -n 8`
Expected: FAIL — splitMatches/homeHeroCount do not exist.

- [ ] **Step 3: Implement** pure helpers `splitMatches`/`homeHeroCount` (testable, in `engine/`) + Home + Matches screens per design-text M07–M09 (names, pay, distance, scores 92/88/74, fail reasons verbatim, tabs, bottom tabs Home/Matches/Courses/Profile).
- [ ] **Step 4: Run test to verify it passes**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.MatchesLogicTest" 2>&1 | tail -n 8`
Expected: PASS, BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
cd Y:/NaukriSetu && git add -A && git commit -m "feat: home and matches with live engine counts"
```

### Task 6: Profile/KYC + Settings + Gaps + Courses (M10–M12)

**Files:**
- Create: `app/src/main/java/com/naukrisetu/ui/screens/ProfileScreen.kt`, `app/src/main/java/com/naukrisetu/ui/screens/GapsScreen.kt`, `app/src/main/java/com/naukrisetu/ui/screens/CoursesScreen.kt`, `app/src/main/java/com/naukrisetu/ui/kyc/KycLogic.kt`
- Test: `app/src/test/java/com/naukrisetu/KycTest.kt`

**Interfaces:**
- Consumes: `DemoStore`, `gapsFor` (Task 2).
- Produces: `object KycLogic { fun aadhaarValid(n: String): Boolean (12 digits); fun progressPct(seeker): Int; fun unlockCount(...): Int }`; `ProfileScreen` (progress header, 3 KYC steps, live unlock banner, settings: language note, About v0.1.0, demo reset, logout); `GapsScreen` (missing rows → catalogue courses, Save/Notify, zero-course empty text); `CoursesScreen` (searchable grid, trade + duration filters, A–Z sort).

- [ ] **Step 1: Write the failing test** — Aadhaar 12-digit, progress math:
```kotlin
@Test fun `aadhaar needs 12 digits`() { assertTrue(KycLogic.aadhaarValid("123456789012")); assertFalse(KycLogic.aadhaarValid("1234")) }
@Test fun `progress counts checks`() { assertEquals(40, KycLogic.progressPct(seekerTwoOfFive)) }
```
- [ ] **Step 2: Run test to verify it fails**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.KycTest" 2>&1 | tail -n 8`
Expected: FAIL.

- [ ] **Step 3: Implement** logic + three screens per design-text M10–M12 (pending badges, "exact as on certificate" hint, privacy line, unlock banner recomputing matches, Save/Notify no-enrollment note).
- [ ] **Step 4: Run test to verify it passes**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.KycTest" 2>&1 | tail -n 8`
Expected: PASS, BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
cd Y:/NaukriSetu && git add -A && git commit -m "feat: kyc profile gaps courses"
```

### Task 7: Employer + Verifier (M13–M16)

**Files:**
- Create: `app/src/main/java/com/naukrisetu/ui/screens/PostJobScreen.kt`, `app/src/main/java/com/naukrisetu/ui/screens/MyPostingsScreen.kt`, `app/src/main/java/com/naukrisetu/ui/screens/VerifierScreen.kt`
- Test: `app/src/test/java/com/naukrisetu/PostingFlowTest.kt`

**Interfaces:**
- Consumes: `DemoStore` (post/approve/reject), engine visibility rule (Task 2).
- Produces: `PostJobScreen` (title/desc/skills autocomplete/criteria builder/city/pay/work, submit → pending); `MyPostingsScreen` (status badges, eligible-seeker counts, edit→pending/delete); `VerifierScreen` (KYC docs + Jobs tabs with counts, Approve/Reject+note, live-update).

- [ ] **Step 1: Write the failing test** — new post starts pending + invisible; approve → live + visible:
```kotlin
@Test fun `new post pending and invisible`() { val p = store.submitPost(draft); assertEquals(OppStatus.Pending, p.status); assertFalse(seekerVisibleOpps(store).any { it.id == p.id }) }
@Test fun `approve makes post live`() { store.approvePost(p.id); assertTrue(seekerVisibleOpps(store).any { it.id == p.id }) }
```
- [ ] **Step 2: Run test to verify it fails**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.PostingFlowTest" 2>&1 | tail -n 8`
Expected: FAIL — submitPost/approvePost do not exist.

- [ ] **Step 3: Implement** store methods + three screens per design-text M13–M16 (criteria rows, "at least 1 required", verifier notes, re-submit flow, role tabs for employer/verifier).
- [ ] **Step 4: Run test to verify it passes**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.PostingFlowTest" 2>&1 | tail -n 8`
Expected: PASS, BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
cd Y:/NaukriSetu && git add -A && git commit -m "feat: employer posting and verifier queues"
```

### Task 8: Assemble, build, install, verify on emulator

**Files:** none new (wiring + `README.md` run instructions).

- [ ] **Step 1: Full unit suite**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest 2>&1 | tail -n 12`
Expected: ALL PASS, BUILD SUCCESSFUL.

- [ ] **Step 2: Debug build**

Run: `cd Y:/NaukriSetu && ./gradlew :app:assembleDebug 2>&1 | tail -n 8`
Expected: BUILD SUCCESSFUL, APK at `app/build/outputs/apk/debug/app-debug.apk`.

- [ ] **Step 3: Wait for boot + install + launch**

Run: `adb wait-for-device; adb shell getprop sys.boot_completed` until `1`; then `adb install -r app/build/outputs/apk/debug/app-debug.apk && adb shell am start -n com.naukrisetu/.MainActivity`
Expected: `Success`, `Starting: Intent`.

- [ ] **Step 4: Eyeball check** — `adb shell dumpsys` / screenshot via `adb exec-out screencap -p > verify.png`; confirm app foreground, no crash (`adb logcat -d | grep -i "naukrisetu.*fatal"` empty).
- [ ] **Step 5: Commit**

```bash
cd Y:/NaukriSetu && git add -A && git commit -m "docs: readme run instructions and emulator verification"
```
