# Seduligma Documentation Summary for Sign-off

**Audience:** Sunil, Founder, DIGMA Growth  
**Purpose:** A plain-language review of the documents that will govern AI coding agents before further product development.  
**Scope:** PRD v2.1 plus the complete `docs/engineering/` suite.

## 1. Executive Summary

The documentation suite is designed to make sure a coding agent builds **the product that DIGMA approved**, not a generic messaging app and not an unsafe shortcut. It establishes that the Android phone is the place where a personal schedule is actually managed and executed; the later Node.js backend helps with accounts, subscriptions, support, dashboards, and safe synchronization but does not remotely send personal messages or control a device.

The documents preserve the approved product strategy: first prove the local scheduler in a closed beta, then make the **policy-gated personal automation capability** the next core milestone, before spending time on billing or dashboards. The agent is explicitly prevented from adding secret capture, autonomous AI sending, blind screen tapping, false delivery/read results, ban-evasion claims, or early AccessibilityService execution.

## 2. Per-Document Summary

| Document | What it covers in plain language | Important decisions made here, not silently assumed | Risks, uncertainty, or input still needed |
|---|---|---|---|
| `12-canonical-android-prd-v2.1.md` | The approved product definition: what Seduligma is, what Beta 1 includes, what it excludes, how users see honest schedule states, and the Phase 1–5 roadmap. | The Android phone is the execution source of truth; Phase 2 automation comes before the commercial backend; Tier D lock handling is not Beta 1/2. | Founder must choose distribution route, supported devices, commercial region/legal setup, and whether Tier D is ever worth pursuing. |
| `architecture-design.md` | A map of the Android app, future backend, database, storage, customer dashboard, admin panel, and their connections. It explains who owns each responsibility. | Kotlin/Compose/Hilt; Node.js TypeScript with NestJS/Fastify; PostgreSQL; Redis; REST; object storage. The backend cannot send personal messages or issue arbitrary device commands. | These technologies are now the intended stack unless a human approves a documented change. Backend is a later Phase 4 dependency, not an automation prerequisite. |
| `system-design.md` | The detailed behaviour rules: schedule states, allowed state changes, error codes, local database structure, backend tables, and how only one local action can run at a time. | All stored/API timestamps use UTC with a separate timezone ID; schedules use UUIDs; a separate execution lease prevents two UI-dependent jobs running together; REST is the API style. | Error taxonomy is complete for the current scope, but any new product feature must add its error codes before code is written. |
| `data-model.md` | A simple shared dictionary of the main business objects—Schedule, Device, Consent, Event, Remote Config, Subscription—so Android and backend teams call the same thing by the same name. | Raw schedule content and recipient references remain local by default; only redacted lifecycle information is syncable by default. | Future rich sync, attachments, groups, and commercial entities need feature-specific privacy decisions before implementation. |
| `api-contract.md` | The future backend’s allowed endpoints, authentication rules, error format, and what APIs are prohibited. | Backend uses versioned REST JSON and OpenAPI. There must never be a personal-message send endpoint, remote unlock endpoint, or arbitrary phone-command endpoint. | These APIs are Phase 4 except signed remote configuration, which is relevant to Phase 2 but only after its human policy gate. |
| `agent.md` | The “constitution” for coding agents. It tells them what phase they may work on, what they must never do, where code belongs, what tests are required, and when to stop and ask. | Agents must use the defined Android folder boundaries, canonical names, commit format, and Definition of Done. An ambiguous requirement is a stop-and-escalate event, not an invitation to guess. | A human must keep the phase status current. If the business approves Phase 2 later, the authorisation must be recorded before an agent starts it. |
| `implementation-plan.md` | The PRD roadmap converted into actual engineering tasks, dependencies, acceptance checks, and phase exit gates. | Beta 1 work is sequenced from local persistence and editor finishing through device tests and a closed-beta review; commercial backend comes later. | The first human checkpoint is the Phase 1 beta readiness review. Phase 2 cannot begin without distribution and disclosure decisions. |
| `security-and-testing.md` | The test plan, data handling rules, deferred Tier D threat model, incident response steps, and Play/distribution checklist. | Sensitive content is local-only by default; hosted payment details are never collected by Seduligma; Phase 2 failures must safely stop and preserve truthful evidence. | The full Tier D threat model exists for future use, but it must not be treated as authorisation to implement Tier D. Distribution compliance evidence still needs human ownership. |
| `design-system.md` | The visual rules for Android and web: colours, spacing, status labels, accessibility of Seduligma’s own UI, and key screen states. | `uncertain`, `blocked`, `failed`, and `completed` have separate visual meanings. The web dashboard must never present a remote “Send now” button for personal workflows. | Brand/logo choices and final design polish can be made later; honest status labels are mandatory now. |
| `onboarding.md` | The short starting point for a new coding-agent session. It tells an agent what to read first, what is already built, and what it must not touch. | Every agent session must end with changed files, tests, CI result, limitations, and next blocking decision. | It must be updated whenever a phase changes or a meaningful implementation milestone is completed. |
| `decisions-and-open-questions.md` | One place to see decisions already made and the questions that only humans may answer. | Records the local-first model, Phase 2 sequencing, prefilled-chat-first approach, selected stack, and no remote-send rule. | This is the main founder approval list; unresolved decisions must stay unresolved until answered, not be guessed by agents. |
| `README.md` | The index and mandatory reading order for the engineering suite. | PRD overrides all supporting documents; the agent constitution governs agent behaviour. | Keep links and current phase status accurate as the project grows. |

## 3. Full List of Engineering Decisions Made Outside the PRD

The following decisions were made to remove engineering ambiguity. Each is recorded in the relevant source document and is not a hidden assumption.

| Decision | Why it was made | Document |
|---|---|---|
| Use Kotlin, Jetpack Compose, Hilt, Room/SQLCipher, AlarmManager, and WorkManager on Android. | They fit a native Android, offline-first, encrypted scheduler. | Architecture design |
| Use Node.js TypeScript with NestJS and Fastify for the future API. | Provides modular server structure, validation, and OpenAPI support. | Architecture design |
| Use PostgreSQL rather than MongoDB. | Schedules, subscriptions, consent, audits, and access roles need strong relationships and transactions. | Architecture design |
| Use Redis/BullMQ only for backend jobs, not personal message dispatch. | Preserves local-first execution and prevents the backend becoming a hidden sender. | Architecture design |
| Use REST JSON plus OpenAPI rather than GraphQL. | Mobile/offline clients benefit from explicit, versioned endpoints and common tooling. | System design/API contract |
| Store timestamps in UTC with an IANA timezone ID. | Keeps schedule math stable across timezones/DST while preserving the user’s intended local time. | System design |
| Use UUIDs for local/backend identities. | Devices can create schedules offline without ID conflicts. | System design |
| Use one local execution lease. | Prevents two UI-dependent jobs from running at the same time. | System design |
| Sync only redacted schedule metadata by default. | Keeps raw message/contact content local unless a user opts in for a specific purpose. | System design/data model |
| Do not provide backend endpoints for send, device unlock, or arbitrary commands. | Prevents architectural drift away from the approved local-first model. | API contract |
| Use signed, time-limited, audience-limited remote configuration with approval and rollback. | Allows safe configuration changes without handing an agent/admin unrestricted control of devices. | Architecture/system design |
| Treat `uncertain` as a permanent honest outcome until the user reviews it. | Avoids falsely calling a missed/reboot-interrupted schedule a success. | System design/data model |
| Use semantic status colours and text together. | Users must clearly understand that a local completion is not a delivery/read receipt. | Design system |
| Require each coding agent to stop when requirements are unclear or phase-gated. | Prevents “vibe coding” from silently inventing features or bypassing guardrails. | Agent rules |
| Use one channel-pluggable Accessibility adapter for WhatsApp, Telegram, Messenger, SMS, and Email. | Every planned personal channel follows the same local UI-automation, profile, safety, and evidence model; there is no native-SMS or direct-email exception. | System design/data model/API contract |
| Keep Telegram Bot API as a non-default future alternative. | It needs a separate founder-approved product, consent, data-flow, and API-contract decision; it is never a silent fallback. | System design/API contract/implementation plan |

## 4. Cross-Document Consistency Check

**Result: Passed.** I reviewed the canonical PRD and all engineering-suite files for the approved safety rules, state language, and phase ordering. No source-document correction was required during this summary review.

| Required check | Result | Evidence across documents |
|---|---|---|
| Canonical state machine stays the same | **Consistent** | PRD, system design, data model, agent Definition of Done, implementation plan, and test plan all use `draft`, `needs_permission`, `waiting`, `blocked`, `attempting`, `completed`, `failed`, `uncertain`, `paused`, `cancelled`. |
| Tier A–D lock model is preserved | **Consistent** | PRD remains authority; engineering documents keep Tier A as Beta 1 default, B/C user-managed, and Tier D deferred/gated. |
| No autonomous AI send | **Consistent** | Agent rules prohibit it; architecture, PRD, testing, and implementation plan retain AI as drafting only. |
| No ban-evasion or false safety claims | **Consistent** | No document introduces ban-proof, undetectable, human-mimicry, or “safe-rate” claims. Fair-use wording is only a reliability/single-lane control. |
| Phase 2—not Phase 5—automation sequencing | **Consistent** | PRD, architecture, implementation plan, agent rules, onboarding, and decisions register all place policy-gated automation immediately after Phase 1 local beta and before Phase 4 commercial platform work. |
| No blind UI interaction | **Consistent** | PRD, system error rules, agent rules, test plan, and Phase 2 plan require aborting as `failed`/`uncertain` when expected state cannot be verified. |
| Backend remains non-sender | **Consistent** | Architecture and API contract prohibit send/unlock/arbitrary device-command endpoints. |
| Uniform multi-channel execution | **Consistent** | WhatsApp, Telegram, Messenger, SMS, and Email all use the same Phase 2 policy-gated local Accessibility adapter model and can report only local UI-verified evidence. |

No inconsistency was found, so no source document was changed as part of this review.

## 5. What a Coding Agent Will Build First

When a coding agent opens `onboarding.md`, it first reads the PRD, the agent constitution, architecture, system design, data model, security/testing document, and implementation plan. It then checks the repository TODO and the latest GitHub Actions run. The agent is authorised only to work inside the **Phase 1 local scheduler** scope.

The first remaining practical task is to **audit and complete the schedule editor/list experience**: ensure a user can create, validate, edit, pause, cancel, reactivate, and filter local schedules with correct timezone/recurrence behaviour. The current repository already has part of this foundation, so the agent must inspect what exists rather than duplicate it.

That task is done only when unit tests cover valid and invalid state transitions, UI/instrumentation tests cover the editor’s normal and error states, encrypted persistence is verified, and CI builds/lints/tests the app successfully. A screen that “looks right” is not enough.

| Milestone from first task to working Beta 1 | Plain-language outcome |
|---|---|
| 1. Finish editor/list behaviour | Users reliably create and manage local schedules. |
| 2. Finish device-health UX | Users understand missing exact-alarm/notification access and can act on it. |
| 3. Add Ask Me Before Sending | At the scheduled time, user receives a truthful manual-confirmation notification. |
| 4. Add clear event history and outcome reasons | Users can understand waiting, blocked, failed, and uncertain outcomes. |
| 5. Complete encryption/migration/reset tests | Local schedule data remains protected and survives expected app changes. |
| 6. Test real supported phones | Validate the product on Pixel/stock Android, Samsung, and Xiaomi/Redmi/POCO devices. |
| 7. Run closed-beta readiness review | Confirm privacy, support process, stability, and supported-device list before inviting users. |

There are **seven Phase 1 milestones** from the first remaining editor/list task to a working, reviewable Beta 1. The Phase 2 automation work does not start until the human approvals below are completed.

## 6. Consolidated Open Questions and Human Approvals

This is the complete list an agent must not answer alone.

| Priority | Decision or question | Needed before | Recommended owner |
|---|---|---|---|
| P0 | Which Android versions and exact device/OEM models are officially supported for Beta 1? | Real-device test plan and closed beta | Engineering + QA + Founder |
| P0 | Which distribution route will be used for Phase 2: public Play Store, Play closed test, or private distribution? | Any Accessibility automation implementation/enablement | Founder + product + legal |
| P0 | Who owns and approves the Phase 2 Accessibility declaration, prominent disclosure, affirmative consent, Data Safety answers, and reviewer video? | Phase 2 gate | Founder + product + legal + engineering |
| P0 | What is the explicit Phase 2 go/no-go decision after policy/security/device evidence is available? | Real-user automation release | Founder + security + product |
| P1 | What exact supported-device matrix applies to the prefilled-chat/narrow final-action workflow? | Phase 2 controlled beta | Engineering + QA |
| P1 | What privacy jurisdiction, legal entity, commercial region, and Razorpay account owner will support Phase 4? | Billing/backend implementation | Founder + finance/legal |
| P1 | Should DIGMA eventually support consumer-only devices, agency-managed devices, or both? | Commercial platform design | Founder + product |
| P2 | Is Tier D PIN/password Auto-Unlock ever commercially justified after its cost, security, and support risk are measured? | Any Tier D code | Founder + independent security/legal |
| P2 | Which Phase 3 feature is next after automation: templates, media, CSV, groups, Status, or static rules? | Each Phase 3 vertical slice | Founder + product |
| P2 | If richer sync/support diagnostics are desired, what exact content may be synced, for what purpose, and with what consent/retention rule? | Backend sync expansion | Product + legal/security |

## 7. Sign-off Recommendation

Sunil can approve the documentation suite for **Phase 1 agent-driven work now**. The suite is complete enough for an agent to continue local scheduler hardening safely and consistently.

Before approving Phase 2 work, Sunil should resolve the four P0 items above. The recommended immediate founder action is to nominate the supported-device list owner and choose whether Phase 2 will begin as a Play closed test or another lawful/private distribution route. No additional technical document is needed before Phase 1 coding continues.
