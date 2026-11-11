# MHGU Codex

[![Android CI](https://github.com/sanixmon/mhgudb/actions/workflows/android.yml/badge.svg)](https://github.com/sanixmon/mhgudb/actions/workflows/android.yml)

An offline Android database app scaffold for Monster Hunter Generations Ultimate
reference content. This repository contains **structure and placeholder data
only** — no real monster, weapon, armor, item, quest, skill, decoration, or map
data is bundled.

The app is a single-module Kotlin/Jetpack Compose project built around
unidirectional data flow: Room is the source of truth, repositories expose
`Flow`s, ViewModels expose a single `StateFlow<UiState>`, and screens are
stateless composables driven by state plus callbacks.

## What is in the scaffold

- Five bottom-navigation destinations: **Monsters**, **Weapons**, **Armor**,
  **Items**, **More**.
- Eight catalog features with list + detail screens: monsters, weapons, armor,
  items, quests, skills, decorations, locations.
- Detail layouts covering weaknesses, hitzones, drops by rank, habitats, weapon
  stats and sharpness, armor resistances and skills, item sources/usages, quest
  objectives/monsters/rewards, skill thresholds and sources, decoration slots,
  and map monsters/gathering points.
- Material 3 theme with dynamic color on Android 12+ and a static light/dark
  fallback, shared design-system components with Compose previews, type-safe
  Navigation Compose routes, Hilt DI, and Room with a bundled JSON seed.
- Edge-to-edge layout with window insets handled once in `MhguApp`; content is
  capped at 840dp and centered so the same UI works on phones and tablets.
- No `INTERNET` permission and `allowBackup="false"`; everything is local.

## Stack

Versions are pinned in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

| Concern | Choice |
| --- | --- |
| Language | Kotlin 2.4.21 |
| UI | Jetpack Compose (BOM 2026.09.00), Material 3 |
| Build | Gradle 8.13, AGP 8.13.2, Kotlin DSL, version catalog |
| Annotation processing | KSP 2.3.12 (no kapt) |
| DI | Hilt 2.60.1, `hilt-navigation-compose` 1.4.0 |
| Persistence | Room 2.8.5 with `Flow` queries |
| Navigation | Navigation Compose 2.10.2, type-safe `@Serializable` routes |
| Images | Coil 3.6.3 (local drawables only) |
| Serialization | kotlinx.serialization 1.12.0 |
| SDK | `minSdk 26`, `compileSdk`/`targetSdk` 36, JDK 17 |

## Architecture

```
ui/            stateless Compose screens, UiState, ViewModels
   │  state + callbacks
   ▼
domain/        plain data models (no Android or Room types)
   ▲
   │  mapping
data/          Room entities + DAOs, repository interfaces/implementations
   │
   ▼
Room ── seeded once from assets/seed/placeholder_seed.json
```

Rules used throughout the scaffold:

- Composables never read the database and never hold business state.
- A `*ScreenRoute` composable collects the ViewModel state and forwards
  callbacks to the stateless `*Screen` composable.
- Filters and search live in the ViewModel and are combined with the repository
  `Flow` into one immutable UiState.
- Domain models are separate from Room entities so the data layer can be
  replaced (for example with a prebuilt SQLite asset) without touching the UI.
- Cross-reference tables use composite primary keys, so re-seeding is
  idempotent.

### Data flow

```
Room DAO Flow ──► Repository (seeding + entity→domain mapping)
                        │
                        ▼
              ViewModel combine(search, filter, data)
                        │
                        ▼
              StateFlow<UiState> ──► Route ──► stateless Screen
```

## Package layout

```
app/src/main/java/com/sanix/mhgucodex/
├── MainActivity.kt                 edge-to-edge entry point
├── MhguApplication.kt              @HiltAndroidApp
├── core/
│   ├── designsystem/
│   │   ├── component/              SearchBar, FilterChipRow, StatBar, ElementIcon,
│   │   │                           SectionHeader, EmptyState, ErrorState,
│   │   │                           LoadingState, MhguListRow, DetailRow, DemoLabel
│   │   └── theme/                  Color, Type, Shape, Theme
│   ├── di/                         DatabaseModule, RepositoryModule
│   └── navigation/                 Routes, TopLevelDestination, MhguBottomBar,
│                                   MhguApp, MhguNavHost
├── data/local/                     MhguDatabase (23 entities), SeedDao,
│                                   SeedModels, DatabaseSeeder
└── feature/
    ├── monsters/{data,domain,ui}
    ├── weapons/{data,domain,ui}
    ├── armor/{data,domain,ui}
    ├── items/{data,domain,ui}
    ├── quests/{data,domain,ui}
    ├── skills/{data,domain,ui}
    ├── decorations/{data,domain,ui}
    ├── locations/{data,domain,ui}
    └── more/ui/                    secondary navigation + about/disclaimer
```

`app/src/main/assets/seed/placeholder_seed.json` is the single seed document.
`DatabaseSeeder` checks the `monsters` table count under a `Mutex`, parses the
JSON, and inserts every table in one Room transaction.

### Database tables

| Feature | Tables |
| --- | --- |
| Monsters | `monsters`, `monster_weakness`, `monster_hitzones`, `monster_drops`, `monster_habitats` |
| Weapons | `weapons` |
| Armor | `armor`, `armor_skills` |
| Items | `items`, `item_sources`, `item_usages` |
| Quests | `quests`, `quest_monsters`, `quest_rewards` |
| Skills | `skills`, `skill_levels`, `skill_armor_sources`, `skill_decoration_sources` |
| Decorations | `decorations`, `decoration_skills` |
| Locations | `locations`, `location_monsters`, `gathering_points` |

## Navigation

```
Monsters ──► MonsterDetail
Weapons  ──► WeaponDetail
Armor    ──► ArmorDetail
Items    ──► ItemDetail
More
 ├── Quests       ──► QuestDetail
 ├── Skills       ──► SkillDetail
 ├── Decorations  ──► DecorationDetail
 └── Locations    ──► LocationDetail
```

Routes are `@Serializable` objects/data classes in
`core/navigation/Routes.kt`. The bottom bar selects an item with
`NavDestination.hasRoute`, saves/restores state per tab, and stays visible on
detail screens. Detail ViewModels read their arguments with
`SavedStateHandle.toRoute<T>()`.

## Build and test

All builds for this project are verified by GitHub Actions; the scaffold was
not compiled locally. To build on a machine that has JDK 17 and the Android SDK:

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
```

The debug APK is written to `app/build/outputs/apk/debug/`.

### Continuous integration

[`.github/workflows/android.yml`](.github/workflows/android.yml) runs on every
push and pull request with JDK 17 (Temurin), Gradle dependency caching via
`gradle/actions/setup-gradle`, and these gates:

1. `:app:assembleDebug`
2. `:app:testDebugUnitTest`
3. `:app:lintDebug`

On success it uploads the APK as the **`mhgu-codex-debug-apk`** artifact. On
failure it uploads lint/test reports as **`ci-reports`**. Concurrent runs for the
same ref are cancelled.

## How to add a feature

Using a hypothetical `Palicoes` catalog as the example:

1. **Route** — add `@Serializable data object PalicoesRoute` and
   `@Serializable data class PalicoDetailRoute(val palicoId: Long)` to
   `core/navigation/Routes.kt`.
2. **Data** — create `feature/palicoes/data/PalicoEntities.kt` and
   `PalicoDao.kt`. Use `@Entity`, `@PrimaryKey`, `@Serializable`, and return
   `Flow<List<...>>` from DAO queries.
3. **Domain** — add plain data classes in `feature/palicoes/domain/` with no
   Room annotations.
4. **Repository** — define `PalicoRepository` and `PalicoRepositoryImpl` in the
   data package. Call `seeder.ensureSeeded()` before observing the DAO, and map
   entities to domain models.
5. **DI** — register the new DAO in `MhguDatabase` and `DatabaseModule`, insert
   its tables in `SeedDao`/`DatabaseSeeder`, and `@Binds` the repository in
   `RepositoryModule`.
6. **Seed** — add the new lists to `PlaceholderSeed`, the asset JSON, and the
   `insert()` order in `DatabaseSeeder`.
7. **State + ViewModel** — add `PalicoUiState.kt` and `PalicoViewModels.kt`.
   Combine search/filter `MutableStateFlow`s with the repository flow and expose
   one `StateFlow`.
8. **UI** — build a stateless `PalicoListScreen` and `PalicoDetailScreen` using
   the shared components, plus thin `*ScreenRoute` wrappers that call
   `hiltViewModel()`.
9. **Navigation** — add both destinations to `MhguNavHost`, and a row to
   `MoreScreen` if the feature is secondary.
10. **Test** — add a JVM unit test for any pure mapping/filter logic. Avoid
    Android dependencies in `src/test`.

Keep new code inside the existing `data` / `domain` / `ui` split and reuse
`core/designsystem` components rather than introducing one-off styles.

## Placeholder data

Every seeded display name starts with `Placeholder` and every description says
it is a demo record. The dataset exists to prove that each list, detail section,
filter, and cross-reference renders — it is **not** game data and must not be
used for play.

To ship real data later, the intended path is to keep the same domain models and
repositories but replace `DatabaseSeeder` with a Room `createFromAsset(...)`
database built from a licensed dataset. A known candidate is
[`gatheringhallstudios/MHGenDatabase`](https://github.com/gatheringhallstudios/MHGenDatabase)
(MIT, ships `mhgu.db`); review its license and attribution requirements before
redistributing any data or icons. That database is not included here.

## Attribution

- Icons are Material Symbols (Apache License 2.0), converted to Android vector
  drawables. No Monster Hunter artwork is bundled.
- No license file is included yet; add one before distributing the project.
