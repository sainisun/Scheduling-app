# Seduligma Monorepo Structure and Initial File Plan

**Status:** Canonical planning document. This specifies the target repository layout, but it does not authorise code outside the currently approved PRD phase.  
**Read with:** PRD v2.1, `architecture-design.md`, `system-design.md`, `implementation-plan.md`, and `agent.md`.

## 1. Direct Answer on Design Depth

| Question | Answer | Evidence and action taken |
|---|---|---|
| Is `architecture-design.md` detailed enough to justify top-level and module boundaries? | **Yes, after this planning update.** | It now names the single-monorepo boundary, current `:app`, planned Android module ownership/extraction triggers, Phase 4 backend/web boundaries, and the folder convention. |
| Was the original architecture document alone detailed enough for literal files? | **Partial.** | It correctly defined components and responsibilities but did not distinguish current `:app` from planned Gradle modules or name backend/web folder ownership. Those sections were added before this file plan. |
| Is `system-design.md` detailed enough for state, entity, DAO, migration, DTO, and route-file planning? | **Yes for the approved Phase 1/Phase 2 plan; sufficient but intentionally gated for Phase 4.** | It now maps canonical Room entities, typed fields, backend entities/DTOs, error/state rules, and module ownership. It still does not authorise backend implementation before Phase 4. |
| Was the original system design alone enough to generate migrations/DTOs without guessing? | **Partial.** | It had table names and core fields but not every implementation type/file mapping. The file-level persistence and contract mapping was added before this plan. |

> **Conclusion:** The folder/file plan below is now justified by the canonical design documents. It is not a generic starter template. Every planned file either supports a named PRD requirement, an approved architecture decision, or a required test/release gate.

## 2. Literal Target Monorepo Tree

The `app/` Android application exists now. `backend/` and `web/` currently contain only their Phase 4 boundary README files. The tree shows the final approved layout; labels mark whether a path exists now, is created incrementally in Phase 1/2, or is reserved until Phase 4.

```text
Scheduling-app/
├── AGENTS.md                                  # Mandatory agent entry rules (exists)
├── README.md                                  # Canonical repository entry point (exists)
├── TODO.md                                    # Repository delivery tracker (exists)
├── settings.gradle.kts                        # Android Gradle module registry (exists)
├── build.gradle.kts                           # Root Android build configuration (exists)
├── gradle.properties                          # Android/Gradle settings (exists)
├── gradle/
│   ├── libs.versions.toml                     # Android dependency catalog (exists)
│   └── wrapper/                               # Gradle wrapper files (exists)
├── app/                                       # Android :app module — active Phase 0–3 work
│   ├── build.gradle.kts                       # App module dependencies/build settings (exists)
│   ├── proguard-rules.pro                     # Release shrinking rules (exists)
│   ├── schemas/                               # Room exported schemas (exists; migration evidence)
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml             # Permissions, receivers, app entry (exists)
│       │   ├── java/com/seduligma/app/
│       │   │   ├── SeduligmaApplication.kt     # Hilt application root (exists)
│       │   │   ├── MainActivity.kt             # Compose app host (exists)
│       │   │   ├── di/                         # Hilt bindings/providers (exists; grows incrementally)
│       │   │   ├── domain/                     # Pure models, policies, repository interfaces (exists)
│       │   │   ├── data/
│       │   │   │   ├── local/                  # Room/SQLCipher entities, DAOs, database (exists)
│       │   │   │   ├── repository/             # Repository implementations (exists)
│       │   │   │   ├── device/                 # Android device-health adapter (exists)
│       │   │   │   └── scheduling/             # Alarm registration adapter (exists)
│       │   │   ├── feature/
│       │   │   │   └── schedule/               # Schedule list/editor/ViewModel (exists)
│       │   │   ├── receiver/                   # Alarm/boot/reconciliation receivers (exists)
│       │   │   └── ui/theme/                   # Compose semantic theme (exists)
│       │   └── res/                            # Android strings, styles, icons, XML config (exists)
│       ├── test/java/com/seduligma/app/        # JVM/unit tests adjacent by feature/domain (exists)
│       └── androidTest/java/com/seduligma/app/ # Instrumentation/Compose/Room device tests (Phase 1 incremental)
├── core/                                      # Planned Android shared Gradle modules; do not create empty modules early
│   ├── common/                                # Result/error, clock, UUID, redaction primitives (post-Phase 1 extraction)
│   ├── domain/                                # Shared models/use cases/interfaces (post-Phase 1 extraction)
│   ├── data/                                  # Shared persistence/profile/data adapters (post-Phase 1 extraction)
│   ├── ui/                                    # Shared Compose semantic status/UI primitives (post-Phase 1 extraction)
│   └── testing/                               # Fakes, fixtures, dispatcher, contract helpers (post-Phase 1 extraction)
├── feature/                                   # Planned Android feature Gradle modules; each created only with approved scope
│   ├── schedules/                             # Schedule editor/list/calendar extraction (post-Phase 1 only if needed)
│   ├── device-health/                         # Permission/readiness screen extraction (post-Phase 1 only if needed)
│   └── automation/                            # Phase 2 disclosure, profiles, execution adapters — policy gate required
├── backend/                                   # Node.js/NestJS service boundary — Phase 4 only
│   ├── README.md                              # Phase gate notice (exists)
│   ├── package.json                           # Phase 4 only
│   ├── tsconfig.json                          # Phase 4 only
│   ├── nest-cli.json                          # Phase 4 only
│   ├── prisma/                                # PostgreSQL schema/migrations — Phase 4 only
│   │   ├── schema.prisma
│   │   └── migrations/
│   ├── src/
│   │   ├── main.ts                            # Nest/Fastify bootstrap
│   │   ├── app.module.ts                      # Root module
│   │   ├── config/                            # Typed env/config validation
│   │   ├── common/                            # Guards, errors, audit, OpenAPI, redaction
│   │   └── modules/
│   │       ├── auth/                          # OIDC validation/session boundary
│   │       ├── devices/                       # Device registration and health metadata
│   │       ├── consents/                      # Immutable consent receipts
│   │       ├── sync/                          # Redacted metadata sync only
│   │       ├── remote-config/                 # Signed ChannelProfile lifecycle
│   │       ├── audit/                         # Administrative audit log
│   │       ├── support/                       # Consent-aware redacted support
│   │       ├── subscriptions/                 # Entitlement projection
│   │       └── billing/                       # Hosted provider checkout/webhook references
│   └── test/                                  # API/module/integration/contract tests
├── web/                                       # Next.js dashboard/admin boundary — Phase 4 only
│   ├── README.md                              # Phase gate notice (exists)
│   ├── package.json                           # Phase 4 only
│   ├── next.config.ts                         # Phase 4 only
│   ├── tsconfig.json                          # Phase 4 only
│   ├── public/                                # Favicon and static configuration assets only
│   └── src/
│       ├── app/
│       │   ├── (public)/sign-in/              # OIDC sign-in handoff
│       │   ├── (app)/devices/                 # Customer device health view
│       │   ├── (app)/schedules/               # Redacted metadata and local-evidence view
│       │   ├── (app)/privacy/                 # Consent/sync/privacy controls
│       │   ├── (app)/billing/                 # Hosted billing entry
│       │   ├── (admin)/support/               # Redacted support operations
│       │   ├── (admin)/channel-profiles/      # Maker/checker profile release
│       │   └── (admin)/audit/                 # Audit log view
│       ├── components/
│       │   ├── ui/                            # Generic primitives
│       │   └── domain/                        # Status badges, notices, tables, dialogs
│       ├── lib/
│       │   ├── api/                           # Generated/typed API client and DTO bindings
│       │   └── auth/                          # OIDC session helpers
│       └── test/                              # Component/unit/e2e test support
├── docs/
│   ├── 12-canonical-android-prd-v2.1.md       # Product authority (exists)
│   ├── 00-README.md … 11-*.md                 # Deprecated pointers only (exists)
│   └── engineering/                           # Canonical engineering suite (exists)
├── .github/
│   └── workflows/
│       ├── android-ci.yml                     # Active Android CI (exists)
│       ├── backend-ci.yml                     # Phase 4 only; do not create early
│       ├── web-ci.yml                         # Phase 4 only; do not create early
│       └── contracts-ci.yml                   # Phase 4 only; cross-component OpenAPI checks
└── scripts/                                   # Approved repository maintenance/verification scripts only
```

## 3. What Files Exist First vs. Incrementally

### 3.1 Android Files That Exist Now

The current Android app is intentionally a single Gradle module. It already contains the foundation files needed for local scheduler work; a coding agent must extend these rather than create parallel replacements.

| Existing file/family | Current purpose | Next approved work |
|---|---|---|
| `MainActivity.kt`, `SeduligmaApplication.kt` | Compose/Hilt application host | Keep stable; no business logic. |
| `domain/model/Schedule.kt` | Schedule state and recurrence domain model | Add only approved canonical fields, then update tests/migrations. |
| `domain/repository/ScheduleRepository.kt` | Local schedule contract | Extend only with P1 requirements. |
| `domain/scheduling/RecurrenceCalculator.kt` | Timezone-aware recurrence | Add DST/edge tests before changing rules. |
| `data/local/ScheduleEntity.kt`, `ScheduleDao.kt`, `SeduligmaDatabase.kt` | SQLCipher Room storage | Add migration tests, backup/reset/deletion evidence. |
| `data/repository/RoomScheduleRepository.kt` | Domain-to-Room mapping | Keep recipient content local/encrypted. |
| `data/scheduling/AndroidScheduleAlarmRegistrar.kt` | Exact alarm registration | Complete permission/WorkManager/reconciliation criteria. |
| `data/device/AndroidDeviceHealthEvaluator.kt` | Readiness summary | Complete P1 OEM/device-health UX tests. |
| `feature/schedule/SchedulePlannerApp.kt`, `ScheduleListViewModel.kt` | Current schedule UI/list/editor | Complete P1 validation/filter/calendar/history/confirmation slices. |
| `receiver/*` | Alarm/boot/reconciliation entry points | Add tests; keep receivers thin. |
| `test/**` | Unit tests/fakes | Expand P1 state, migration, privacy, and error-path coverage. |

### 3.2 Files Created Incrementally in Remaining Phase 1

| Implementation-plan task | First planned files | Do not create yet |
|---|---|---|
| P1-01 editor/list completion | `feature/schedule/ScheduleFilter.kt`, `ScheduleEditorValidator.kt`, corresponding unit/UI tests | Phase 2 adapter/consent files. |
| P1-02 health UX | `feature/devicehealth/DeviceHealthViewModel.kt` only if extraction is justified; otherwise extend current feature package | New Gradle feature module without an approved reason. |
| P1-03 confirmation | `receiver/ManualConfirmationReceiver.kt`, `data/notification/ScheduleNotificationPublisher.kt`, `feature/schedule/ConfirmationActionHandler.kt`, tests | AccessibilityService class. |
| P1-04 evidence history | `feature/schedule/ScheduleHistoryViewModel.kt`, `ScheduleEventDao.kt` extensions, UI tests | Delivery/read status model. |
| P1-05 storage hardening | Room migration file/schema export, `DatabaseMigrationTest.kt`, reset/backup tests | Destructive release migration. |
| P1-06/07 beta readiness | Device test report templates under `docs/engineering/`, support checklist updates | Backend/web code. |

### 3.3 Phase 2 Files — Explicitly Gated

No Phase 2 file is created until the human gate in the PRD/decision register is recorded. After approval, the first files are `feature/automation/AutomationDisclosureScreen.kt`, `AutomationConsentViewModel.kt`, `domain/execution/ExecutionAdapter.kt`, `domain/execution/ChannelProfile.kt`, `data/remoteconfig/ChannelProfileVerifier.kt`, and their unit/device tests. The first adapter remains channel-profile-driven and safe-abort-only; no adapter can be created as a generic command runner.

### 3.4 Phase 4 Backend and Web Files — Explicitly Gated

The `backend/` and `web/` README files are the only allowed files in those folders before Phase 4. Once Phase 4 is approved, create the root config/test files first, then `backend/src/main.ts`, `backend/src/app.module.ts`, `backend/src/config/env.schema.ts`, `web/src/app/layout.tsx`, `web/src/app/(public)/sign-in/page.tsx`, and shared test setup. Feature modules/routes then follow the API contract order: auth/device/consent → sync/support/remote config → subscriptions/billing → customer/admin routes.

## 4. Traceability: Highest-Impact Files and Modules

| File/module | Why it exists | Source requirement/decision |
|---|---|---|
| `app/.../domain/model/Schedule.kt` | Canonical lifecycle, time, recurrence, status semantics | PRD FR-01–FR-05; system-design Sections 1 and 4. |
| `app/.../data/local/ScheduleEntity.kt` and `ScheduleDao.kt` | Encrypted local persistence and event history | PRD Section 8, NFR-01; system-design Section 9. |
| `app/.../data/scheduling/AndroidScheduleAlarmRegistrar.kt` | Exact-alarm registration and no false precise-time claim | PRD FR-06–FR-10. |
| `app/.../data/device/AndroidDeviceHealthEvaluator.kt` | Permission/readiness explanations and reason codes | PRD FR-08–FR-11, NFR-06. |
| `app/.../feature/schedule/SchedulePlannerApp.kt` | Create/edit/pause/cancel/reactivate user workflow | PRD FR-01–FR-04; design-system Android screen requirements. |
| `app/.../receiver/ManualConfirmationReceiver.kt` | User-controlled Beta 1 due-time action | PRD FR-12 Tier A. |
| `feature/automation/ExecutionAdapter.kt` | Uniform Phase 2 channel mechanism | PRD FR-12/15/16; system-design Section 8. |
| `backend/src/modules/remote-config/*` | Signed channel-profile lifecycle and kill switch | PRD Section 8, Phase 2; API contract Section 5. |
| `backend/src/modules/devices/*` and `consents/*` | OIDC-bound device/consent records | PRD Sections 8/11; API contract Sections 2–3. |
| `web/src/app/(app)/schedules/page.tsx` | Customer visibility into redacted local schedule evidence | Design-system Section 6; PRD truthful-status principle. |
| `.github/workflows/android-ci.yml` | Enforces build/lint/tests on active Android code | PRD Section 12; release-and-operations Section 1. |
| Future `backend-ci.yml`, `web-ci.yml`, `contracts-ci.yml` | Component-specific monorepo validation | Release-and-operations Section 1.1; Phase 4 gate. |

## 5. Agent Rule for Structure Changes

An agent must not create a folder merely because it appears in the final tree. Before adding any new module or file, it must identify the approved implementation-plan task, the PRD/architecture source, the acceptance test, and whether the current phase allows it. If the source map does not justify a file, the agent must ask for a human decision rather than scaffold a generic template.
