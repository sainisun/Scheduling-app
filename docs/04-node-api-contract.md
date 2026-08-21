# Android ↔ Node.js API Contract

## API Principles

The existing Node.js backend provides HTTPS JSON APIs. It validates OIDC access tokens, resolves user/tenant authorization server-side, uses PostgreSQL for authoritative account/subscription data and never trusts device-provided role or entitlement values.

Base URLs must be environment-scoped: `https://api-staging.<domain>` for beta and `https://api.<domain>` for production. Android app never hardcodes production keys in source.

## Required Endpoints

| Method / path | Purpose | Android caller |
|---|---|---|
| `POST /v1/mobile/devices/register` | Register/update one device installation | On login/install |
| `POST /v1/mobile/devices/{id}/heartbeat` | Send redacted app/health version state | Periodic worker, user-consented |
| `GET /v1/mobile/entitlements` | Get plan limits/capabilities | App launch and entitlement refresh |
| `GET /v1/mobile/schedules?cursor=` | Pull user-approved backup metadata | Sync worker |
| `PUT /v1/mobile/schedules/{id}` | Idempotent schedule upsert | Sync outbox |
| `POST /v1/mobile/schedules/{id}/events` | Upload safe lifecycle event | State transition |
| `DELETE /v1/mobile/schedules/{id}` | Sync user deletion/tombstone | Delete flow |
| `GET /v1/mobile/templates` | Pull templates allowed for mobile | Template screen |
| `POST /v1/mobile/support-diagnostics` | User-initiated redacted support bundle | Support action only |

## Common Headers

```text
Authorization: Bearer <OIDC access token>
X-Device-Id: <UUID>
X-App-Version: <semantic version>
Idempotency-Key: <UUID for mutation>
X-Request-Id: <UUID>
```

## Error Contract

```json
{
  "code": "ENTITLEMENT_LIMIT_REACHED",
  "message": "Human-readable safe summary",
  "requestId": "uuid",
  "retryable": false,
  "details": { "limit": "pending_schedules" }
}
```

Never return provider tokens, internal stack traces or tenant-sensitive data to Android client. `401` triggers refresh/logout flow, `403` is visible as no-access, `409` is a sync conflict, `429` applies retry-after, and `5xx` uses bounded exponential backoff.

## Contract Testing

Node backend publishes OpenAPI specification. Android CI generates/validates DTO compatibility or runs contract tests against staging test environment. Every endpoint must include success, auth failure, validation failure, entitlement denial and retryable failure tests.
