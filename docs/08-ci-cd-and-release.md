# CI/CD, Signing and Release Process

## GitHub Branch Model

| Branch | Purpose | Merge policy |
|---|---|---|
| `main` | Always releasable Android source | Protected, PR + CI required |
| `feat/*` | One bounded feature slice | PR to main after review/tests |
| `fix/*` | Production/beta defect | PR to main with regression test |
| `release/*` | Stabilization only if release branch is needed | No feature additions |

## GitHub Actions Pipeline

1. Checkout and validate Gradle wrapper.
2. Run Kotlin formatting/lint and Android Lint.
3. Run JVM unit tests and code coverage.
4. Run Room migration and API client/contract tests.
5. Run secret/dependency scan.
6. Build debug APK for PR artifact.
7. On protected `main`, build signed internal AAB/APK using GitHub environment secrets.
8. Upload to internal test track only after manual approval and release notes.

## Environment Separation

| Environment | Backend | Crash project | Distribution |
|---|---|---|---|
| Local | Local/mock Node API | Disabled/local | Developer device |
| Staging | `api-staging` | Staging project | Debug/internal APK |
| Beta | Isolated beta backend/data | Beta project | Play internal/closed testing |
| Production | `api` | Production project | Play production track |

## Signing Rules

Use Google Play App Signing for Play-distributed builds. Upload key is restricted to release owners, encrypted backup is held outside Git repository, and rotation/revocation procedure is documented. Do not share keystore over chat/email/source control.

## Release Checklist

Release notes, known limitations, privacy-policy URL, permissions disclosure, support email, screenshot/video evidence where required, current test report, rollback decision, crash-monitoring dashboard and beta cohort list must be ready before tester rollout.
