# NaukriSetu (Android demo)

Native Kotlin + Jetpack Compose implementation of the NaukriSetu Kombai UI
(`.kombai/canvas/naukrisetu-ui.canvas`, 18 mobile designs M01–M18).
Demo-local: any `@gmail.com` login, OTP `1234`, all data stays on device.

## Prereqs

- JDK 17, Android SDK (set path in `local.properties` as `sdk.dir=...`)
- Emulator AVD (tested on `Medium_Phone_API_37.0`, API 37)

## Run

```bash
cd Y:/NaukriSetu
./gradlew :app:testDebugUnitTest      # unit tests (21 tests)
./gradlew :app:assembleDebug          # APK → app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.naukrisetu/.MainActivity
```

## Demo script

1. Splash → Get started → Login (`ravi.kadam@gmail.com`) → OTP `1234`
2. Onboarding: role → basics (phone 10 digits starting 6–9, city required) → skills
3. Home: live match count, gap-of-week, KYC card, courses strip
4. Matches: Eligible (5) / Not eligible (3) tabs with verbatim fail reasons
5. Profile: Aadhaar (12-digit) → address → certificates; progress % + unlock banner
6. Employer: Post a job → pending → Verifier approves → live in seeker matches

## Versions (pinned 2026-10-11)

Kotlin 2.3.21 · AGP 9.4.1 · Gradle 9.8.1 · compileSdk 37 · minSdk 26 ·
Compose BOM 2026.09.00. AGP 9+ has built-in Kotlin: do NOT re-add the
`kotlin-android` plugin. Java processes here need IPv4
(`-Djava.net.preferIPv4Stack=true`, already in `gradle.properties`/`gradlew`).
