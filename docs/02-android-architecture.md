# Android Architecture

## Approved Stack

| Layer | Technology | Rule |
|---|---|---|
| Language | Kotlin | No Java-first implementation for new features |
| UI | Jetpack Compose + Material 3 | Compose state is lifecycle-safe and testable |
| Architecture | Clean Architecture + MVVM | UI never talks directly to Room or network |
| DI | Hilt | Constructors receive interfaces, not concrete global objects |
| Local DB | Room | Use migrations; never destructive migration in production |
| Scheduling | AlarmManager + WorkManager | Exact alarm only after user-facing permission check |
| HTTP | Retrofit/OkHttp + Kotlin serialization | TLS only, typed DTOs, timeouts and interceptors |
| Secure storage | Android Keystore + EncryptedSharedPreferences | Tokens only; no messaging credentials |
| Crash reporting | Sentry Android or equivalent | Redact message content and identifiers |

## Module Boundaries

```text
app
 ├── core:common       # errors, time, result types, logging contracts
 ├── core:domain       # schedule use cases and repository interfaces
 ├── core:data         # Room, Retrofit, secure storage, repository implementations
 ├── core:ui           # theme, reusable Compose components, navigation primitives
 ├── core:testing      # test fakes, fixtures, test dispatcher
 └── feature:*         # screen/viewmodel/UI per bounded feature
```

Dependencies point inward: `feature → domain ← data`. Feature modules must not query Room directly. Network DTOs must not be used as UI models.

## Core Components

| Component | Responsibility |
|---|---|
| `ScheduleRepository` | Local schedules, recurrence calculation, sync cursor |
| `ScheduleEngine` | Create/cancel/reconcile alarms and calculate next occurrence |
| `PermissionHealthEvaluator` | Produces ready/blocked reasons from Android system state |
| `ExecutionCoordinator` | Bounded preflight and state transition; never marks false success |
| `DeviceRegistrationRepository` | Device key/identity and safe server registration |
| `SyncWorker` | WorkManager-based safe metadata sync and retry |
| `ScheduleAlarmReceiver` | Receives alarm, delegates to coordinator; contains no business logic |

## State Management

Use immutable `UiState`, `StateFlow`, one-off `UiEvent`, and saved state only for navigation/recoverable UI inputs. All state transitions are domain actions that generate an audit event.

## Time Rules

Store scheduled instant in UTC plus original IANA timezone and recurrence expression. Display in current device locale, but never mutate the original timezone silently. DST gap/overlap must require explicit rule documented in tests.
