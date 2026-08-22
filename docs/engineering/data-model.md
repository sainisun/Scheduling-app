# Seduligma Canonical Data Model

This document defines shared concepts. Android and backend storage may shape them differently, but their meaning and state semantics must not diverge.

| Entity | Definition | Ownership | Content classification |
|---|---|---|---|
| `Schedule` | A user-created plan for a future local action. | Android device is execution source of truth. | Local-only by default. |
| `RecipientReference` | The minimum user-selected route/reference needed to prepare the target action. | Android device. | Sensitive; local-only by default. |
| `ScheduleEvent` | Immutable lifecycle entry explaining a state transition. | Android device; optionally redacted sync. | Event code is consented-sync eligible; raw content is not. |
| `DeviceHealthReport` | Current special-access/device-readiness summary. | Android device. | Consented diagnostic metadata only. |
| `Device` | Registered installation/public-key identity. | Backend. | Personal/device identifier. |
| `User` | OIDC-authenticated person. | Backend. | Personal data. |
| `Organization` | Optional commercial/agency scope. | Backend. | Business metadata. |
| `Subscription` | Provider-linked commercial entitlement. | Backend. | Financial reference IDs only. |
| `RemoteConfig` | Signed, scoped, expiry-bearing feature/config artifact. | Backend, verified locally. | Security-sensitive configuration. |
| `ConsentReceipt` | Evidence of a user grant or withdrawal for a named purpose/version. | Local and backend where consented. | Sensitive compliance record. |
| `AuditLog` | Append-only administrative/security action record. | Backend. | Redacted operational data. |

## Schedule Shape

```ts
type Schedule = {
  id: string;                       // UUID generated locally
  version: number;                  // optimistic-concurrency version
  title: string;
  messagePreview: string;           // user-visible; local-only by default
  recipientReference: RecipientReference;
  scheduledAtUtcMs: number;
  timezoneId: string;               // IANA zone
  recurrence: "ONCE" | "DAILY" | "WEEKLY" | "MONTHLY";
  state: ScheduleState;
  stateReasonCode?: ScheduleReasonCode;
  localEvidence?: LocalEvidence;
  createdAtUtcMs: number;
  updatedAtUtcMs: number;
};
```

`RecipientReference` is deliberately abstract. Beta 1 uses a single local reference; it must not assume that a backend can deliver a message or that a reference proves recipient identity.

```ts
type RecipientReference = {
  kind: "single";
  displayLabel: string;
  routeValue: string;               // encrypted at rest; never server-synced by default
};
```

## State and Evidence

| Concept | Allowed values |
|---|---|
| `ScheduleState` | `draft`, `needs_permission`, `waiting`, `blocked`, `attempting`, `completed`, `failed`, `uncertain`, `paused`, `cancelled` |
| `LocalEvidence` | `alarm_registered`, `notification_posted`, `user_confirmed`, `prefill_verified`, `final_ui_action_verified`, `none` |
| `ScheduleReasonCode` | Defined only in `system-design.md`; agents may not add ad-hoc string reasons. |

## Data Lifecycle Rules

| Data | Retention/deletion rule |
|---|---|
| Local schedule content | User can edit/cancel/delete; encrypted deletion on app reset/uninstall subject to OS guarantees. |
| Event log | Retained locally for configured support window; raw content excluded. |
| Device diagnostics | Sync only with explicit support/diagnostic consent; expire on defined retention policy. |
| Backend account/subscription records | Retained according to legal/accounting policy; payment details remain with payment provider. |
| Remote config | Retain release/audit metadata; clients reject expiry and can delete revoked payloads. |

## Naming Rules

Use singular domain names in code (`Schedule`, `Device`, `ConsentReceipt`), plural table/collection names in storage (`schedules`, `devices`, `consent_receipts`), and lower snake case for SQL columns. Avoid alternate names such as “job”, “task”, “send”, or “message” when the correct concept is `Schedule`.

## Multi-Channel Addendum

**Decision made here, not in PRD:** A `Schedule` carries a `channel` and resolves to a local signed `ChannelProfile`; it does not branch into native-SMS or direct-email entity families.

```ts
type Channel =
  | "WHATSAPP"
  | "TELEGRAM"
  | "MESSENGER"
  | "SMS_APP"
  | "EMAIL_APP";

type ChannelProfile = {
  id: string;
  channel: Channel;
  targetPackage: string;
  compatibleAppVersionRange: string;
  selectorSetVersion: string;
  prefillCapability: "SUPPORTED" | "NOT_SUPPORTED";
  status: "DRAFT" | "APPROVED" | "RELEASED" | "REVOKED";
  expiresAtUtcMs: number;
  signature: string;
};
```

`Schedule.recipientReference` is channel-specific but always local-only by default. An SMS reference is a user-selected target in a profiled SMS app; an email reference is a user-selected target in a profiled email app. Neither creates permission for protocol-level sending, server delivery receipts, or a backend copy of recipient content.

The local database adds `channel` to `schedule`, `channel_profile_id` and `channel_profile_version` to `schedule_event`, and `channel_profile_cache` for verified signed profiles. The backend remote-config metadata stores profile hashes, lifecycle, approval, audience, and revocation information—not raw selectors in telemetry and not message/recipient data.
