# Seduligma Acceptance Traceability

**Status:** Canonical replacement for the valid traceability intent previously held in legacy document 10.

| PRD area | Engineering source | Test/exit evidence |
|---|---|---|
| FR-01 to FR-05: local lifecycle | `system-design.md`, `implementation-plan.md` P0/P1 | Unit state tests, Room/repository tests, editor/list UI tests. |
| FR-06 to FR-11: device readiness/recovery | `system-design.md`, `security-and-testing.md` | Alarm/notification revocation, reboot, OEM/device matrix tests. |
| FR-12 to FR-17: manual confirmation and Phase 2 automation | `system-design.md` multi-channel addendum, `security-and-testing.md` | Disclosure/consent, prefill verification, safe abort, profile/kill-switch, device evidence. |
| FR-18 to FR-21: later content/AI/rules | `implementation-plan.md` Phase 3 | Separate approved feature acceptance criteria. |
| Privacy/security | `data-model.md`, `security-and-testing.md` | Encryption, migration, redaction, consent withdrawal, config signature tests. |
| NFR-01 to NFR-06 | `security-and-testing.md`, `release-and-operations.md` | CI, real device reports, beta exit review, incident runbooks. |

An agent must update this matrix when a new approved PRD requirement, error code, profile, or release gate is introduced. A feature cannot be marked complete without linked acceptance evidence.
