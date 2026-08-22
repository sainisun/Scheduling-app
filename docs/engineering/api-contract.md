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

## 5. Multi-Channel Profile Contract

**Decision made here, not in PRD:** Remote configuration serves approved channel profiles for the same local Accessibility execution engine. It never sends a message through SMS, email, Telegram, or any other channel on behalf of a device.

| Method/path | Auth | Request | Response | Guard |
|---|---|---|---|---|
| `GET /v1/remote-config/channels` | Device token | device/app metadata headers | `200 SignedChannelProfile[]` | Returns only approved, audience-compatible, unexpired profiles. |
| `GET /v1/remote-config/channels/{channel}/current` | Device token | device/app metadata headers | `200 SignedChannelProfile` | No profile returns `404 CHANNEL_PROFILE_UNAVAILABLE`; client stays disabled. |
| `POST /v1/admin/channel-profiles` | Admin | `{channel,targetPackage,versionRange,selectorSet,expiresAt}` | `201 DraftProfile` | Draft only; full payload never appears in general client logs. |
| `POST /v1/admin/channel-profiles/{id}/approvals` | Different admin | `{decision}` | `200 Profile` | Maker/checker separation. |
| `POST /v1/admin/channel-profiles/{id}/release` | Admin | `{}` | `202 Profile` | Requires signed artifact, test evidence reference, approval, audit record. |
| `POST /v1/admin/channel-profiles/{id}/revoke` | Admin | `{reason}` | `202 Profile` | Per-channel kill switch. |

`SignedChannelProfile` includes the channel, target package, compatible version range, selector-set version/hash, prefill capability, audience, issue/expiry time, signature, and rollback configuration. It does not carry free-form executable code, recipient data, raw content, timing-randomization rules, or remote send commands.

The Telegram Bot API remains documented as a founder-approved alternative option only. If selected later, it requires a new versioned API section, consent/data-flow review, and explicit product decision. No existing endpoint treats it as a fallback for `TelegramAdapter`.

## 6. Concrete DTO Shapes and Module Ownership

The endpoint tables are implemented in Phase 4 only. These exact shapes are sufficient to generate validation DTOs and OpenAPI schemas without inventing property names.

```ts
type RegisterDeviceRequest = {
  publicKey: string;
  platform: "ANDROID";
  appVersion: string;
};

type DeviceResponse = {
  id: string;
  platform: "ANDROID";
  appVersion: string;
  status: "ACTIVE" | "REVOKED";
  lastSeenAtUtcMs: number | null;
};

type RecordConsentRequest = {
  purpose: "SUPPORT_DIAGNOSTICS" | "METADATA_SYNC" | "AUTOMATION_DISCLOSURE";
  policyVersion: string;
  granted: boolean;
};

type SyncEventRequest = {
  eventId: string;
  aggregateId: string;
  version: number;
  type: string;
  payloadClass: "REDACTED_METADATA";
  redactedPayload: Record<string, string | number | boolean>;
};
```

| API route group | Future owning backend module | Future web consumer |
|---|---|---|
| `/v1/devices*` and device consent | `modules/devices`, `modules/consents` | Customer devices and privacy routes. |
| `/v1/devices/{id}/sync-*` | `modules/sync` | Customer schedule-metadata view only. |
| `/v1/remote-config*`, `/v1/admin/channel-profiles*` | `modules/remote-config`, `modules/audit` | Admin profile/release and audit routes. |
| `/v1/subscription*`, `/v1/billing*` | `modules/subscriptions`, `modules/billing` | Customer billing route. |

Every `POST`, `PATCH`, and `DELETE` request carries an `Idempotency-Key` UUID header. Validation rejects unknown fields, raw content fields, route/recipient fields, credentials, and any command-shaped payload. Backend implementation begins only under the approved Phase 4 plan, except the narrow Phase 2 signed-profile endpoints after that separate gate.
