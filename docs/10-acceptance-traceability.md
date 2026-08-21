# Acceptance Criteria and Traceability

| Requirement | Implementation owner | Required verification | Release gate |
|---|---|---|---|
| User can create one-time schedule | Schedules feature | Unit + Compose UI + physical device | R1 |
| Schedule persists after reboot | Engine/data | Instrumentation reboot/reconcile test | R1 |
| Recurrence respects timezone/DST | Domain engine | Unit table-driven date tests | R1 |
| Exact-alarm denial is transparent | Device health | Permission revoke/deny manual + automated test | R1 |
| User can pause/cancel | Schedules/domain | Race-condition + UI tests | R1 |
| No false sent/completed state | Execution coordinator | State-machine tests | R1 |
| Tokens stay secure | Data/security | Keystore/logging/security review | R1 |
| Sync is idempotent | Data/API | Outbox/contract conflict tests | R1 |
| Accessibility feature is explicitly enabled | Feature/policy | UI disclosure + service-disabled test | R2 gate |
| Device matrix passes | QA | Physical device report | Closed beta exit |
| Support diagnostics are redacted | Support/data | Payload inspection test | Closed beta exit |

Each release PR must add the relevant test evidence link/CI run to its description. Requirements with no verification cannot be marked complete.
