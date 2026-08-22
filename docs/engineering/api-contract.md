# Seduligma API Contract v1

**Decision made here, not in PRD:** REST JSON under `/v1`; OpenAPI 3.1 is generated from these endpoints before backend implementation. The API never offers a personal-message dispatch endpoint.

## 1. Global Contract

All requests use TLS, JSON, and `Authorization: Bearer <OIDC access token>`. API errors use:

```json
{
  "error": {
    "code": "DEVICE_NOT_FOUND",
    "message": "The requested device is unavailable.",
    "requestId": "req_...",
    "details": []
  }
}
```

| Status | Meaning |
|---|---|
| `200/201/202` | Successful synchronous/create/accepted asynchronous request. |
| `400` | Malformed request or schema validation failed. |
| `401/403` | Missing/invalid token or insufficient role/consent. |
| `404` | Resource outside caller scope or absent. |
| `409` | Version conflict, duplicate idempotency key, invalid lifecycle mutation. |
| `422` | Well-formed request fails business guard. |
| `429` | Account/device/admin rate limit. |

## 2. Phase Availability

| Endpoint family | Phase | Notes |
|---|---|---|
| Device registration and consent | 4 commercial platform | Backend is not required for Phase 1/2 execution. |
| Consented schedule metadata sync | 4 | No raw content default. |
| Subscription/customer portal | 4 | Provider IDs/webhooks only. |
| Signed remote config fetch | 2 policy-gated automation | Read-only, verified locally, no generic remote command. |
| Admin remote config release | 2 internal policy gate | Two-person approval/audit. |

## 3. Endpoints

### Authentication and Device

| Method/path | Auth | Request | Response | Guard |
|---|---|---|---|---|
| `POST /v1/devices` | User | `{publicKey, platform, appVersion}` | `201 Device` | One installation key per registration. |
| `GET /v1/devices/me` | User | — | `200 Device[]` | Only caller-owned devices. |
| `PATCH /v1/devices/{id}` | User | `{appVersion?, healthSummary?}` | `200 Device` | Redacted health only. |
| `POST /v1/devices/{id}/consents` | User | `{purpose, policyVersion, granted}` | `201 ConsentReceipt` | Immutable receipt, revocation supported. |

### Consented Sync

| Method/path | Auth | Request | Response | Guard |
|---|---|---|---|---|
| `POST /v1/devices/{id}/sync-events` | User + device binding | `{events:[{eventId,aggregateId,version,type,payloadClass,redactedPayload}]}` | `202 {acceptedIds}` | Idempotent event ID; content payload rejected unless scope consent exists. |
| `GET /v1/devices/{id}/sync-cursor` | User + device binding | `?cursor=` | `200 {events,nextCursor}` | Server sends metadata/config only. |

### Remote Configuration

| Method/path | Auth | Request | Response | Guard |
|---|---|---|---|---|
| `GET /v1/remote-config/current` | Device token | device/app metadata headers | `200 SignedConfig` | Audience, version, expiry scope checked. |
| `POST /v1/admin/remote-configs` | Admin | `{payload,audience,expiresAt}` | `201 DraftConfig` | Create draft only. |
| `POST /v1/admin/remote-configs/{id}/approvals` | Different admin | `{decision}` | `200 Config` | Creator cannot be sole approver. |
| `POST /v1/admin/remote-configs/{id}/release` | Admin | `{}` | `202 Config` | Requires approval, signed artifact, audit. |
| `POST /v1/admin/remote-configs/{id}/revoke` | Admin | `{reason}` | `202 Config` | Immediate kill switch, audit. |

### Subscription

| Method/path | Auth | Response | Guard |
|---|---|---|---|
| `GET /v1/subscription` | User | Current entitlement projection | Payment provider remains source of payment truth. |
| `POST /v1/billing/checkout-session` | User | Hosted checkout URL | Idempotency key; no card data handled by API. |
| `POST /v1/webhooks/razorpay` | Provider signature | `204` | Raw body signature verified before processing. |

## 4. Prohibited Endpoints

The API must not expose `POST /send`, `POST /execute`, remote accessibility command, raw-message log retrieval, credential upload, remote device unlock, or generic arbitrary-command endpoints. These contradict the local-first, user-controlled PRD.
