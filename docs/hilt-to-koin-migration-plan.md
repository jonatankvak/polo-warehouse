# Hilt to Koin migration plan

## Goal

Replace Hilt-based dependency injection with Koin while keeping current behavior, module boundaries, and test coverage stable.

## Recommended migration track

1. Phase 1-4: Migrate from Hilt to Koin Annotations (KSP path) on current Kotlin toolchain.
2. Phase 5: Upgrade to Koin compiler plugin (K2 typed startup APIs) in a dedicated follow-up.

Reasoning: this keeps migration risk isolated and avoids coupling DI migration with Kotlin major toolchain changes.

## Current inventory

### Hilt entry points

- `app/src/main/java/com/polo/warehouse/application/WarehouseApplication.kt`
- `app/src/main/java/com/polo/warehouse/activity/MainActivity.kt`
- `app/src/main/java/com/polo/warehouse/activity/MainActivityViewModel.kt`

### Hilt modules

- `core/data/src/main/java/com/polo/data/DataModule.kt`
- `core/firebase/src/main/java/com/polo/firebase/FirebaseModule.kt`

### `hiltViewModel()` usage

- `feature/authentication/src/main/java/com/polo/authentication/view/VerificationScreen.kt`
- `feature/dashboard/src/main/java/com/polo/dashboard/view/DashboardScreen.kt`
- `feature/pallet/src/main/java/com/polo/pallet/create/view/CreatePalletScreen.kt`
- `feature/pallet/src/main/java/com/polo/pallet/read/view/ReadPalletScreen.kt`
- `feature/scanner/src/main/java/com/polo/scanner/verify/view/VerifyPalletScannerScreen.kt`

### Gradle usage

- Hilt plugin and dependencies are present in root + app + core + feature impl modules.

## Phased implementation plan

### Phase 1 - Build setup and dependency wiring

- Add Koin dependencies to `gradle/libs.versions.toml`:
  - `koin-android`
  - `koin-androidx-compose`
  - `koin-annotations`
  - `koin-ksp-compiler`
- Remove Hilt plugin aliases/dependencies from module build files.
- Keep or re-apply `ksp` plugin only where Koin annotation processing is required.

Definition of done:

- No Hilt artifacts left in Gradle scripts.
- Project sync succeeds.

### Phase 2 - Replace DI definitions

- Replace Hilt modules with Koin annotation modules:
  - bindings for repository/data source interfaces
  - providers for `FirebaseAuth` and `FirebaseFirestore`
- Move implementation classes and use cases from `@Inject` constructor style to Koin annotations (`@Single`, `@Factory`).

Definition of done:

- Koin has definitions for all previously Hilt-provided dependencies.
- No `@Module/@InstallIn/@Binds/@Provides` from Dagger/Hilt remain.

### Phase 3 - Application and Android entry points

- Replace `@HiltAndroidApp` app bootstrap with Koin startup in `WarehouseApplication`.
- Remove `@AndroidEntryPoint` from `MainActivity`.
- Migrate `MainActivityViewModel` to Koin ViewModel declaration and retrieval.

Definition of done:

- App starts with Koin initialized.
- Main activity resolves its dependencies via Koin.

### Phase 4 - Compose ViewModel integration

- Replace all `hiltViewModel()` usages with `koinViewModel()`.
- Migrate all feature ViewModels from `@HiltViewModel` to Koin ViewModel annotations.

Definition of done:

- No Hilt imports/annotations/usages remain in production Kotlin sources.

### Phase 5 - Verification and cleanup

- Run:
  - `./gradlew :app:compileDebugKotlin`
  - `./gradlew verifyModuleBoundaries`
  - `./gradlew testDebugUnitTest`
  - `./gradlew :app:assembleDebug`
- Fix any missing definitions or lifecycle/scoping mismatches.

Definition of done:

- Build and checks pass.
- No runtime DI resolution errors in smoke testing.

## Follow-up: compiler plugin migration (long-run target)

After the DI migration is stable, perform a separate upgrade:

- Upgrade Kotlin/toolchain to supported Koin compiler plugin requirements.
- Replace KSP annotations setup with Koin compiler plugin.
- Adopt typed startup APIs (`startKoin<T>()`) and plugin options.

This follow-up should be isolated in its own PR to keep rollback and debugging simple.
