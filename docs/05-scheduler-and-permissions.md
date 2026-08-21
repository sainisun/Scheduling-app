# Scheduler, Permissions and Device Health

## Scheduling Strategy

| Need | Android mechanism | Product behavior |
|---|---|---|
| Exact user-chosen time | `AlarmManager` after `canScheduleExactAlarms()` validation | If denied, label fallback as approximate; never claim exact |
| Reboot/app update/timezone recovery | WorkManager reconciliation worker | Rebuild only valid non-paused schedules |
| Periodic sync/health | Unique WorkManager work | Backoff on network errors; do not drain battery |
| User reminder/confirmation | Notification channel + action | User can confirm/cancel within expiry window |
| Persistent local state | Room transaction before alarm registration | Prevent duplicate or lost schedules |

## Required Permission Health States

| Requirement | Check | On failure |
|---|---|---|
| Exact alarm | `AlarmManager.canScheduleExactAlarms()` where required | `needs_permission`; direct user to system setting |
| Notification | Android runtime state | Display limited reminder warning |
| Battery/background | Device policy signal where available | `blocked` with non-coercive instructions |
| Feature-specific accessibility | Service enabled and connected | Disable feature and explain explicitly |
| Notification listener | Listener enabled only for auto-reply feature | Auto-reply inactive; schedules unaffected |
| Attachment readability | Content URI permission/file exists | Block attachment schedule or request reselect |

## Lifecycle State Machine

```text
draft → needs_permission → ready → waiting → preflight → attempting
                                              ├→ awaiting_user → attempting
                                              └→ blocked
attempting → completed | failed | uncertain
```

`completed` requires an implementation-defined reliable completion signal. If final condition cannot be verified, state remains `uncertain`. No background retry may duplicate an uncertain external action.

## Accessibility Safety Rule

Any AccessibilityService-based capability is separate feature flag and user opt-in. It must have visible in-app toggle, clear disclosure, narrow event scope, no screen unlock behavior, no invisible overlay and no action outside selected schedule flow. Before public distribution, current Play policy and relevant third-party terms must be reviewed.

## Recurrence

Use a typed recurrence model: one-time, daily, weekly (`daysOfWeek`), monthly (`dayOfMonth` with end-of-month policy), or custom interval. Recalculate only one next occurrence at a time after successful/terminal handling; do not use unbounded repeating alarms.
