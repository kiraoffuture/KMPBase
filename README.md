# KMPBase

Kotlin Multiplatform base project with **Clean Architecture** and modular feature slices.

## Modules

```
androidApp / desktopApp / iosApp   # platform shells
composeApp                         # app entry, navigation, SessionViewModel
core:domain                        # entities, repository contracts, use cases
core:model                         # network DTOs (@Serializable)
core:common                        # dispatchers, network config
core:network                       # Ktor client, API services
core:database                      # Room KMP cache (BundledSQLiteDriver)
core:data                          # repository implementations, mappers
core:ui                            # theme, shared UI components
core:navigation                    # destinations
core:di                            # Koin modules (data + domain wiring)
feature:auth                       # login flow
feature:home                       # home tab (contact list)
feature:settings                   # settings tab (logout)
```

## Clean Architecture layers

```
Presentation     feature:* + composeApp
    Screen, ViewModel, UiState
    depends on → core:domain (use cases only)

Domain           core:domain
    Entity, AppResult, repository interface, UseCase
    depends on → kotlinx.coroutines only

Data             core:data
    RepositoryImpl, mapper (DTO → Entity), local/remote orchestration
    depends on → core:domain, core:model, core:network, core:database

Infrastructure   core:network, core:database, core:common
    Ktor, Room, Settings, env config
```

### Data flow example (Home)

```
HomeScreen → HomeViewModel
  → ObserveContactsUseCase / RefreshContactsUseCase   (domain)
    → HomeRepository                                  (domain interface)
      → HomeRepositoryImpl                            (data)
        → HomeApiService (ContactDto) + CacheDao + mapper
```

### Dependency rules

- `feature:*` → `core:domain`, `core:ui` (never `core:data`, `core:network`, `core:database`)
- `core:domain` → no dependency on data/UI/framework
- `core:data` → implements domain repository interfaces
- `composeApp` → wires navigation + `appModule` (SessionViewModel); no direct data layer
- features must not depend on each other

## Run

### Environments

The app supports three environments: **develop**, **staging**, **product**.

1. Create env files and set `SERVER_URL` (files are **gitignored** — not committed):

```bash
cp .env.example .env.develop
cp .env.example .env.staging
cp .env.example .env.product
```

Team-shared env templates: [Google Drive (envs)](https://drive.google.com/drive/folders/1snZx6jmEWapsf7mJjdJpj92Ai0CGC9Sf?usp=sharing).

Gradle reads `.env.{appEnv}` first, then falls back to `.env` if missing.

2. Select environment per platform (default: `develop`):

- **Android (Android Studio):** pick an **Android App** run config (`android DevelopDebug`, …) from the run dropdown
- **Desktop:** `-PappEnv=develop|staging|product` (default: `develop`)
- **iOS:** Xcode scheme `ios DevelopDebug`, … — `APP_ENV` from xcconfig → Gradle `-PappEnv`

| Platform | Develop | Staging | Product |
|----------|---------|---------|---------|
| Android | `./gradlew :androidApp:assembleDevelopDebug` | `./gradlew :androidApp:assembleStagingDebug` | `./gradlew :androidApp:assembleProductRelease` |
| Desktop | `./gradlew :desktopApp:run -PappEnv=develop` | `./gradlew :desktopApp:run -PappEnv=staging` | `./gradlew :desktopApp:run -PappEnv=product` |
| iOS (scheme) | `ios DevelopDebug` | `ios StagingDebug` | `ios ProductRelease` |

Shared Xcode schemes (prefix `ios`, no generic `Debug` / `Release`):

- `ios DevelopDebug`, `ios DevelopRelease`
- `ios StagingDebug`, `ios StagingRelease`
- `ios ProductDebug`, `ios ProductRelease`

Each scheme maps to build configuration `DevelopDebug`, `StagingRelease`, etc. Flavor settings (`APP_ENV`, bundle id) live in `iosApp/Configuration/Develop.xcconfig`, `Staging.xcconfig`, `Product.xcconfig`. Per-configuration wrappers (e.g. `DevelopDebug.xcconfig`) also set `KOTLIN_FRAMEWORK_BUILD_TYPE` (`debug` / `release`) for the Kotlin framework build.

### Android from Android Studio

Shared run configurations (type **Android App**) are in **`.idea/runConfigurations/`** and **`.run/`**:

- `android DevelopDebug`, `android DevelopRelease`
- `android StagingDebug`, `android StagingRelease`
- `android ProductDebug`, `android ProductRelease`

Each config runs Gradle **before launch** (`install{Variant}` for debug, `run{Variant}` for release) then deploys via **Android App** (debug only).

After clone: **File → Sync Project with Gradle Files**, then pick a config from the run dropdown (`Android App.android DevelopDebug`, …). If missing, **File → Invalidate Caches → Restart**.

For debug configs, also set **Build Variants → androidApp** to the matching variant (e.g. `developDebug`) so deploy matches the installed APK.

Requires a connected device/emulator with `adb` available (via Android SDK).

### iOS from Android Studio

Requires **macOS**, **Xcode**, and a bootable iOS Simulator.

**Why adding a config in AS does not change git:** by default, run configurations are saved to `.idea/workspace.xml` (local, gitignored). They are not written under `.idea/runConfigurations/` unless you enable **Store as project file** in the Run Configuration dialog.

Shared iOS run configurations are in **`.idea/runConfigurations/`** and **`.run/`** (type **Xcode Application** / `AppleRunConfiguration`):

- `ios DevelopDebug`, `ios DevelopRelease`
- `ios StagingDebug`, `ios StagingRelease`
- `ios ProductDebug`, `ios ProductRelease`

After clone: open project → **File → Sync Project with Gradle Files** → configs appear in the run dropdown → pick a simulator → Run. If missing, **File → Invalidate Caches → Restart**.

To save your own config to git: **Run → Edit Configurations →** (your config) → check **Store as project file** → choose `.run/` (recommended) or `.idea/runConfigurations/`.

Each config uses scheme `ios {Variant}` (e.g. `ios DevelopDebug`) with build configuration `{Variant}` and passes `APP_ENV` via xcconfig → Gradle `-PappEnv`.

### Quick start

- Android: `./gradlew :androidApp:assembleDevelopDebug`
- Desktop: `./gradlew :desktopApp:run` (defaults to `develop`)
- iOS: open `iosApp/` in Xcode and run scheme `ios DevelopDebug`, or use **ios DevelopDebug** in Android Studio

## Tests

```bash
./gradlew :feature:home:jvmTest :core:data:jvmTest :core:domain:jvmTest
```

## Add a new feature

1. Create `feature/<name>` and apply the `kmp.feature` convention plugin.
2. Add use case(s) in `core/domain/.../usecase/<name>/`.
3. Add repository contract in `core/domain/.../repository/` and impl in `core/data/`.
4. Register use cases in `core/di/.../KoinModules.kt` (`domainModule`).
5. Add route(s) in `core/navigation/.../AppDestination.kt`.
6. Create `<Name>ViewModel` calling use cases; register in `feature/<name>/.../<Name>Module.kt`.
7. Add `implementation(projects.feature.<name>)` to `composeApp/build.gradle.kts`.
8. Register the route in `composeApp/.../AppNavHost.kt`.
9. Include the feature module in `settings.gradle.kts`.
10. Load the feature Koin module from `initApp()` via `initKoin { modules(...) }`.

## Stack

- Compose Multiplatform + Material 3
- Koin
- Ktor
- Room KMP (BundledSQLiteDriver)
- kotlinx.serialization
- Navigation Compose (JetBrains AndroidX)
- multiplatform-settings
- Kermit
