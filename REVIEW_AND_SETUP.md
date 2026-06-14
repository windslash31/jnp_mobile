# Japan Mission — Codebase Review, Mac Setup & Action Plan

_Reviewed 2026-06-14. Target trip: October 2026._

## TL;DR

This is a **Google AI Studio–generated** Kotlin + Jetpack Compose app: a personal Japan
trip companion with 5 tabs (Plan, Map, Food, Wallet, Intel). The UI is genuinely
impressive and feature-rich for a first app. But underneath it has **one critical
functional bug, no buildable toolchain on your Mac, and the classic "generated code"
problems**: a 2,871-line monolith file, placeholder package names, a pile of unused
dependencies, and almost no real tests.

Nothing here is scary. It's a great learning project. The list below is ordered so you
fix the things that will actually bite you on the trip first.

---

## 1. What the app is (as built)

| Tab | Screen fn | What it does |
|-----|-----------|--------------|
| Plan | `ItineraryScreen` | 11-day itinerary, check off steps, swipe to edit/delete, auto-prompt to log expense |
| Map | `MapScreen` | Leaflet map inside a WebView, pins per day, "Launch Google Maps" buttons |
| Food | `GourmetScreen` | Curated restaurant checklist with search + category filters |
| Wallet | `BudgetScreen` | Expense logger vs. a ¥150k budget, category breakdown |
| Intel | `IntelScreen` | Booking deadlines, JP phrasebook, 30 travel tips, dark-mode toggle |

**Stack:** Compose Material 3, Room (local DB), `AndroidViewModel` + `StateFlow`. Single
activity. Kotlin 2.2, AGP 9.1.1, compileSdk 36 (Android 16), minSdk 24.

**Persistence reality:** Room stores **only** (a) logged transactions, (b) itinerary
checkbox state, (c) food checkbox state. The itinerary content itself is static Kotlin
data — see the critical bug below.

---

## 2. Setting up your Mac (currently a blank slate)

Your machine has Homebrew and git, but **no JDK, no Android SDK, no Android Studio, no
emulator**. You cannot build or run this app yet. Here's the path.

> AGP 9.1.1 is bleeding-edge. It needs **JDK 17+** (we'll use 21 LTS) and a **very recent
> Android Studio** (2025+ / "Narwhal" or newer). If Android Studio can't open the project,
> that version mismatch is the first thing to check.

### Step A — Install the JDK
```bash
brew install --cask temurin@21
```

### Step B — Install Android Studio (brings the SDK + emulator)
```bash
brew install --cask android-studio
```
Then open Android Studio once and complete the setup wizard (it downloads the SDK,
platform-tools, and a system image). This is the easiest way to get a correct SDK.

### Step C — Set environment variables
Add to `~/.zshrc`:
```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
```
Then `source ~/.zshrc`.

### Step D — Fix this project's missing build files
These are required and currently missing (see §3, C1/C2):
```bash
cd /Users/ananda.anugrah/Downloads/jnp_mobile

# 1) Recreate the debug keystore the build expects (it's committed as base64)
base64 -d debug.keystore.base64 > debug.keystore

# 2) Create the .env file the Secrets Gradle plugin looks for (can be empty —
#    the app doesn't actually call any Gemini/network API; see M3)
touch .env .env.example

# 3) Generate the Gradle wrapper so you can build from the terminal.
#    Easiest: let Android Studio do it on first import. Or, once a JDK is on PATH
#    and you have a system 'gradle', run:  gradle wrapper
```

### Step E — Open & run
1. Android Studio → **Open** → choose this folder.
2. Let it sync Gradle (first sync downloads dependencies; slow once).
3. Create an emulator (Device Manager → Pixel + Android 16) **or** plug in a phone with
   USB debugging on.
4. Press **Run ▶**.

---

## 3. Findings by severity

### 🔴 Critical — fix before the trip

**C1 — Itinerary edits are never saved (data loss).**
`MainViewModel._itineraryDays` is seeded from the static `ItineraryData.days` and every
add/edit/delete only mutates the in-memory `StateFlow`. Nothing writes itinerary content
to Room. **Every edit you make to your plan is wiped when the app restarts.** Only the
checkboxes and wallet survive. For a trip planner this is the #1 thing to fix — persist
the itinerary (Room table or a JSON snapshot in DataStore).

**C2 — Project can't be built as-is.** No Gradle wrapper committed (`gradlew`,
`gradle-wrapper.jar/properties` all missing) and no `debug.keystore` (only its `.base64`).
A fresh clone won't build until §2/Step D is done.

### 🟠 High

**H1 — No `.gitignore`.** You risk committing `debug.keystore`, `.env`, `local.properties`,
`/build` dirs, and (worst) a future release keystore or signing passwords. Add a standard
Android `.gitignore` now.

**H2 — Dark-mode + theme state isn't durable.** `themeMode` in `MainTabApp` uses
`remember` (not `rememberSaveable`) and isn't persisted, so it resets on rotation and every
launch. The toggle is also hidden behind a gear icon on the Intel tab — hard to find.

**H3 — Placeholder identity.** Package/namespace is `com.example`; `applicationId` is an
auto-generated `com.aistudio.japantrip.qwxpt`. Rename to something real
(e.g. `com.windslash.japanmission`) before you ever publish or share an APK.

### 🟡 Medium

**M1 — One 2,871-line file.** `MissionScreens.kt` holds all 5 screens, every dialog, the
theme color palette, the HTML map template, helpers, and the bottom nav. Split into
`ui/screens/*Screen.kt`, `ui/theme/BentoColors.kt`, `ui/components/*`. This is the single
biggest "learning + maintainability" win.

**M2 — WebView is over-permissive.** `MIXED_CONTENT_ALWAYS_ALLOW` is unnecessary (all your
URLs are https), and step titles/notes are concatenated straight into JavaScript
(`bindPopup("<b>" + pt.title ...)`). It's your own data so real risk is low, but it's a
genuine injection pattern. Drop mixed-content; escape values.

**M3 — ~6 unused dependencies.** Retrofit, Moshi, OkHttp, logging-interceptor,
converter-moshi, and the Firebase BOM are all declared but never used — there is **no
network layer in the app at all**, despite `metadata.json` claiming a Gemini capability.
Remove them to shrink the build and reduce confusion.

**M4 — Tests are placeholders.** `2+2=4`, an activity smoke-launch, and one screenshot.
None of the real logic (progress %, add/edit/delete steps, category totals) is covered.
Once C1 is fixed, ViewModel unit tests are easy, high-value, and a great way to learn.

### 🟢 Low / polish

- **L1 — Recenter button does nothing.** `recenterTrigger` is incremented by the map's
  refresh FAB but never read anywhere — dead state.
- **L2 — "Alternative" checks don't count toward progress.** `progressPercent` sums only
  morning/afternoon/evening, so ticking an Alternative step never moves the bar.
- **L3 — Day selector reads static data.** The Plan/Map day sliders iterate
  `ItineraryData.days` while content reads the editable `itineraryDays` — they'd desync if
  days became editable.
- **L4 — Fragile `setTag(id, …)`** in the WebView uses the view's (unset) id as the tag key.
- **L5 — Hardcoded everywhere.** Names ("Jessi & Putra"), ¥150k budget, all copy. Fine for
  personal use; blocks reuse/i18n.
- **L6 — Accessibility:** several empty `contentDescription`s and 7–9sp text.

---

## 4. Action items (impact × effort)

| # | Action | Impact | Effort | Priority |
|---|--------|--------|--------|----------|
| C2 | Set up Mac toolchain + missing build files (§2) | Unblocks everything | M | **Do first** |
| H1 | Add `.gitignore` | Prevents secret leaks | XS | **Do first** |
| C1 | Persist itinerary edits to Room/DataStore | Stops trip-data loss | M | **High** |
| H3 | Rename package off `com.example` | Required to publish | S | High |
| H2 | `rememberSaveable` + persist theme; surface toggle | UX correctness | S | High |
| M3 | Remove unused deps (Retrofit/Moshi/OkHttp/Firebase) | Faster, cleaner build | S | Medium |
| M1 | Split `MissionScreens.kt` into per-screen files | Maintainability + learning | M–L | Medium |
| M4 | Real ViewModel unit tests | Confidence, learning | M | Medium |
| M2 | Harden WebView (mixed content + escaping) | Security hygiene | S | Medium |
| L1–L4 | Fix recenter, alt-progress, tag, desync | Correctness polish | S each | Low |
| — | Pre-trip: offline maps, currency, dark-mode default | Trip usefulness | M | Pre-Oct |

---

## 5. Suggested order (3 phases)

**Phase 0 — "Make it run & safe" (this week)**
C2 (Mac setup) → H1 (.gitignore) → confirm it launches on an emulator.

**Phase 1 — "Make it correct" (next)**
C1 (persist itinerary) → H2 (theme) → H3 (package) → M4 (tests around the logic you just
changed, so you don't regress).

**Phase 2 — "Make it clean & trip-ready" (before October)**
M3 (drop unused deps) → M1 (split the monolith) → M2 (WebView) → L-items → trip polish
(offline map tiles, default to dark mode at night, currency notes).

---

## Feature ideas worth considering before the trip
- **Offline support** — cache map tiles / make the itinerary fully offline (data roaming in
  Japan is unreliable; an eSIM helps but offline-first is safer).
- **Real currency** — show ¥→IDR alongside totals.
- **Export/share** the itinerary (PDF or link) for your travel companion.
- **Per-day budget pacing** instead of one ¥150k bar.

---

# Addendum — Specialist re-review & "make it a proper app" plan (2026-06-14)

Three specialist agents (mobile architecture, Kotlin/Compose, product) re-reviewed the
code. Full backlog is tracked in the task list. Highlights below.

## New confirmed bugs (beyond the first pass)
- **Undo is broken for "Alternative" steps** — `undoLastDelete` (`MainViewModel.kt:137-145`)
  has no `"alternative"` branch, so tapping UNDO on a deleted alt step does nothing and the
  delete is permanent.
- **Checkbox state misaligns on insert/reorder** — check keys are positional
  (`"d0-m-0"`); inserting/sorting a step shifts indices so persisted checks point at the
  wrong steps. Fix by keying checks to stable step IDs (do this with C1).
- **Progress % ignores Alternative steps** — `progressPercent` sums only morning/afternoon/
  evening, so ticking an alt never moves the bar.
- **Map "recenter" button is dead** — `recenterTrigger` is incremented but never read.
- **No bounds guard** on `itineraryDays[selectedDayIndex]` — crash risk on stale index.
- **Dead network deps ship in the APK** — Retrofit/Moshi/OkHttp/Firebase are `implementation`
  (not commented out) and compiled in (~0.5 MB) despite no network code.
- **~30 theme colors as `@Composable get()`** re-read a CompositionLocal on every
  recomposition — replace with a `staticCompositionLocalOf<AppColors>` object.

## Generalization: from "Japan app" → real travel companion
**Vision:** a multi-destination, multi-currency app where users create & manage their own
trips, with offline support and shareable plans.

**Must-haves to stop being a Japan app:**
1. A **Trip** entity in Room (destination, dates, currency, budget, traveler names) — the
   container everything else hangs off.
2. **Itinerary/Food/Tips/Phrasebook move into Room** (trip-scoped, user-editable); keep the
   curated Japan content as a *loadable template*, not the hardcoded default.
3. **Multi-currency** (per-trip currency + manual rate + conversion) replacing JPY/¥150k.
4. **Multi-trip home screen**; thread `tripId` through all queries/ViewModels.

**Keep / Generalize / Cut:**
- KEEP: 5-tab structure, swipe gestures, progress bar, category budget breakdown, the map.
- GENERALIZE: the Rookie→Shogun "tactical" theme → make it an **optional** toggle (it's a
  charming asset, but a serious traveler may want it off); Food/Phrasebook/Tips → user data.
- CUT (as hardcoded): the fixed Japan itinerary, ¥150k budget, "Jessi & Putra" names →
  replace with per-trip configurable values.

## Recommended architecture (pragmatic for a solo beginner — don't over-engineer)
- **Single `:app` module**, 3 layers: `data/` (Room, DataStore, repos) · `domain/` (models)
  · `ui/` (screens, components, theme, per-screen viewmodels).
- **Navigation Compose** with type-safe routes (replace string-tab switching).
- **Per-screen ViewModels** scoped to a `tripId` (split the God `MainViewModel`).
- **Hilt** for DI (one afternoon to learn; replaces manual wiring).
- **Room** as single source of truth for all user data; **DataStore** for settings
  (active trip, theme, currency). Replace `fallbackToDestructiveMigration` with real
  migrations before shipping.

## Roadmap (mirrors the task board)
- **Phase 1 — Correct & Generalize (pre-October):** C1 persistence · Trip schema ·
  de-hardcode UI · multi-currency · theme/settings · package rename · bug batch · tests.
- **Phase 2 — Multi-trip & trip-ready:** trip selector · Navigation + per-screen VMs · Hilt ·
  offline maps · export/share · booking reminders · generalize Food/Phrasebook · packing list.
- **Phase 3 — Clean & delight:** split the monolith · Compose perf · Kotlin idioms ·
  WebView hardening · quick-wins batch · accessibility & i18n.
