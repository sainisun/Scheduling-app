# Seduligma Android Scheduler

## Canonical Entry Point

This repository has one authoritative documentation path for every developer and coding agent:

1. Read [`docs/12-canonical-android-prd-v2.1.md`](docs/12-canonical-android-prd-v2.1.md).
2. Then start at [`docs/engineering/onboarding.md`](docs/engineering/onboarding.md).
3. Follow the required reading order in [`docs/engineering/README.md`](docs/engineering/README.md).

The Android device is the local scheduling and execution source of truth. The app is a transparent user-authorised scheduler; it is not a stealth tool, account-restriction bypass, delivery/read tracking system, or remote device-control product.

## Documentation Governance

The historical numbered `docs/00-README.md` through `docs/11-*.md` files are **deprecated** and are retained only as pointers to their canonical replacements. Do not use them as implementation authority. The current governance record is [`docs/engineering/governance-changelog-2026-08-22.md`](docs/engineering/governance-changelog-2026-08-22.md).

## Current Build Boundary

The currently authorised code path is Phase 1 local scheduler hardening. Phase 2 personal automation requires the explicit founder/distribution/disclosure/device-matrix approvals listed in the PRD and the engineering decision register.

## Repository

Android source lives in `app/`; canonical documentation lives in `docs/`. Build and test status is enforced through GitHub Actions. See [`AGENTS.md`](AGENTS.md) for the mandatory coding-agent rules.
