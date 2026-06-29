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

- Android: `./gradlew :androidApp:assembleDebug`
- Desktop: `./gradlew :desktopApp:run`
- iOS: open `iosApp/` in Xcode and run

## Tests

- Common/JVM: `./gradlew :feature:home:jvmTest :core:data:jvmTest`
- Android host: `./gradlew :core:model:testAndroidHostTest`

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
