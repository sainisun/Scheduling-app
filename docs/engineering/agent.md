# Seduligma AI Coding Agent Constitution

**Read this before changing code.** This file is binding for AI coding agents. PRD v2.1 is the product authority; `architecture-design.md`, `system-design.md`, `data-model.md`, `api-contract.md`, and `security-and-testing.md` make its implementation rules executable.

## 1. Currently Authorised Scope

The current authorised work is **Phase 1 local scheduler hardening** and supporting documentation/test work. Existing Kotlin foundation work may continue only within the Phase 1 boundary. Phase 2 Accessibility-based automation is not authorised until a human records the distribution route, Play/declaration/disclosure package, supported-device matrix, and Phase 2 go/no-go in the decision register.

Do not implement Phase 3 feature expansion, Phase 4 commercial backend/billing/dashboard, or Phase 5 Tier D credential handling merely because interfaces or documents mention them.

## 2. Never Do This Without Explicit Human Approval

| Prohibited shortcut | Required response |
|---|---|
| Capture, infer, log, sync, back up, or replay a device PIN/password/pattern/OTP/biometric | Stop. Flag the blocked Tier D requirement. |
| Add autonomous AI-generated sending or an AI action decision | Stop. AI remains draft/enhance only. |
| Add blind coordinate tapping when target UI evidence is absent | Stop the workflow; return `failed` or `uncertain`. |
| Claim/represent `completed` as delivery or read confirmation | Use only local evidence terminology. |
| Add ban-evasion, human-mimicry, randomized evasion, or “safe rate” claims | Remove/decline; use fair-use single-lane reliability behaviour only. |
| Make backend send a personal message, remotely unlock a device, or issue arbitrary accessibility commands | Stop; it violates local-first architecture. |
| Remove, hide, or weaken disclosure/consent to simplify UX | Stop and escalate. |
| Use AccessibilityService before Phase 2 gate evidence is recorded | Stop and escalate. |
| Replace Kotlin/Compose, NestJS/Fastify, PostgreSQL, Redis, SQLCipher, or defined conventions without a documented architecture decision | Stop and request human approval. |

## 3. Required Repository Structure

```text
app/src/main/java/com/seduligma/app/
  domain/          # pure models, state transitions, interfaces
  data/            # Room/SQLCipher, Android adapters, repositories
  feature/         # Compose screens and ViewModels by feature
  receiver/        # alarm, boot, notification entry points
  di/              # Hilt modules only
  security/        # local crypto/consent boundaries
docs/engineering/  # canonical agent-facing documents
```

New Android code must depend inward: `feature → domain ← data`. Receivers delegate to domain/use-case code; they do not contain business logic. No network or Android framework class appears in pure domain tests.

## 4. Coding and Commit Rules

Use Kotlin naming conventions, immutable state, sealed UI state where appropriate, validated input at boundaries, UTC timestamp storage with IANA timezone IDs, and the canonical names in `data-model.md`. Commit format is `type(scope): concise imperative summary`, such as `feat(schedule): add exact-alarm readiness evaluator`.

Every change must add/adjust the closest relevant tests. A task is not done if it compiles only; it must pass its Definition of Done.

## 5. Definition of Done

| Task type | Required before completion |
|---|---|
| Domain/state transition | Pure unit tests for valid and invalid transitions, reason-code outcome, and regression path. |
| Room persistence | DAO/repository test, encryption/migration coverage, deletion and reboot-recovery behaviour. |
| Compose UI | ViewModel test, UI/instrumentation test for success, empty, loading, error, and relevant readiness state. |
| Alarm/receiver | Unit test plus instrumented/device test plan; reboot/revocation/late-event behaviour. |
| Remote config | Signature/expiry/audience test, rollback test, audit flow, kill-switch test. |
| Backend endpoint | OpenAPI update, schema validation, auth/role test, tenancy test, idempotency/error test. |
| Accessibility work | Human Phase 2 approval, policy evidence, disclosure/consent screens, device matrix evidence, abort-safe test, no-blind-action test. |

## 6. Escalation Rule

If a requirement is ambiguous, lacks an acceptance criterion, conflicts with PRD v2.1, requires a new permission/data type, needs a cloud secret, or would cross a phase boundary, **do not guess**. Add a short issue describing the conflict, the affected PRD/engineering section, options, and the minimum human decision needed.
