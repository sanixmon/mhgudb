# MHGU Codex

[![Android CI](https://github.com/sanixmon/mhgudb/actions/workflows/android.yml/badge.svg)](https://github.com/sanixmon/mhgudb/actions/workflows/android.yml)

An offline Android reference app for Monster Hunter Generations Ultimate. It ships
the full English dataset from
[MHGenDatabase](https://github.com/gatheringhallstudios/MHGenDatabase) (MIT) as a
prebuilt Room database: 129 monsters, 10,877 weapons, 5,637 armor pieces, 2,901
items, 1,355 quests, 205 skill trees, 242 decorations, and 31 locations.

The app is a single-module Kotlin/Jetpack Compose project built around
unidirectional data flow: Room is the source of truth, repositories expose
`Flow`s, ViewModels expose a single `StateFlow<UiState>`, and screens are
stateless composables driven by state plus callbacks.

## Features

- Five bottom-navigation destinations: **Monsters**, **Weapons**, **Armor**,
  **Items**, **More**.
- Eight catalog features with list + detail screens: monsters, weapons, armor,
  items, quests, skills, decorations, locations.
- Detail layouts covering weaknesses (per monster state), hitzones, drops by
  rank, habitats, weapon stats, sharpness gauges (base / +1 / +2) and
  type-specific stats (phial, shelling, notes, ammo), armor resistances and
  skills, item sources (carves, gathering, quest rewards, combining) and usages
  (crafting, upgrades, combining), quest objectives/monsters/rewards, skill
  thresholds and sources, decoration recipes, and map monsters/gathering points.
- Material 3 theme with dynamic color on Android 12+ and a static light/dark
  fallback, shared design-system components with Compose previews, type-safe
  Navigation Compose routes, Hilt DI, and Room opened from a bundled database.
- Edge-to-edge layout with window insets handled once in `MhguApp`; content is
  capped at 840dp and centered so the same UI works on phones and tablets.
- No `INTERNET` permission and `allowBackup="false"`; everything is local.

## Stack

Versions are pinned in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

| Concern | Choice |
| --- | --- |
| Language | Kotlin 2.4.21 |
| UI | Jetpack Compose (BOM 2026.06.01), Material 3 |
| Build | Gradle 8.13, AGP 8.13.2, Kotlin DSL, version catalog |
| Annotation processing | KSP 2.3.12 (no kapt) |
| DI | Hilt 2.58, `hilt-navigation-compose` 1.3.0 |
| Persistence | Room 2.8.5 with `Flow` queries |
| Navigation | Navigation Compose 2.9.8, type-safe `@Serializable` routes |
| Images | Coil 3.5.0 (local drawables only) |
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
Room ── copied once from assets/databases/mhgu_codex.db.gz
```

Rules used throughout the app:

- Composables never read the database and never hold business state.
- A `*ScreenRoute` composable collects the ViewModel state and forwards
  callbacks to the stateless `*Screen` composable.
- Filters and search live in the ViewModel and are combined with the repository
  `Flow` into one immutable UiState.
- Domain models are separate from Room entities so the data layer can be
  replaced (for example with a prebuilt SQLite asset) without touching the UI.
- The database is read-only reference data. Cross-reference columns are
  indexed, and display strings (for example "Rathalos · High Rank · Tail Carve
  · 7%") are prepared by the build script, so the app needs no joins.

### Data flow

```
Room DAO Flow ──► Repository (entity→domain mapping)
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
│   │   │                           LoadingState, MhguListRow, DetailRow
│   │   └── theme/                  Color, Type, Shape, Theme
│   ├── di/                         DatabaseModule, RepositoryModule
│   └── navigation/                 Routes, TopLevelDestination, MhguBottomBar,
│                                   MhguApp, MhguNavHost
├── data/local/                     MhguDatabase (23 entities)
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

`app/src/main/assets/databases/mhgu_codex.db.gz` holds the whole dataset.
`DatabaseModule` opens it with `createFromInputStream` (gunzipping on the fly),
so Room copies it into app storage on first launch. Schema changes bump the
database version; `fallbackToDestructiveMigration` then replaces the old copy
with the new asset.

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

All builds for this project are verified by GitHub Actions; nothing is compiled
locally. To build on a machine that has JDK 17 and the Android SDK:

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

1. `:app:assembleDebug` (also exports the Room schema to `app/schemas/`)
2. `tools/verify_room_schema.py` — fails if the bundled database's tables,
   columns, keys, indices or version differ from what Room expects. Room would
   otherwise only reject it at runtime on a device.
3. `:app:testDebugUnitTest`
4. `:app:lintDebug`

On success it uploads the APK as the **`mhgu-codex-debug-apk`** artifact. On
failure it uploads lint/test reports as **`ci-reports`**. Concurrent runs for the
same ref are cancelled.

## How to add a feature

Using a hypothetical `Palicoes` catalog as the example:

1. **Route** — add `@Serializable data object PalicoesRoute` and
   `@Serializable data class PalicoDetailRoute(val palicoId: Long)` to
   `core/navigation/Routes.kt`.
2. **Data** — create `feature/palicoes/data/PalicoEntities.kt` and
   `PalicoDao.kt`. Use `@Entity`, `@PrimaryKey`, and return
   `Flow<List<...>>` from DAO queries. Add `indices = [Index("...")]` for
   columns that detail queries filter on.
3. **Domain** — add plain data classes in `feature/palicoes/domain/` with no
   Room annotations.
4. **Repository** — define `PalicoRepository` and `PalicoRepositoryImpl` in the
   data package, mapping DAO entities to domain models.
5. **DI** — register the new DAO in `MhguDatabase` and `DatabaseModule`, and
   `@Binds` the repository in `RepositoryModule`.
6. **Data** — add the tables to `TABLES` in `tools/build_database.py` (same
   columns, nullability, primary key and indices as the entities), fill them in
   `build()`, bump `DB_VERSION` together with `MhguDatabase.version`, and
   regenerate the asset.
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

## Data

The bundled database is generated from MHGenDatabase's `mhgu.db` (upstream
`app/src/main/assets/databases/mhgu.db.zip`, last updated December 2018):

```bash
unzip mhgu.db.zip -d /tmp/mhgu
python3 tools/build_database.py /tmp/mhgu/mhgu.db
```

The script needs only the Python 3 standard library. It writes
`app/src/main/assets/databases/mhgu_codex.db.gz` (about 4 MB, 17 MB unpacked)
and is deterministic, so regenerating it from the same source gives the same file.
Some fields are derived during conversion and are not in upstream:

- Item categories (Consumable, Material, Ore, Ticket, Other) come from the item
  icon. Weapons, armor and Palico gear are not listed as items.
- Location terrain (Forest, Snow, Desert, Volcano, Arena, Special) is a fixed
  mapping in the script.
- Skill "category" means whether a decoration exists for the tree ("Jewel") or
  it only comes from armor ("Armor only").
- "Guild" quests are labelled "Hub".

## Attribution

- Game data: [MHGenDatabase](https://github.com/gatheringhallstudios/MHGenDatabase)
  by Gathering Hall Studios, MIT License, Copyright (c) 2014 DavianCorp. See
  [`THIRD_PARTY_NOTICES.md`](THIRD_PARTY_NOTICES.md).
- Monster Hunter is a trademark of Capcom. This is an unofficial fan project and
  is not affiliated with or endorsed by Capcom.
- Icons are Material Symbols (Apache License 2.0), converted to Android vector
  drawables. No Monster Hunter artwork is bundled.
- No license file is included for this project's own code yet; add one before
  distributing it.
