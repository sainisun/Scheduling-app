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
| Uniform safety model | Every channel now has the same Phase 2 declaration/disclosure/consent/profile/device-matrix/no-blind-action/truthful-evidence gate. |

## Explicitly Unresolved Founder Decisions

The old documentation mentions an Official Business API server-side dispatcher and a two-repository split. Neither concept is merged into canonical scope by this change. Both require founder direction before they become product or repository decisions.
