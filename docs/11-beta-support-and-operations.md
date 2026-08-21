# Closed Beta, Support and Operations

## Beta Scope

Start with 10–20 trusted Android users who agree to provide feedback. Beta users must be told that personal scheduling is device-dependent, which permissions are required, and that unsupported/blocked state is possible. Do not market public “guaranteed sending” claims during beta.

## Observability

| Signal | Alert/Review action |
|---|---|
| Crash-free sessions | Daily review; block rollout on material regression |
| Schedule blocked reason | Aggregate by device/OS/permission; improve health instructions |
| `uncertain` events | Investigate before adding automatic retry behavior |
| Alarm/worker lag | Detect scheduling reliability regression |
| Sync error rate | Check Node API/network/auth contract |
| Permission revocation | Show user corrective UI, no repeated coercive prompts |

Diagnostics must be redacted: use schedule ID hash, app version, Android version, device family, permission state and error category. Never upload message content, phone PIN, OTP, token or raw notifications.

## Support Workflow

1. User opens schedule event log and copies safe support reference ID.
2. Support checks account/device health, version and error category.
3. Support provides no-secret troubleshooting: permission, notification, alarm, battery or app-version guidance.
4. Suspected security incident triggers token revocation/device unlink and incident runbook.
5. Every confirmed defect becomes tracked issue with regression test before fix is released.

## Rollout Strategy

Internal testers → closed beta cohort A → closed beta cohort B → limited production. Each expansion requires test gate evidence, stable crash trend, no unresolved high-severity security issue and documented known limitations.
