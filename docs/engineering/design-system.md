# Seduligma Design System

## 1. Visual Direction

Use calm, operational Android-native UI: clear hierarchy, low visual noise, explicit status evidence, and no false-success styling. Compose Material 3 conventions take priority on Android; the web dashboard shares semantic tokens but follows web navigation conventions.

## 2. Semantic Tokens

| Token | Android value | Meaning |
|---|---|---|
| `primary` | `#135BEC` | Main actions and active navigation. |
| `success` | `#0A6E3A` | `completed` with defined local evidence only. |
| `warning` | `#A15C00` | `needs_permission`, `paused`, human review. |
| `danger` | `#B3261E` | `failed`, destructive actions. |
| `uncertain` | `#5E5E70` | Unknown outcome; never use success green. |
| `blocked` | `#7B1FA2` | Device/precondition blocker. |
| `info` | `#005AC1` | Waiting and device guidance. |

| Token | Value |
|---|---|
| Spacing scale | 4, 8, 12, 16, 24, 32 dp |
| Corner radius | 12 dp cards, 8 dp controls |
| Minimum touch target | 48 × 48 dp |
| Body text | 16 sp default; 14 sp secondary |
| Contrast | Meet WCAG AA; do not rely on colour alone. |

## 3. Required Android Screens/States

| Screen | Required UI states |
|---|---|
| Schedule list/calendar | Loading, empty, upcoming, paused, failed, uncertain, completed, offline local-only indicator. |
| Schedule editor | Draft, invalid date/time, future-time validation, timezone selector, recurrence selector, save/activate distinction. |
| Device health | Exact alarm missing, notifications missing, app unsupported, battery/OEM guidance, ready. |
| Ask Me Before Sending | Notification title/body/action names that clearly say user action is required; no implication it was sent. |
| Execution history | Evidence chip plus reason code explanation; `completed` displays local evidence label. |
| Consent/disclosure | Separate prominent disclosure, accept/decline, withdrawn state, re-enable path. |

## 4. Accessibility (App A11y) Requirements

This section concerns accessibility of Seduligma’s own interface, not the separate AccessibilityService automation permission. Every icon action has a content description, state uses text plus colour/icon, focus order matches reading order, dynamic type does not clip critical status text, and all error messages are announced. Status cards must say, for example, “Uncertain — review required,” not just present a grey dot.

## 5. Web Dashboard Direction

The Next.js dashboard uses the same semantic states and colour meanings. It uses an application sidebar, workspace/device context, filtered event tables, and a clear “device executes locally” disclosure near every schedule view. The dashboard must not show a remote “Send now” action for personal workflows.
