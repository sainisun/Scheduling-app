# Seduligma Android Companion — Canonical Documentation Index

Yeh package `sainisun/Seduligma-android` repository ke liye canonical specification hai. Android app **Kotlin + Jetpack Compose** mein banega, jabki existing `sainisun/Seduligma` repository Next.js frontend aur Node.js backend/platform ke liye rahega.

## Document Reading Order

| File | Purpose | Kab padhen |
|---|---|---|
| `01-product-scope.md` | Product boundaries, modes aur user outcomes | Kisi feature se pehle |
| `02-android-architecture.md` | Modules, dependencies aur technical decisions | Project scaffold karte samay |
| `03-local-data-and-sync.md` | Room entities, encrypted storage aur sync strategy | Data/API work se pehle |
| `04-node-api-contract.md` | Android-to-Node API contracts | Backend integration se pehle |
| `05-scheduler-and-permissions.md` | Alarm, WorkManager, permission health aur statuses | Scheduling feature se pehle |
| `06-security-and-privacy.md` | Security, privacy aur secret-handling rules | Har implementation task mein |
| `07-testing-and-quality-gates.md` | Automated/real-device testing aur release gates | PR merge aur beta se pehle |
| `08-ci-cd-and-release.md` | GitHub Actions, signing, staging aur Play testing | Release pipeline banate samay |
| `09-vibe-coding-agent-rules.md` | AI/vibe-coding agent instructions | Har agent session se pehle |
| `10-acceptance-traceability.md` | Feature-to-test traceability | Sprint planning aur acceptance mein |
| `11-beta-support-and-operations.md` | Closed beta, observability aur support process | Tester invite se pehle |

## Non-Negotiable Rules

1. App user-authorized scheduling product hai; stealth, policy bypass, ban-proof ya spam tool nahi hai.
2. App WhatsApp password, device PIN, OTP, recovery code, fingerprint ya screen-lock credential kabhi collect/store nahi karega.
3. Android personal mode aur official Business API mode alag dispatch mechanisms hain; UI mein unhe mix nahi karna hai.
4. `uncertain` status ko kabhi `sent` ya `completed` nahi dikhana hai.
5. Har code change ke saath automated tests aur relevant real-device checklist update karni hai.
6. Secrets `.env`, `local.properties`, GitHub Secrets ya secret manager mein rahenge; source code/issue/comment mein nahi.

## Repository Convention

```text
Seduligma-android/
├── app/
├── core/{common,data,domain,ui,testing}/
├── feature/{onboarding,schedules,calendar,device-health,account,autoreply}/
├── docs/
├── .github/workflows/
├── AGENTS.md
└── README.md
```

`main` branch protected rahegi. Feature work `feat/<name>` branches par hoga. Direct `main` push allowed nahi hoga.
