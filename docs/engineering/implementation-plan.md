# Seduligma Actionable Engineering Plan

Each task maps to PRD v2.1 requirements. Agents complete tasks in order unless an explicit dependency is waived by a human owner.

## Phase 0 — Foundation (partially complete)

| ID | Task | Dependencies | Acceptance criteria |
|---|---|---|---|
| P0-01 | Kotlin/Compose/Hilt project foundation | None | CI builds debug APK and executes unit tests. |
| P0-02 | Encrypted Room/SQLCipher storage | P0-01 | PRD Section 8 local encryption; tests prove no plaintext DB path. |
| P0-03 | Canonical schedule state machine | P0-01 | PRD Section 7.1 transitions and `system-design.md` table tested. |
| P0-04 | Recurrence calculator | P0-03 | One-time/daily/weekly/monthly, DST/timezone, end-of-month test coverage. |
| P0-05 | Exact-alarm readiness and reboot reconciliation | P0-02, P0-03 | FR-06 through FR-10 tested. |

## Phase 1 — Closed Beta Local Scheduler

| ID | Task | Dependencies | PRD acceptance criteria |
|---|---|---|---|
| P1-01 | Complete editor validation and calendar/list filtering | P0-02/03 | FR-01 to FR-05; drafts, edit, pause, cancel, reactivation. |
| P1-02 | Device health UX | P0-05 | FR-06 to FR-11; exact alarm and notification denial/regrant paths. |
| P1-03 | Ask Me Before Sending notification action | P0-05 | FR-12 Beta 1 manual-confirmation path; no credential persistence. |
| P1-04 | Local evidence/event history | P0-03 | NFR-02; every outcome shows known evidence/reason. |
| P1-05 | Migration, backup exclusion, delete/reset tests | P0-02 | PRD Section 8; secure local retention/deletion. |
| P1-06 | Physical device matrix run | P1-01..05 | PRD Section 12: supported Pixel, Samsung, Xiaomi/Redmi/POCO results. |
| P1-07 | Closed beta readiness review | P1-06 | Human accepts supported devices, privacy/terms, support process, issue severity gate. |

**Phase 1 exit gate:** build/lint/unit/UI tests pass; manual confirmation works on supported devices; reboot/revocation/uncertain outcomes are truthful; beta support process exists.

## Phase 2 — Policy-Gated Core Personal Automation

| ID | Task | Human sign-off required | PRD criteria |
|---|---|---|---|
| P2-01 | Record distribution route and Play policy evidence package | **Yes** | FR-14, Section 11. |
| P2-02 | Implement prominent disclosure, affirmative consent, withdrawal, and audit receipt | **Yes before enablement** | FR-14; policy declaration requirements. |
| P2-03 | Produce reviewer video and Data Safety mapping | **Yes** | Section 11. |
| P2-04 | Build verified prefilled-chat adapter | **Yes after P2-01** | FR-12; verify target app/chat/draft. |
| P2-05 | Build narrow final-action adapter | **Yes after P2-02** | FR-15/16; static user-defined script only. |
| P2-06 | Implement failure evidence/no-blind-action tests | No | FR-16/17 and error taxonomy. |
| P2-07 | Signed feature flag and supported-device configuration | **Yes for production release** | Section 8 signed config. |
| P2-08 | Controlled device-matrix beta | **Yes** | Section 12, Phase 2 exit gate. |

**Phase 2 exit gate:** policy/disclosure package approved; every supported-device execution path verifies state or aborts safely; no blind action; logs are redacted; controlled beta evidence is reviewed.

## Phase 3 — Feature Expansion

Add only one vertical slice at a time: templates, media, CSV/lists, groups, Status, or deterministic static rules. Each needs written acceptance criteria, content/privacy analysis, device compatibility tests, and rollback plan before implementation.

## Phase 4 — Commercial Platform

Sequence: OIDC/device registration → consented metadata sync → support/audit foundations → subscription entitlement model → Razorpay checkout/webhooks → Next.js customer dashboard → protected admin panel. Backend must not become a personal-send executor.

## Phase 5 — Tier D Pilot

No code begins until the Tier D threat model, independent security/legal review, distribution review, supported-device/lock matrix, revocation/deletion recovery, and human go/no-go are recorded.
