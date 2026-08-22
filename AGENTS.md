# Seduligma Coding Agent Entry Rules

Before any code or documentation change, read these files in this exact order:

1. [`docs/12-canonical-android-prd-v2.1.md`](docs/12-canonical-android-prd-v2.1.md)
2. [`docs/engineering/onboarding.md`](docs/engineering/onboarding.md)
3. [`docs/engineering/agent.md`](docs/engineering/agent.md)
4. The relevant canonical engineering documents named by the onboarding guide.

The numbered legacy `docs/00-*` through `docs/11-*` files are deprecated and must not be used as implementation authority.

## Non-Negotiable Rules

- Work only in the currently authorised PRD phase.
- Never add secret/PIN/OTP capture, autonomous AI sending, blind coordinate fallback, false delivery/read status, ban-evasion behaviour, remote personal-message dispatch, remote unlock, or arbitrary device commands.
- Never remove or weaken a required disclosure, consent, device-health, safe-abort, signed-config, or test gate.
- If a requirement is unclear, phase-gated, or would require an unapproved permission/data collection change, stop and request a human decision.
- Every task must satisfy the Definition of Done in `docs/engineering/agent.md` and report changed files, tests, CI result, limitations, and the next blocker.
