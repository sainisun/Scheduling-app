# Seduligma Engineering Onboarding

## Start Here

1. Read `../12-canonical-android-prd-v2.1.md` first.
2. Read `agent.md`; it is binding for coding agents.
3. Read `architecture-design.md`, `system-design.md`, `data-model.md`, and `security-and-testing.md` before adding any module or changing state behaviour.
4. Check `implementation-plan.md` to confirm the authorised phase and task.
5. Open the repository `TODO.md` and current GitHub Actions status before coding.

## Current Status

The Android repository contains an initial local scheduler foundation: Compose/Hilt, encrypted Room/SQLCipher storage, state model, recurrence logic, exact-alarm readiness, reboot reconciliation, local schedule create/edit/pause/cancel, device health, and CI. Treat this as a foundation requiring continued tests and real-device validation—not proof of production automation.

## Do Not Touch Without Human Approval

Do not start AccessibilityService execution, Tier D credential handling, backend personal-send features, account/billing, remote config release, CSV/groups/Status/auto-reply, or AI send decisions. These are phase-gated in the PRD and `implementation-plan.md`.

## Required Handoff Format

At the end of every agent session, report: changed files; relevant PRD/FR/NFR IDs; tests added and run; CI result; known limitations; and the next blocked decision. If no human decision is needed, state the next authorised implementation-plan task.
