# Product Requirements Document
## Seduligma — Transparent Android Personal Scheduler

| Field | Value |
|---|---|
| **Product owner** | DIGMA Growth |
| **Document owner** | Sunil, Founder, DIGMA Growth |
| **Version** | 2.1 — Corrected Canonical PRD and Automation Sequencing Update |
| **Date** | August 22, 2026 |
| **Primary platform** | Native Android |
| **Supporting platform** | Node.js API and optional Next.js customer/admin dashboard, introduced after local-beta validation |
| **Status** | Build source of truth |

---

## 1. Product Vision

Seduligma is a **transparent Android personal scheduler** that lets a user prepare a message now and schedule a user-authorized action later. It provides clear device-readiness checks, honest result states, optional manual confirmation, secure local schedule storage, and carefully gated advanced lock handling.

The product is inspired by the user problem addressed by SKEDit: an Android user wants personal messaging workflows scheduled from their own phone. Seduligma is **not** a promise that every third-party application workflow can be automated on every device, that every scheduled action will be delivered, or that an external messaging account can never be restricted. The app must not make “ban-proof,” “undetectable,” “human-like,” or enforcement-evasion claims.

> **Primary product promise:** “See whether your Android device is ready, schedule a personal workflow transparently, and receive an honest status when the scheduled time arrives.”

## 2. Problem Statement

Existing users of personal scheduling apps need a way to plan repetitive communications without repeatedly remembering a future time. They also need a reliable explanation when an Android permission, battery restriction, locked screen, device state, or application UI condition prevents an action.

DIGMA Growth wants to offer this category of product to its clients under its own brand, with control over product roadmap, customer support, subscription plans, diagnostics, and data governance. The initial product must solve the local Android scheduling problem before expanding into agency tooling, dashboards, official Business API workflows, or multi-channel messaging.

## 3. Product Principles and Non-Negotiable Boundaries

| Principle | Requirement |
|---|---|
| **User control** | Every sensitive permission is requested only after an in-context explanation and a user action. The app never silently enables special access. |
| **Truthful status** | `completed` never means delivered or read. It means the defined local verification evidence was observed. When evidence is incomplete, use `uncertain`. |
| **Local first** | The Android device is the execution source of truth. A backend may support account, backup, billing, support, and signed configuration, but it must not be required to trigger a saved local schedule. |
| **Privacy by default** | Raw message/contact content stays local unless the user gives separate, informed consent for a defined sync or support use. |
| **Safe failure** | If the phone is locked without a supported, enabled user choice; a required permission is missing; or expected UI is absent, stop safely. Do not guess, blindly tap coordinates, or claim success. |
| **No credential collection by default** | The default Beta 1 path stores no device PIN, pattern, password, OTP, biometric data, or third-party messaging credential. |
| **No autonomous AI sending** | AI may draft or improve content. It cannot independently decide to send a message. A deterministic, user-defined static rule is a separate policy-gated capability. |

## 4. Users, Jobs, and Initial Use Cases

| User | Job to be done | Beta 1 outcome |
|---|---|---|
| Individual Android user | “Remind me and help me complete a prepared message at a later time.” | Creates a text-only, single-recipient local schedule and receives an honest due-time action/status. |
| Small-business owner | “Plan recurring personal follow-ups from my business phone.” | Uses recurring local schedules and device-health guidance on a supported Android device. |
| DIGMA support operator | “Explain why a scheduled item did not run without viewing customer content.” | Uses consented, minimised device/readiness/status diagnostics. |

## 5. Closed-Beta Scope

### 5.1 Included in Beta 1

Beta 1 is deliberately narrow. It supports **one Android device, text-only content, a single recipient, local storage, and a supported-device list**. It supports one-time, daily, weekly, and monthly schedules; timezone-aware date/time selection; create, edit, pause, cancel, and explicit reactivation; exact-alarm readiness; notification readiness; reboot reconciliation; local calendar/list views; and honest state logging.

### 5.2 Excluded from Beta 1

The following are separate release gates: WhatsApp Groups, Broadcast Lists, Status posting, CSV imports, send lists, media attachments, templates, merge fields, automatic replies, AI features, multi-channel support, web dashboard, billing, user account sync, super-admin panel, remote selector configuration, and PIN/password Auto-Unlock.

| Capability | Planned release gate | Why it is excluded from Beta 1 |
|---|---|---|
| Personal UI automation through AccessibilityService | **Phase 2 core-product milestone after Beta 1** | Requires distribution decision, declaration, disclosure, supported-device testing, and separate policy review before enabling it.[1] |
| Groups, Status, CSV, media, templates | Feature release after local reliability beta | Each increases UI variability and support burden. |
| Static auto-reply rules | Later policy-gated release | Requires carefully scoped inbound-data, consent, and deterministic-action design. |
| AI drafting | Later release | Requires quota, data-handling, and user-consent requirements. |
| AI-sent reply | Out of scope | Autonomous initiation/planning/execution is prohibited for non-accessibility-tool apps using AccessibilityService.[1] |
| Dashboard, billing, backend account system | Commercial-release phase | Must not block proof that the local scheduler works. |

## 6. Lock-Handling Model

The app must show lock handling as a user choice with transparent limitations. The default user flow must work without device credential storage.

| Tier | Name | Beta decision | Required behavior |
|---|---|---|---|
| **A** | Ask Me Before Sending | **Required and default in Beta 1** | At the scheduled time, show a notification. User manually unlocks the device and taps the action. No device credential is stored. This matches the manual-confirmation model documented by SKEDit.[2] |
| **B** | User-managed Smart Lock / Extended Lock | Supported as device state | Explain that the user may configure Android’s own trusted-device/place setting. The app checks readiness only; it does not configure Smart Lock or collect its secrets. |
| **C** | User-managed no-lock / swipe | Supported with warning | Show the security trade-off, require acknowledgement, and do not alter the user’s lock settings. |
| **D** | PIN/password Auto-Unlock | Feature-flagged advanced pilot; **excluded from Beta 1** | Separate consent, separate threat model, compatibility matrix, distribution review, recovery path, and independent security/legal approval are mandatory before implementation or release. |

### 6.1 Advanced PIN/Password Gate

Tier D may not be enabled unless all gates pass: a written credential-lifecycle threat model; no transmission/logging/backup/export; supported-device and Android-version evidence; fail-closed behavior for unsupported locks, patterns, biometric locks, managed devices, or cloned profiles; Play Console distribution review; clear in-app disclosure; abuse-prevention controls; user revocation and deletion flow; and independent Android security/legal review.

Android Device Admin is a legacy management API primarily documented for enterprise/security use. Password-management and reset-related policies have been deprecated or removed in modern Android contexts; it is not a general-purpose, durable consumer unlock contract.[3] [4] The product therefore cannot rely on it as its core Beta 1 path.

## 7. Functional Requirements

### 7.1 Schedule Lifecycle and Local Planning

The canonical schedule state machine is:

`draft → needs_permission | waiting | blocked → attempting → completed | failed | uncertain`, with `paused` and `cancelled` available from non-terminal states.

| ID | Requirement | Acceptance criterion |
|---|---|---|
| FR-01 | Create a text-only, single-recipient local schedule. | A user enters recipient reference, title, content preview, date, time, timezone, and recurrence; the schedule persists encrypted locally. |
| FR-02 | Support one-time, daily, weekly, and monthly recurrence. | Next occurrence is calculated correctly in the selected timezone, including end-of-month behavior. |
| FR-03 | Support edit, pause, cancel, and explicit reactivation. | Editing preserves the schedule ID, cancels a currently registered alarm, returns to `draft`, and requires explicit reactivation. |
| FR-04 | Show local list/calendar views. | User can see upcoming, paused, failed, uncertain, and completed local schedules. |
| FR-05 | Use one local execution lane. | The app does not attempt two UI-dependent jobs concurrently. Pacing is disclosed as a fair-use/product-reliability control, not as a safety or enforcement guarantee. |

### 7.2 Device Readiness, Exact Alarms, and Recovery

| ID | Requirement | Acceptance criterion |
|---|---|---|
| FR-06 | Check exact-alarm access before activation and before an execution attempt. | If access is missing, state becomes `needs_permission`; the app explains why exact timing is needed and links only through an explicit user tap to Android settings. |
| FR-07 | Provide exact-alarm fallback. | If exact-alarm access is denied or revoked, offer inexact/manual-confirmation fallback and never silently claim precise scheduling. Android special access can be denied by default for many modern installs.[5] [6] |
| FR-08 | Check notification readiness. | If notification access is unavailable, explain impact and offer an explicit settings shortcut. |
| FR-09 | Reconcile after reboot/app update. | Future eligible schedules are restored; overdue items become `uncertain`, never `completed`. |
| FR-10 | Re-check readiness at resume, boot, relevant special-access changes, and immediately before an attempt. | Revoked settings result in `needs_permission` or `blocked` with a specific reason code. |
| FR-11 | Provide OEM/device-health guidance. | The app presents supported-device documentation and user-initiated battery/autostart guidance. It does not claim that every OEM will preserve background behavior. |

### 7.3 Manual Confirmation and Advanced Personal Automation

| ID | Requirement | Acceptance criterion |
|---|---|---|
| FR-12 | Implement the manual-confirmation default and the policy-gated prefilled-chat execution path. | **Beta 1:** At due time, the app posts an action notification; the user unlocks manually and taps the action. **Phase 2:** when an automation attempt is enabled and the policy gate has passed, first open the recipient chat through WhatsApp’s documented click-to-chat/pre-filled-message route (for example, a `wa.me` URL or equivalent resolved Android intent) so the target chat and draft text are prefilled. WhatsApp documents that click-to-chat opens the chat and a pre-filled message appears in the text field; it does **not** send the message.[8] The implementation must verify that the expected target app/chat/draft state is actually present before any final action. |
| FR-13 | Display locked-device state honestly. | Without a supported user-selected lock tier, a locked phone results in `blocked`/manual confirmation—not a silent bypass. |
| FR-14 | Accessibility capability may only be implemented and enabled after policy gate approval. | Before any real-user automation: the distribution path is selected; Play declaration is complete where relevant; prominent in-app disclosure and affirmative consent are implemented; data-safety statement is accurate; a reviewer video is prepared; the supported-device matrix is approved; and Android/Google Play compliance review has signed off.[1] |
| FR-15 | Use Accessibility only for the smallest necessary final interaction after verified prefilled-chat state. | Prefilling reduces UI traversal, contact-search, and text-entry fragility. It **does not remove AccessibilityService policy obligations** when the service still inspects screen content or performs the final action on the user’s behalf. The app must retain declaration, prominent disclosure, affirmative consent, static user-defined script, and data-use requirements.[1] |
| FR-16 | Abort safely on unexpected target UI state. | Missing expected UI evidence results in `failed` or `uncertain`. The product must not use blind coordinate tapping as a general fallback. |
| FR-17 | Define local verification evidence. | The app distinguishes `attempting`, `UI-verified`, `completed`, `failed`, and `uncertain`. It does not call local UI completion “delivered” or “read.” |

### 7.4 Content, AI, and Auto-Reply — Later Gates

| ID | Requirement | Release gate |
|---|---|---|
| FR-18 | Add reusable templates and user-defined personalization tokens. | After Beta 1 schedule reliability criteria pass. |
| FR-19 | Add AI draft/enhance assistance. | User chooses data submitted to an AI provider; no automatic send. |
| FR-20 | Add deterministic, static auto-reply rules only after policy review. | Rules are visible, user-defined, scoped, pausable, and never AI-autonomous. |
| FR-21 | Add media, lists, CSV, groups, and Status only with separate acceptance criteria. | Each feature requires a supported-device/UI compatibility test matrix. |

## 8. Privacy, Security, and Data Governance

| Area | Mandatory requirement |
|---|---|
| Local storage | Schedule data is encrypted at rest with SQLCipher/Room and a Keystore-backed passphrase. |
| Sensitive secrets | Never collect or store OTPs, biometrics, third-party app credentials, or lock credentials in Beta 1. Tier D has separate controls. |
| Support diagnostics | Default diagnostics exclude raw message and contact content. Any richer diagnostic upload requires separate, granular user consent. |
| Backend sync | No raw content sync by default. Sync design requires purpose limitation, encryption in transit, retention period, deletion, export, and user control. |
| Logging | Minimise data, redact identifiers/content, define retention, and protect access by role. |
| Remote configuration | Any UI selector/config update must be signed, versioned, expiry-controlled, staged, reversible, auditable, and kill-switchable. The mobile app rejects unsigned, expired, or incompatible configuration. |
| Security events | Define response for selector breakage, unexpected action, crash, consent withdrawal, remote-config compromise, and potential data incident. |

## 9. Non-Functional Requirements

| ID | Requirement | Acceptance criterion |
|---|---|---|
| NFR-01 | Reliability | Supported-device tests demonstrate schedule persistence, reboot reconciliation, state correctness, and permission-revocation handling. |
| NFR-02 | Honest outcomes | No path marks a schedule `completed` without documented local verification evidence. |
| NFR-03 | Performance | Local planner and readiness UI remain responsive; no performance target is framed as a way to evade external enforcement. |
| NFR-04 | Accessibility/policy | Any AccessibilityService use follows the Play declaration, disclosure, consent, and data-use requirements before public distribution.[1] |
| NFR-05 | Maintainability | Automation-related configuration is centrally tested, signed, staged, reversible, and versioned. |
| NFR-06 | Supportability | Every failure/uncertain state has a user-readable reason and consent-respecting diagnostic path. |

## 10. Technical Architecture

### 10.1 Android Client

The Android application uses **Kotlin**, **Jetpack Compose**, **Hilt**, encrypted **Room/SQLCipher**, **AlarmManager** for exact user-visible scheduling where granted, and **WorkManager** for non-exact sync, cleanup, reconciliation, and diagnostics. WorkManager is not used as a precise trigger mechanism.[5]

The Android module boundaries are: local domain model and state machine; encrypted persistence; recurrence calculator; alarm-registration abstraction; device-health evaluator; schedule editor/list/calendar UI; notification and reboot receivers; and **Phase 2 policy-gated execution adapters**. The first adapter must prefer a verified WhatsApp click-to-chat/pre-filled-message route to minimise UI traversal, then use Accessibility only for the smallest necessary final action after required disclosures and consent.

### 10.2 Backend and Web Platform — Later Commercial Release

The supporting platform uses a **Next.js/TypeScript** customer/admin frontend and a separate **Node.js/TypeScript API** (recommended NestJS + Fastify) with PostgreSQL, Redis, object storage, OIDC authentication, and Razorpay integration for an India-first commercial release.

The backend owns accounts, subscriptions, device registration, consented sync, support diagnostics, signed remote configuration, abuse controls, and administrative audit logs. It does not directly dispatch personal messages from a user’s phone or require an always-on connection to trigger an already-saved local schedule.

## 11. Distribution, Compliance, and Support Decision

Before any AccessibilityService capability is developed, the team must choose and document one path: public Play Store, Play closed test, or private distribution. The decision pack must contain disclosure screens, permission rationale, Accessibility declaration where applicable, Data Safety disclosure, privacy policy, Terms, supported-device list, age/region limitations, beta support process, crash/incident response process, and test evidence.[1]

| Decision | Owner | Required before |
|---|---|---|
| Distribution route | Founder + product/legal | Accessibility implementation |
| Supported Android/OEM list | Engineering + QA | Closed beta invitation |
| Tier D Auto-Unlock go/no-go | Security/legal/product | Any credential-handling code |
| Backend/sync scope | Product + engineering | Commercial launch build |
| Pricing and plan limits | Founder + finance | Razorpay integration |

## 12. Quality Gates and Testing

| Test layer | Required coverage |
|---|---|
| Unit tests | State transitions, recurrence, failure classification, encryption abstraction, remote-config verification. |
| Repository/migration tests | Encrypted persistence, schema migrations, deletion, restoration, and no-content diagnostic defaults. |
| UI/instrumentation tests | Schedule editor, locked device state, permission-required state, notification actions, edit/pause/cancel flow. |
| Android system tests | Exact-alarm denied/revoked, notification denied, reboot, app update, offline mode, timezone/DST, Doze, battery restrictions. |
| Device matrix | Supported Pixel/stock Android, Samsung One UI, and Xiaomi/Redmi/POCO device families; declared Android API range. |
| Security review | Keystore lifecycle, backup exclusion, log redaction, consent withdrawal, remote-config signing, and Tier D gate if proposed. |
| Closed beta | Ten to twenty invited users, no public automation claims, support escalation, and explicit feedback/incident review. |

## 13. Phased Roadmap

| Phase | Scope | Exit gate |
|---|---|---|
| **Phase 0 — Current foundation** | Kotlin/Compose, encrypted local store, schedule lifecycle, recurrence, exact-alarm readiness, reboot recovery, CI. | Build, tests, lint, and debug APK pass. |
| **Phase 1 — Closed Beta 1: local scheduler** | Text-only, single-recipient, local scheduling; Ask Me Before Sending; device-health UX; supported-device matrix. | Real-device beta meets truthful-state, persistence, recovery, and manual-confirmation criteria. |
| **Phase 2 — Core product: policy-gated personal automation** | Distribution decision, Accessibility declaration/disclosure/consent, reviewer video, supported-device matrix, signed feature flag, verified prefilled-chat intent, narrow final-action Accessibility flow, device testing, and support diagnostics. | The automation feature passes policy, security, UI-state verification, no-blind-action, device-matrix, and controlled-beta gates. Commercial backend is **not** a prerequisite. |
| **Phase 3 — Selected feature expansion** | Templates, calendar refinement, media, CSV, groups, Status, or deterministic rules—one separately tested slice at a time. | Each feature’s acceptance criteria and supported-device/UI compatibility evidence pass. |
| **Phase 4 — Commercial platform** | OIDC account, consented sync, Razorpay, customer dashboard, admin controls, subscription plans, and support operations. | Security, privacy, billing, and operational readiness pass. |
| **Phase 5 — Tier D PIN/password Auto-Unlock pilot** | Feature-flagged advanced lock-handling pilot only if still justified after Phase 2 evidence. | Full Tier D threat-model, compatibility, distribution, recovery, and independent security/legal gates pass. |

## 14. Success Metrics

| Metric | Definition |
|---|---|
| Persistence reliability | At least 99% of saved schedules reload after restart/reboot in supported-device tests. |
| Readiness clarity | Every blocked/revoked condition is surfaced with a user-readable reason and remediation action. |
| Outcome integrity | Zero test cases label a schedule `completed` without its defined evidence. |
| Beta stability | Crash-free and failure-triage metrics are set after baseline device test data exists; do not invent targets before measurement. |
| Account safety | No guarantee or numerical promise. External messaging platforms retain their own enforcement rules and outcomes. |

## 15. Remaining Pre-Engineering Decisions

The following are intentionally unresolved, not hidden assumptions:

1. Select the Beta 1 supported Android versions and exact device/OEM list.
2. Choose the distribution route before Phase 2 Accessibility implementation and enablement; prepare the declaration, prominent-disclosure, consent, reviewer-video, and Data Safety evidence package.
3. Define whether the product remains consumer-only or will later include agency-managed devices.
4. Confirm the commercial release region, legal entity, privacy jurisdiction, and Razorpay onboarding owner.
5. Confirm the Phase 2 scope and supported-device matrix for the prefilled-chat plus narrow final-action workflow; verify behavior with WhatsApp versions under the applicable device test policy.
6. Decide if/when Tier D is worth the security, support, and distribution risk; neither Beta 1 nor Phase 2 depends on it.
7. Define feature-specific acceptance criteria before adding groups, Status, bulk flows, auto-replies, media, or multi-channel functionality.

## References

[1]: https://support.google.com/googleplay/android-developer/answer/10964491?hl=en-GB "Google Play — Use of the AccessibilityService API"  
[2]: https://skedit.zendesk.com/hc/en-us/articles/4407450851986-How-to-use-Ask-me-before-sending "SKEDit Help Center — Ask Me Before Sending"  
[3]: https://developer.android.com/work/device-admin "Android Developers — Device administration overview"  
[4]: https://developers.google.com/android/work/device-admin-deprecation "Android Enterprise — Device admin deprecation"  
[5]: https://developer.android.com/develop/background-work/services/alarms "Android Developers — Schedule alarms"  
[6]: https://developer.android.com/about/versions/14/changes/schedule-exact-alarms "Android Developers — Android 14 exact alarms"  
[7]: https://skedit.zendesk.com/hc/en-us/articles/11171372655772-Managing-Screen-Lock "SKEDit Help Center — Managing Screen-Lock"  
[8]: https://faq.whatsapp.com/5913398998672934 "WhatsApp Help Center — How to use click to chat"
