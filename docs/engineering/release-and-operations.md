# Seduligma Release and Beta Operations

**Status:** Canonical replacement for the valid release, CI/CD, beta-support, and operations material previously held in legacy documents 08 and 11.

## 1. Build and Release Pipeline

| Stage | Required checks | Failure rule |
|---|---|---|
| Pull request | Format, static analysis, unit tests, affected UI/instrumentation tests, debug build | No merge if any required check fails. |
| Main branch | Re-run protected CI, generate signed-internal artifact metadata, preserve test reports | Main is the only release-candidate source. |
| Internal build | Versioned artifact, non-production configuration, crash reporting verified | No real user rollout without privacy/support readiness. |
| Closed beta | Supported-device cohort, feedback collection, incident triage, staged rollout | Pause/rollback on unexpected action, selector breakage, data incident, or material crash trend. |

Release signing material is stored in CI secret storage or approved external secret management, never in source, issue comments, screenshots, or documentation. The exact signing provider remains a Phase 1 human implementation decision.

## 2. Environments

| Environment | Purpose | Data rule |
|---|---|---|
| Local developer | Unit/UI development | Synthetic data only. |
| CI | Build/test validation | Ephemeral test data; no production secrets. |
| Internal/staging | Controlled integration/device checks | Test accounts and redacted diagnostics only. |
| Closed beta | Limited invited users | Explicit beta consent, support path, incident response, supported-device scope. |
| Production | Approved public/commercial release | Only after applicable Phase exit gates. |

## 3. Closed Beta Support Operations

A beta tester report must capture app version, Android version, device/OEM, target channel/app profile, schedule state/reason code, and consented redacted diagnostics. It must not require a user to disclose message content, recipient content, PIN, OTP, screen-lock details, or screenshots containing unrelated private data.

| Severity | Example | Response |
|---|---|---|
| Critical | Unexpected UI action, potential privacy exposure, remote-config compromise | Disable affected feature/profile, preserve evidence, incident owner immediately engaged. |
| High | Repeatable safe-abort/selector failure on supported device | Pause affected profile rollout and issue fix/rollback. |
| Medium | Usability or device-health guidance issue | Triage into next supported beta release. |
| Low | Cosmetic or enhancement request | Log for product review. |

## 4. Channel Profile Rollout

Each `ChannelProfile` is released independently. A new WhatsApp, Telegram, Messenger, Google Messages, Samsung Messages, Gmail, or Outlook profile needs its own compatibility evidence, signed configuration release, kill switch, and support note. The shared adapter architecture does not permit assuming one app’s selector/UI behaviour applies to another.
