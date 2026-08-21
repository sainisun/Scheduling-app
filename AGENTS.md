# Android Vibe-Coding Agent Rules

## Before Writing Code

1. Read `00-README.md`, relevant feature specification, security requirements and test gate.
2. State the bounded feature slice, data change, API contract change, permission change and tests to add.
3. Do not expand scope silently. If a permission, third-party policy or backend contract is unclear, stop and ask.

## Implementation Rules

1. Use Kotlin, Jetpack Compose, Hilt, Room and Coroutines/Flow as prescribed.
2. Do not put business logic inside Activity, Composable, Receiver, Worker or AccessibilityService. Delegate to domain use case.
3. Do not use a global singleton for mutable feature state.
4. Use UTC for stored execution times plus original IANA timezone for recurrence/display.
5. Every state transition writes an audit-safe event.
6. Treat device/permission conditions as fallible. Never use silent fallback that reports false success.
7. All Android permissions are feature-scoped and user-disclosed. No hidden gestures, no screen unlock, no credential collection.
8. All API calls use repository/interface, typed DTO, timeout, error mapping and request ID.

## Test Rules

Every feature PR adds or updates unit tests, affected UI test and failure-path tests. Any change to scheduling adds tests for cancellation, restart/reboot reconciliation and time/permission edge case. Any change to storage adds migration/backward-compatibility test.

## Forbidden Shortcuts

- Never disable certificate validation, use cleartext HTTP in release, hardcode secret, log message text or bypass backend authorization.
- Never mark schedule completed based only on alarm firing.
- Never use destructive Room migration in release.
- Never declare broad accessibility/overlay behavior without a user-facing feature, disclosure and policy review.

## Completion Format

At end of every task, provide changed files, tests run/results, known limitations, migration/permission implications, and next manual real-device test. A feature is not complete just because it compiles.
