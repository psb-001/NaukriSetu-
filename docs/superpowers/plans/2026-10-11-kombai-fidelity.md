# Kombai-fidelity pass — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Close the visual/structural gaps between the app and the 18 Kombai mobile designs (M01–M18): wire the 4 illustration SVGs, fix the logo lockup, rebuild Login per M02, add icon tab bar, restyle Home/Matches/Profile per canvas truth.

**Architecture:** Pure UI refinement — no engine/store changes (except additive drawable/test helpers). New `ui/icons/Lucide.kt` (hand-drawn vector paths), `ui/components/` additions (ScoreRing canvas, UnderlineLink, SectionCard), 4 VectorDrawables in `res/drawable/` (already converted via `scripts/svg_to_vector.py`).

**Tech Stack:** Same pins (Kotlin 2.3.21, AGP 9.4.1, Gradle 9.8.1, BOM 2026.09.00).

**Spec:** Kombai canvas `naukrisetu-ui` node screenshots viewed 2026-10-11 (M01/M02/M07/M08/M10) + `docs/design-html/M*.html` (structure/colors/icons) + `docs/design-text/M*.txt` (copy).

## Global Constraints

- Supabase DS tokens unchanged; PJS (display) + Inter (body) — system fonts already in use via FontWeight; no downloadable fonts (demo-local, offline-safe).
- All user-facing strings stay in `Strings.kt`.
- Demo contracts unchanged (OTP 1234, gmail-only, gate-first engine).
- Illustrations: VectorDrawable only (no PNG); `scripts/svg_to_vector.py` is the single converter, idempotent.

## Review Focus

- Illustration renders with wrong aspect (stretch) — expect `ContentScale.Fit` + fixed aspect boxes.
- Score ring arc math wrong at 0/100 — expect sweep = score/100*360, 0 draws track only.
- Tab bar icons rendering as tofu — expect vector paths, verified by screenshot.
- Login role cards losing the stacked title+blurb — expect both lines preserved.
- Underline links implemented as buttons with ripple boxes — expect plain underlined text with click.

---

### Task 1: Illustration drawables + fonts check

**Files:**
- Exist: `app/src/main/res/drawable/ill_*.xml` (4 files, converted), `scripts/svg_to_vector.py`
- Create: `app/src/test/java/com/naukrisetu/VectorAssetTest.kt`

**Interfaces:**
- Produces: `object Illustrations { const val SplashBridge = R.drawable.ill_splash_bridge; ... }` — actually plain R references at call sites; test asserts files exist + parse as XML with `<vector>` root.

- [ ] **Step 1: Write the failing test** — assert each drawable XML exists and has vector root + ≥10 paths:
```kotlin
@Test fun `splash illustration vector exists`() { val f = File("app/src/main/res/drawable/ill_splash_bridge.xml"); assertTrue(f.exists()); assertTrue(f.readText().contains("<vector")); assertTrue("<path".toRegex().findAll(f.readText()).count() >= 10) }
```
(×4 files, one test each)
- [ ] **Step 2: Run test to verify it fails**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.VectorAssetTest" 2>&1 | tail -n 6`
Expected: FAIL — working-dir-relative path won't resolve under Gradle test runner (documents why the test reads via classloader instead). Then fix test to resolve via `../` traversal or absolute res path.

- [ ] **Step 3: Implement** `VectorAssetTest.kt` reading res dir relative to project dir (`System.getProperty("user.dir")` → module dir walk-up), asserting 4 files.
- [ ] **Step 4: Run test to verify it passes**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.VectorAssetTest" 2>&1 | tail -n 6`
Expected: PASS 4/4, BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
cd Y:/NaukriSetu && git add -A && git commit -m "feat: kombai illustration vectors with asset test"
```

### Task 2: Icon set + shared components (ScoreRing, tabs, underline link)

**Files:**
- Create: `app/src/main/java/com/naukrisetu/ui/icons/Lucide.kt`, `app/src/main/java/com/naukrisetu/ui/components/ScoreRing.kt`, `app/src/main/java/com/naukrisetu/ui/components/AppTabs.kt`, `app/src/main/java/com/naukrisetu/ui/components/UnderlineLink.kt`
- Modify: `BottomTabs.kt` (delegate to AppTabs or delete), `JobCard.kt` (use ScoreRing)
- Test: `app/src/test/java/com/naukrisetu/IconMathTest.kt`

**Interfaces:**
- Consumes: Supabase tokens.
- Produces: `object Lucide { val Home, Briefcase, BookOpen, User, Search, Heart, ChevronRight, MapPin, Clock, Wallet, Plus, Camera, FileText, Info, Check, ShieldCheck: ImageVector }`; `@Composable fun ScoreRing(score: Int, size: Dp, stroke: Dp)` (canvas arc, sweep=score/100*360); `@Composable fun SeekerTabs/EmployerTabs/VerifierTabs` (icon + label, selected = Primary tint); `@Composable fun UnderlineLink(text, onClick)`.

- [ ] **Step 1: Write the failing test** — ring sweep math as pure fn `sweepFor(score): Float`:
```kotlin
@Test fun `sweep maps score to degrees`() { assertEquals(331.2f, sweepFor(92)); assertEquals(0f, sweepFor(0)); assertEquals(360f, sweepFor(100)) }
```
- [ ] **Step 2: Run test to verify it fails**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.IconMathTest" 2>&1 | tail -n 6`
Expected: FAIL — sweepFor does not exist.

- [ ] **Step 3: Implement** `sweepFor` + ScoreRing (Canvas drawArc, track #EBEBEB, arc Primary, round caps, centered score text) + 16 lucide ImageVectors (24×24, stroke 2, paths from lucide set) + AppTabs (icon tabs) + UnderlineLink (annotated underlined text, no ripple box).
- [ ] **Step 4: Run test to verify it passes**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --tests "com.naukrisetu.IconMathTest" 2>&1 | tail -n 6`
Expected: PASS, BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
cd Y:/NaukriSetu && git add -A && git commit -m "feat: lucide icon set, score ring, icon tabs"
```

### Task 3: Splash + Login rebuild (M01/M02)

**Files:**
- Modify: `SplashScreen.kt` (illustration + one-line lockup), `LoginScreen.kt` (white Google button + G logo, info box, standalone green Continue, divider, icon role cards), `Strings.kt` (additive entries only)
- Create: `app/src/main/java/com/naukrisetu/ui/icons/GoogleG.kt` (4-path G logo ImageVector from M02 SVG data)

**Interfaces:**
- Consumes: `Illustrations` drawables, `Lucide.Briefcase/ShieldCheck`, auth logic (unchanged).

- [ ] **Step 1: Build + screenshot baseline** (visual task — screenshot is the test)

Run: `cd Y:/NaukriSetu && ./gradlew :app:assembleDebug 2>&1 | tail -n 3`
Expected: BUILD SUCCESSFUL (baseline before edits).

- [ ] **Step 2: Implement** Splash (Image ill_splash_bridge aspect-fit top, Row "Naukri"+Space+"Setu" PJS 600 one line, tagline, green Get started, auto-home note), Login (small top-left lockup, H1, subtitle, white Google button w/ G logo + continueWithGoogle text → fills email? No: Google button advances same as Continue; info box w/ Info icon + demoHint; email field w/ error; GREEN Continue validates; divider lines + orContinueAs; role cards: icon box + title + blurb stacked, click = fill demo email + submit as role).
- [ ] **Step 3: Verify** build + install + screenshot splash & login, view both.

Run: `cd Y:/NaukriSetu && ./gradlew :app:assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk && adb shell am start -n com.naukrisetu/.MainActivity`
Expected: splash matches M01 (illustration, one-line lockup); tap → login matches M02.

- [ ] **Step 4: Commit**

```bash
cd Y:/NaukriSetu && git add -A && git commit -m "feat: splash illustration and login per kombai M01 M02"
```

### Task 4: Home restyle (M07)

**Files:**
- Modify: `HomeScreen.kt`, `JobCard.kt` (compact ranked rows), `Strings.kt` (additive)

**Interfaces:**
- Consumes: ScoreRing, UnderlineLink, AppTabs, ill_banner_ac, engine (unchanged).

- [ ] **Step 1: Implement** pill search (RoundedCornerShape 50 + Search icon), selectable chips (selected = black fill/white text), banner card (text left + illustration right, underlined See the role), hero (huge PJS number + inline label + underlined See all), ranked rows (number, title 2-line, pay·distance, mini ScoreRing, chevron), gap card (underlined How to qualify), KYC card (bar + small green Continue KYC + amber warning box), horizontal course cards (code pill, heart, provider·weeks), icon tabs.
- [ ] **Step 2: Verify** build + install + navigate (login → otp 1234 → onboarding finish → home) + screenshot, view.

- [ ] **Step 3: Commit**

```bash
cd Y:/NaukriSetu && git add -A && git commit -m "feat: home restyle per kombai M07"
```

### Task 5: Matches + Profile restyle (M08–M10)

**Files:**
- Modify: `MatchesScreen.kt` (underline segment tabs, expanded card w/ meta icon row + thin factor bars + outline How-to-qualify + heart Save, compact collapsed rows, empty-search illustration in M17), `ProfileScreen.kt` (timeline nodes, amber Pending pills, warning banner, chevron settings rows, full-width outline Log out), `GapsScreen.kt`/`CoursesScreen.kt`/`PostJobScreen.kt`/`VerifierScreen.kt` (icon tabs swap only)

**Interfaces:**
- Consumes: all Task 2 components + ill_empty_search.

- [ ] **Step 1: Implement** per M08/M09/M10/M17 screenshots + HTML (segment tabs with underline indicator; expanded card: ScoreRing 44 + title + #n of total + meta icon row + reason box + thin bars w/ right labels + outline button + Save heart; timeline: check-circle node → connecting line → numbered nodes; settings rows with trailing chevron + sub-blurbs in Danger for reset).
- [ ] **Step 2: Verify** build + install + screenshots (matches eligible/not-eligible, profile), view.

- [ ] **Step 3: Commit**

```bash
cd Y:/NaukriSetu && git add -A && git commit -m "feat: matches and profile restyle per kombai M08 M10"
```

### Task 6: Full pass — suite, screenshots, detector, push

**Files:** none (verification + `README.md` touch if versions change).

- [ ] **Step 1: Full suite**

Run: `cd Y:/NaukriSetu && ./gradlew :app:testDebugUnitTest --rerun-tasks 2>&1 | tail -n 4`
Expected: ALL PASS (27 tests), BUILD SUCCESSFUL.

- [ ] **Step 2: Screenshot sweep** — splash, login, home, matches, profile; view each, fix leftovers in one batch.
- [ ] **Step 3: Impeccable detector**

Run: `sh "C:/Users/Asus/.pi/agent/skills/impeccable/scripts/impeccable" detect --json <changed targets>`
Expected: no blocking findings; fix floor violations in one batch.

- [ ] **Step 4: Commit + push**

```bash
cd Y:/NaukriSetu && git add -A && git commit -m "docs: kombai fidelity pass notes" && git push
```
