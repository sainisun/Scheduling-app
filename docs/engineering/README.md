# Seduligma Engineering Documentation Suite

This directory is the implementation layer for the canonical [Android PRD v2.1](../12-canonical-android-prd-v2.1.md). A coding agent must not treat any document here as permission to weaken a PRD guardrail.

## Required Reading Order

| Order | Document | Purpose |
|---|---|---|
| 1 | `../12-canonical-android-prd-v2.1.md` | Product authority, phases, safety limits, acceptance criteria. |
| 2 | `agent.md` | Binding coding-agent scope, prohibitions, Definition of Done, escalation rules. |
| 3 | `architecture-design.md` | Component responsibilities, local-first boundary, trust boundaries. |
| 4 | `system-design.md` | State transitions, error taxonomy, concurrency, local/backend schema. |
| 5 | `data-model.md` | Canonical entities and names shared by Android/backend. |
| 6 | `security-and-testing.md` | Data classification, required tests, incident runbooks, compliance checklist. |
| 7 | `implementation-plan.md` | Authorised task sequence and phase exit gates. |
| 8 | `api-contract.md` | Phase 4 backend contract; no personal-send endpoint. |
| 9 | `design-system.md` | Android/web visual and honest-status semantics. |
| 10 | `decisions-and-open-questions.md` | Decisions made here versus human approval required. |

`onboarding.md` is the concise entry point for a new agent session.

## Authority Rules

1. The PRD overrides this suite if any conflict appears.
2. `agent.md` governs agent conduct and escalation.
3. Documents label any decision not already in the PRD as **Decision made here, not in PRD**.
4. A human decision recorded in `decisions-and-open-questions.md` cannot be guessed or bypassed.

## Current Implementation Boundary

The Android project is in the Phase 1 local scheduler path. Phase 2 personal automation remains gated on the human and distribution approvals recorded in the PRD and decisions register.
