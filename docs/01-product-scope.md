# Product Scope — Seduligma Android Companion

## Product Statement

Seduligma Android Companion user ko apne Android phone par future message workflow schedule karne deta hai. User message, recipient/workflow, date-time aur repeat rule choose karta hai; app permission/device readiness validate karke schedule ka truthful status dikhata hai.

## Product Modes

| Mode | Primary user problem | Dispatcher | Core promise |
|---|---|---|---|
| Personal Android Scheduler | Personal mobile communication ko future time par plan karna | Registered Android device | Transparent local scheduling and health visibility |
| Official Business API | Business campaigns, templates aur client communication | Node.js worker + selected provider | Server-side scheduled delivery with webhook reporting |

Personal Android mode ko official Business API jaisa reliable server delivery claim nahi karna hai. Device state, permissions, OS restrictions aur third-party app behavior execution ko affect kar sakte hain.

## R1 Closed-Beta Scope

| Feature | R1 decision |
|---|---|
| Account/device registration | Include |
| Local schedule create, edit, pause, cancel, delete | Include |
| Timezone-aware one-time and daily/weekly/monthly recurrence | Include |
| Calendar and next-run display | Include |
| Notifications and confirmation-before-send | Include |
| Permission/device-health checklist | Include |
| Local execution event log | Include |
| Safe server sync/backup | Include |
| Contact/list/group workflow adapters | Evaluate after scheduler reliability gate |
| Auto-reply | Later beta slice |
| Status scheduling | Separate feasibility/test decision |
| Multi-channel / AI | Not R1 |

## Explicitly Out of Scope

The app will not bypass account restrictions, hide user-visible activity, automate screen unlock, collect private credentials, evade detection, or send unsolicited/bulk spam. “Never banned” is not a product claim because account enforcement is outside app control.

## User Stories

1. As a user, I can create a schedule and see its exact local time/timezone.
2. As a user, I can see whether missing permissions or battery settings block a schedule.
3. As a user, I can pause or cancel a schedule before it starts.
4. As a user, I can choose a confirmation step for sensitive scheduled actions.
5. As a user, I can distinguish completed, failed and uncertain execution results.
6. As a subscriber, I can restore schedule metadata and connected device state after signing in.

## Success Metrics

| Metric | R1 target |
|---|---|
| Schedule persistence after reboot | 100% in automated test matrix |
| False completed status | 0 known cases |
| Permission-blocked schedules with actionable reason | 100% |
| Crash-free beta sessions | Target ≥ 99% before expansion |
| Supported device families | Pixel, Samsung, Xiaomi/Redmi/POCO before closed beta exit |
