# Android Security and Privacy Requirements

## Threat Model Priorities

| Risk | Required control |
|---|---|
| Stolen/reused API token | Android Keystore-backed encrypted token storage, short-lived access tokens, refresh rotation, device revocation |
| Cross-user data leak | Node API validates OIDC subject + tenant; Android never selects tenant by untrusted local ID |
| Sensitive message exposure | No plaintext logs, encrypted local body where justified, redacted crash reports |
| Malicious deep link/exported component | Explicit intent validation, `exported=false` by default, no unsafe pending intents |
| Insecure transport | TLS only, certificate/hostname validation, no cleartext traffic in release |
| Tampered release build | CI signing, protected signing key, Play App Signing for Play distribution |
| Excessive permissions | Just-in-time permissions, feature-scoped disclosure, revoke support |

## Secrets Rules

Do not commit `.jks`, `local.properties`, service-account JSON, API tokens, `.env`, Play credentials, Sentry auth token or signing key to Git. Use GitHub Actions secrets/environment protection and local ignored files. Rotate any secret that enters source control accidentally.

## Logging Rules

Allowed: schedule ID hash, status code, app version, device family, permission state, request ID and redacted error category. Forbidden: recipient phone, message body, access token, raw notification, credential, attachment bytes or full third-party UI content.

## Privacy UX

Before enabling any sensitive capability, show purpose, data access, local/server destination, retention and revoke steps. Provide account deletion, device unlink, diagnostic opt-in and data export/delete pathways as required by target market.

## Security Test Gate

Each release runs dependency scan, secret scan, lint/static analysis, API authorization tests, encrypted storage tests, exported-component tests and manual permission-revocation test. High-severity unresolved issue blocks beta promotion.
