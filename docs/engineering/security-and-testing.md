# Seduligma Security and Testing Specification

## 1. Data Classification

| Data | Classification | Storage/use rule |
|---|---|---|
| Schedule body and recipient reference | Local-only sensitive | SQLCipher encrypted; no sync without named consent. |
| Message preview | Local-only sensitive | Never put in crash logs or default telemetry. |
| State/reason/event code | Consented-sync eligible | Redacted metadata only. |
| Exact alarm/notification readiness | Consented diagnostics | No raw screen content. |
| Device ID/public key/app version | Account/device metadata | TLS, access-controlled backend. |
| Accessibility-derived content | Never collected by default | Requires declared scope and explicit consent; no generic scraping. |
| PIN/password/OTP/biometric | Never collected in Beta 1/2 | Tier D only after separate approval; never sync/log/backup. |
| Billing card/bank details | Never collected | Hosted provider flow only. |

## 2. Mandatory Test Cases

| Layer | Scenario | Expected result |
|---|---|---|
| Unit | Every valid/invalid state transition | Invalid transition rejects with canonical error; state version unchanged. |
| Unit | Monthly recurrence Jan 31 → Feb | Valid end-of-month policy result. |
| Unit | `uncertain` outcome | Never becomes `completed` without new user action/evidence. |
| DAO | Create/edit/pause/cancel/delete | Encrypted repository returns correct latest state/version. |
| Migration | Existing encrypted DB → next schema | Schema export validated, data preserved, migration tested. |
| Instrumented | Exact alarm denied at install | `needs_permission` with explicit settings route. |
| Instrumented | Exact alarm granted then revoked | Existing schedule moves to truthful non-waiting outcome. |
| Instrumented | Notification denied/regranted | Manual-confirmation readiness accurately updates. |
| System | Reboot before due time | Future schedule restored once; no duplicate execution. |
| System | Reboot near due time | Schedule becomes `uncertain`, never success. |
| UI | Edit active schedule | Previous alarm cancelled and item returns to draft. |
| Phase 2 | Prefill chat opens unexpected UI | Abort, reason `TARGET_UI_NOT_FOUND`, no coordinate fallback. |
| Phase 2 | Consent withdrawn | Execution adapter disabled, config/data access reduced as specified. |
| Remote config | Invalid/expired/wrong-audience signature | Client rejects artifact, keeps last known good config. |

## 3. Tier D Threat Model (Deferred but Required)

Tier D does not ship without a dedicated threat-model review. Assets include any credential-equivalent local material, device state, schedule content, and recovery controls. Threats include rooted/debuggable devices, local malware, backups, screenshots, logs, memory extraction, lost devices, compromised remote config, confused consent, unsupported locks, and incorrect unlock state. Required mitigations are: no server transfer; Android Keystore-backed protection; backup exclusion; no log/analytics inclusion; explicit opt-in/out; clear local deletion; fail-closed on unsupported conditions; independent penetration/security review; device matrix evidence; and a remote feature kill switch that disables only the feature, never controls the device.

## 4. Incident Runbooks

| Incident | Immediate action | Investigation | Recovery |
|---|---|---|---|
| Selector/config breakage | Revoke config release/feature flag. | Compare redacted event codes and supported-device reports. | Signed staged rollback; re-test before release. |
| Unexpected UI action | Disable adapter for audience; preserve redacted evidence. | Severity assessment and device/app-version analysis. | Human review; user notification where material; fix and controlled retest. |
| Crash during execution | Mark active lease outcome `uncertain`. | Crash trace without content; check lease/reboot state. | Recovery UI offers review/reactivation. |
| Consent withdrawal | Disable scoped feature/sync immediately. | Verify receipt/revocation propagation. | Delete consent-scoped server data per retention policy. |
| Remote-config compromise | Revoke key/config, disable dependent feature, rotate signing keys. | Audit release approvals and access logs. | Publish signed replacement after review. |

## 5. Play/Distribution Compliance Checklist

- [ ] Distribution route documented before Phase 2 code is enabled.
- [ ] Accessibility declaration completed with accurate functionality/data answers.[1]
- [ ] Prominent in-app disclosure is separate from Terms/Privacy Policy and appears in normal flow.[1]
- [ ] Affirmative consent, withdrawal, and re-entry path are implemented.
- [ ] Reviewer video shows disclosure, consent/no-consent paths, and actual core feature.[1]
- [ ] Data Safety form maps to actual collection/sharing.
- [ ] No autonomous AI action, secret capture, blind UI fallback, or delivery/read claim is present.
- [ ] Supported-device list and beta support process are published.

## References

[1]: https://support.google.com/googleplay/android-developer/answer/10964491?hl=en-GB "Google Play — Use of the AccessibilityService API"

## 6. Uniform Multi-Channel Accessibility Test Matrix

Every channel uses the same Phase 2 security/policy gate and the same local-evidence rule. The app must not claim a richer delivery/read result for SMS or email merely because native protocols could expose such information; this product executes through the target app UI and reports only what the local workflow can verify.

| Channel/app family | Required profile/device coverage | Required failure tests |
|---|---|---|
| WhatsApp / WhatsApp Business | Supported WhatsApp family and app versions on each approved OEM | Prefill target mismatch, chat not opened, final action not verified, dual-app ambiguity. |
| Telegram | Supported Telegram app version on each approved OEM | Search/compose state absent, selector change, draft not present, final action unverified. |
| Messenger | Supported Messenger version on each approved OEM | Login/interstitial, composer unavailable, selector change, final action unverified. |
| SMS app | Google Messages and Samsung Messages as separate profiles; do not treat “SMS” as one UI | Default-SMS-app change, compose unavailable, permission/role UI interference, selector mismatch. |
| Email app | Gmail and Outlook as separate profiles; do not treat “Email” as one UI | Account chooser, attachment not ready, compose unavailable, selector mismatch, final action unverified. |

For each profile, test: first install; consent declined/accepted/withdrawn; exact alarm granted/revoked; notifications denied/regranted; app force-stop/restart; reboot around due time; low storage/network interruption; target app version outside profile range; selector mismatch; concurrent schedules; user interrupt; kill switch; and rollback to the last known-good profile.

Automation must remain deterministic and static: a human creates the schedule and channel; the profile supplies only approved target-state identification; the client aborts when expected UI evidence is missing. Google Play permits deterministic, rule-based automation but prohibits Accessibility use that autonomously initiates, plans, and executes actions or decisions.[1]
