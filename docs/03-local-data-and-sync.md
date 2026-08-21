# Local Data, Encryption and Sync Model

## Local Source of Truth

Room is the local source of truth for Android schedules. The server is sync/backup and account-management source; it must not silently activate/execute a personal device schedule that is paused or blocked locally.

## Core Entities

| Entity | Essential fields |
|---|---|
| `ScheduleEntity` | `id`, `deviceId`, `title`, `bodyCiphertext`, `timezone`, `nextRunAtUtc`, `recurrenceJson`, `state`, `confirmationMode`, `createdAt`, `updatedAt`, `syncVersion` |
| `ScheduleRequirementEntity` | `scheduleId`, `requirementType`, `isSatisfied`, `lastCheckedAt`, `detailsCode` |
| `ScheduleEventEntity` | `id`, `scheduleId`, `eventType`, `occurredAt`, `detailsCode`, `safeMetadataJson` |
| `DeviceEntity` | `id`, `installationId`, `deviceLabel`, `appVersion`, `registeredAt`, `lastHealthyAt` |
| `SyncOutboxEntity` | `id`, `aggregateType`, `aggregateId`, `operation`, `payloadVersion`, `attemptCount`, `nextAttemptAt` |
| `TemplateEntity` | `id`, `title`, `bodyCiphertext`, `updatedAt`, `syncVersion` |

Do not persist raw accessibility trees, notification contents, device PINs, OTPs, passwords, or full third-party message history.

## Encryption Rules

1. Authentication refresh/session token is held in EncryptedSharedPreferences backed by Android Keystore.
2. Sensitive optional message/template body uses application-layer encryption key strategy approved by security review; search/analytics uses redacted metadata only.
3. Database backups are disabled unless encrypted backup behavior is explicitly tested.
4. Android logs never contain plaintext message body, attachment bytes or access token.

## Sync Protocol

1. Local mutation writes Room transaction and a `SyncOutboxEntity` atomically.
2. `SyncWorker` posts idempotent operation with device ID, aggregate ID and version.
3. Node API responds with accepted version or conflict response.
4. Conflict rule: explicit local pause/cancel wins over stale remote active state; server never re-enables blocked schedule.
5. On reconnect/reinstall, sync only user-authorized backup scope; show conflicts to user if a schedule has incompatible device state.

## Attachment Rules

Large media is never placed in Room. Store content URI with user-granted scoped access and optional encrypted upload metadata. App must explain when a scheduled attachment can no longer be read after user deletes/moves it.
