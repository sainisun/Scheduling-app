# Testing and Quality Gates

## Test Pyramid

| Layer | Tools | Required examples |
|---|---|---|
| Unit | JUnit, Kotest, MockK | Recurrence/DST, state transitions, entitlement checks, permission evaluator |
| Data | Room in-memory tests, MockWebServer | Migrations, outbox sync, conflict rules, token refresh |
| UI | Compose UI tests | Create/edit/cancel schedule, health checklist, inaccessible feature state |
| Integration | Instrumentation on physical/emulated devices | Alarm registration, reboot reconciliation, notification actions |
| Contract | OpenAPI/mock server + Node staging | Auth, retries, errors, sync version conflicts |
| Manual device | Real phones | Battery restrictions, OS/permission differences, vendor-specific behavior |

## Mandatory Test Scenarios

1. Create schedule, restart app, reboot device, and verify it is still correct.
2. Deny/revoke exact alarm after schedule creation; UI changes to actionable blocked/limited state.
3. Timezone change, daylight-saving transition, manual clock change and duplicate alarm prevention.
4. Pause/cancel before scheduled time; no execution attempt occurs.
5. Network loss does not lose local schedule; sync later uses idempotency.
6. `uncertain` is never rendered as `completed`.
7. User logout/device unlink clears local account token safely but does not misrepresent unsynced state.
8. Permission changes do not crash app or trigger repeated unwanted prompts.

## CI Quality Gate

Pull request cannot merge until Gradle build, formatting/lint, unit tests, static analysis, secret scan and affected UI/data tests pass. `main` must build signed internal artifact from clean checkout.

## Beta Exit Gate

| Gate | Evidence |
|---|---|
| Core device families | Pixel, Samsung and Xiaomi/Redmi/POCO test report passed |
| Accuracy | No known false success states |
| Stability | Crash-free beta target and no P0/P1 unresolved issue |
| Permissions | Revoke/deny/re-enable tested on current target Android versions |
| Security | No high severity scan finding; privacy UX reviewed |
| Support | Crash, health and user-report triage workflow exists |
