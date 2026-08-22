# Documentation Governance Changelog — 2026-08-22

## Purpose

This changelog resolves the prior split between the legacy numbered `docs/00-*` through `docs/11-*` tree and the canonical PRD/engineering tree.

## Changes Made

| Change | Result |
|---|---|
| Root entry points | Root `README.md` and `AGENTS.md` now direct all readers/agents first to `docs/12-canonical-android-prd-v2.1.md` and `docs/engineering/onboarding.md`. |
| Legacy numbered documents | Replaced with deprecation notices pointing to their canonical replacements; they are not valid implementation authority. |
| Valid legacy release/operations content | Migrated to `release-and-operations.md`. |
| Valid legacy traceability intent | Migrated to `traceability.md`. |
| Multi-channel product source | Canonical PRD updated to describe shared `ExecutionAdapter` + signed `ChannelProfile` architecture for WhatsApp, Telegram, Messenger, SMS, and Email. |
| Legacy server-dispatch concept | Permanently removed by founder decision. It is not canonical scope, a future backlog item, or an implicit backend capability. |
| Repository topology | Founder confirmed one monorepo: `app/`, `backend/`, `web/`, `docs/`, and `.github/`. Backend and web implementation remain Phase 4-gated. |
| Uniform safety model | Every channel now has the same Phase 2 declaration/disclosure/consent/profile/device-matrix/no-blind-action/truthful-evidence gate. |

## Resolved Founder Decisions

The former unapproved legacy dispatch concept is permanently removed. The former split-repository assumption is replaced by the confirmed single-monorepo convention. Remaining open product approvals are maintained in `decisions-and-open-questions.md`.
