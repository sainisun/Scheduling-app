# Seduligma Decisions and Open Questions

## Recorded Engineering Decisions

| ID | Decision | Source |
|---|---|---|
| D-01 | Android device is local schedule execution source of truth. | PRD v2.1 Section 3. |
| D-02 | Phase 2 automation follows Phase 1 beta, before commercial backend. | PRD v2.1 Section 13. |
| D-03 | Prefilled click-to-chat is attempted before narrow final-action Accessibility interaction. | PRD v2.1 FR-12/15. |
| D-04 | Backend uses NestJS/Fastify, PostgreSQL, Redis, REST/OpenAPI. | Architecture/system design decision. |
| D-05 | Backend has no personal-send, unlock, or arbitrary device-command endpoint. | PRD v2.1 local-first principle. |

## Human Decisions Required Before Their Phase

| Decision | Required before | Owner |
|---|---|---|
| Play public/closed/private distribution route | Phase 2 implementation/enablement | Founder + legal/product |
| Supported Android API/OEM/device list | Phase 1 closed beta | Engineering + QA |
| Accessibility declaration/disclosure/Data Safety package | Phase 2 beta | Product + legal + engineering |
| Phase 2 go/no-go | Any real-user execution adapter release | Founder + security + product |
| Commercial legal entity/region/payment setup | Phase 4 billing | Founder + finance/legal |
| Tier D PIN/password go/no-go | Any Tier D code | Founder + independent security/legal |

No agent may silently resolve these decisions.
