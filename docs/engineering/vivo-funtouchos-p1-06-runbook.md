# P1-06 Founder Runbook — Vivo/FuntouchOS Physical Device

> **Purpose:** Produce truthful, repeatable evidence for one named Vivo/FuntouchOS device. This is a human-operated physical-device task. Passing this runbook verifies only the documented Vivo model, Android/FuntouchOS version, and Seduligma build; it does not verify all Vivo devices, Samsung, Xiaomi/Redmi/POCO, or any future Phase 2 automation.

## 1. Before You Start

Use a personal test schedule whose title and preview contain no customer names, phone numbers, or confidential messages. Keep the phone charged, use a known-good USB data cable, and make sure the development computer has Android Studio, Android SDK Platform Tools, JDK 17, and this repository cloned. Record the information below before changing any setting.

| Record | Founder entry |
|---|---|
| Test date/time and timezone | |
| Vivo model number | |
| Android version / security patch | |
| FuntouchOS version | |
| Seduligma Git commit and debug APK build time | |
| USB cable/computer used | |
| Tester initials | |

## 2. Enable Developer Options and USB Debugging

The exact labels vary by FuntouchOS generation. Start with **Settings → System management → About phone → Version information**, then tap **Software version** seven times. On other Vivo versions, the equivalent route is **Settings → More settings → About phone → Software version**. Enter the device unlock PIN if Vivo requests it; do **not** share or record it. Return to **System management** or **More settings**, open **Developer options**, enable **USB debugging**, and accept the device warning. Some Vivo builds also show **USB safety permissions**; enable it only if the device requires it for the testing connection.[1] [2]

Connect the phone to the development computer using the USB cable. If the phone asks how USB should be used, select a data-capable mode such as **File transfer**. On the phone, accept the **Allow USB debugging?** RSA fingerprint prompt only after confirming it appears for your own testing computer. Do not accept this prompt on a public or unknown computer.

On the computer, open a terminal in the repository and run:

```bash
adb devices
```

The result must show one device with the status `device`. If it says `unauthorized`, unlock the phone and accept the RSA prompt; if no device appears, try another data cable/USB port and confirm USB debugging remains enabled. Android’s official documentation confirms that Developer options locations can differ by device and that USB debugging enables ADB communication.[1]

## 3. Build, Install, and Run the Existing Automated Tests

In the repository root, first update your branch and build the debug app plus instrumentation test APK:

```bash
git pull --ff-only origin main
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest
```

Install the app and test APK, then run the instrumented suite. The command uses the application ID already configured in this project.

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w com.seduligma.app.test/androidx.test.runner.AndroidJUnitRunner
```

Mark this section **pass** only when the command finishes with `OK`. If it fails, save the terminal output and collect a redacted log before retrying:

```bash
adb logcat -d -v threadtime > vivo-p1-06-logcat.txt
```

Do not place schedule content, customer information, unlock credentials, or unrelated personal content in an issue, commit, or chat. If the log contains sensitive material, redact it locally before sharing.

## 4. Perform the Founder Checklist on the Phone

For each item, capture one redacted screenshot or a short screen recording that shows the relevant status, then write **Pass**, **Fail**, or **Ambiguous** in the evidence record. “Ambiguous” is a valid result and must not be converted to Pass.

| # | On-phone check | Expected Phase 1 result | Evidence to retain |
|---:|---|---|---|
| 1 | Create a one-time test schedule two to five minutes ahead. | It appears as a draft, then activates only through the visible UI. | Editor and list-state screenshot. |
| 2 | Edit the active schedule, then pause, reactivate, and cancel a separate test schedule. | Each action changes to its truthful visible state; no hidden send occurs. | Before/after state screenshots. |
| 3 | In Seduligma, check device health. Temporarily deny exact-alarm access if the device exposes the setting, then return and grant it. | The app shows a permission-required state and settings route while absent; it refreshes truthfully after regrant. | Device-health states and relevant setting page. |
| 4 | Temporarily turn off Seduligma notifications, return to the app, then turn notifications back on. | The app reports notification readiness truthfully and refreshes after regrant. | Denied/regranted states. |
| 5 | Let an active schedule reach its due time with notifications allowed. | A local **Ask Me Before Sending** notification asks the user to review; it does not send a message or request credentials. | Notification screenshot and resulting local evidence entry. |
| 6 | Create a future schedule, reboot the phone before its due time, unlock normally, and open Seduligma. | The future schedule remains visible and is reconciled once; there is no duplicate execution claim. | Schedule before reboot and after reboot. |
| 7 | For a separate disposable test, reboot close to its due time. | If the app cannot know the outcome, it uses the truthful uncertain/needs-review path rather than claiming success. | Local event-history entry. |
| 8 | Open recent local activity/history. | Entries show only known local evidence and reason codes; they do not claim delivery or read status. | Redacted history screenshot. |
| 9 | Use **Erase local schedules and history**, read the warning, and confirm only for disposable test data. | Local schedules/history are removed, related alarms are cancelled, and the action cannot be undone. | Confirmation dialog and post-reset empty state. |
| 10 | With an active future schedule, leave the app, lock the phone, and observe whether the due-time reminder arrives. If it does not, inspect Vivo’s battery/background controls without guessing a universal setting path. | Record the observed behavior and any needed device-specific battery/autostart change. Do not label the device supported if the expected reminder is not reliable under the documented condition. | Condition notes, power-setting screenshots, and outcome. |

## 5. Check Vivo Background/Battery Behavior Carefully

FuntouchOS menu names vary by model and release. Search the Settings app for **Battery**, **Background**, **Autostart**, or **App battery management** rather than following a guessed internet path. Take a screenshot of the actual setting page before and after any change. Repeat checklist item 10 first under the phone’s default battery settings; only then repeat after a user-visible change if needed. Your evidence must say which condition passed or failed.

> Do not treat a manually exempted battery setting as proof of default reliability. Record it as “passed only with [actual setting name] enabled.”

## 6. Complete and Store the Evidence Record

Create a dated file under `docs/testing-results/` using this filename format:

```text
YYYY-MM-DD_vivo_<model>_funtouchos-p1-06.md
```

Use the template below. Keep screenshots and logs outside Git if they contain any sensitive information; link to an approved private storage location or attach only redacted copies. The final device row must be one of the four labels in the implementation plan.

```markdown
# P1-06 Device Evidence — Vivo <model>

| Field | Result |
|---|---|
| Evidence label | Real-device verified / Failed / Ambiguous / Not yet verified |
| Vivo model | |
| Android / FuntouchOS version | |
| Seduligma commit | |
| Tester / date / timezone | |
| Default battery-condition result | |
| Any changed setting and result | |
| Automated instrumentation result | Pass / Fail; artifact path |
| Checklist result | Pass / Fail / Ambiguous by item |
| Known limitations / next action | |

## Redacted evidence links

- 
```

The founder or release owner must review the completed record before changing the Vivo/FuntouchOS matrix status to **real-device verified**. A failed or ambiguous result remains part of the record and blocks support-list inclusion until resolved and retested.

## 7. Optional Samsung/Xiaomi Cloud-Device-Farm Lane

**Recommendation: set up one small Firebase Test Lab smoke run now, but do not let it delay the Vivo run.** It is a practical way to obtain evidence from a real remote Samsung or Xiaomi-family model if the current catalog offers one. On Firebase’s Spark plan, Test Lab currently allows up to 5 physical-device and 10 virtual-device test runs per project per day. On Blaze, the published no-cost daily time allowance is 30 minutes for physical devices and 60 minutes for virtual devices before rates of $5/device-hour and $1/device-hour respectively; billing is per minute rounded up. The actual catalog, device capacity, and reduced-stability flags change, so record the selected model and limitations rather than promising a vendor will always be available.[3] [4]

The setup is: create a dedicated Firebase project; open **DevOps & Engagement → Test Lab**; build the app and instrumentation APKs using the commands above; choose **Instrumentation test**; upload both APKs; select exactly one currently listed Samsung or Xiaomi/Redmi/POCO real model and one Android version; run only the small Phase 1 smoke tests first; then retain the Test Lab result URL/export, device model, OS, commit SHA, test APK, capacity/stability labels, duration, and pass/fail output. If no suitable OEM model is listed, or a session exceeds quota/has low capacity, record that limitation and keep the lane **not yet verified**.[3] [4]

Do not claim that a cloud run validates Vivo, all Samsung phones, all Xiaomi-family phones, physical owner interaction, or an untested background-restriction workflow. It validates only the exact remote device/model/OS and uploaded test scope.

## References

[1]: https://developer.android.com/studio/debug/dev-options "Android Developers — Configure on-device developer options"
[2]: https://help.airdroid.com/hc/en-us/articles/360045661674-How-to-Enable-USB-debugging-on-Vivo "AirDroid Help Center — How to Enable USB debugging on Vivo"
[3]: https://firebase.google.com/docs/test-lab/usage-quotas-pricing "Firebase Test Lab — Usage levels, quotas, and pricing"
[4]: https://firebase.google.com/docs/test-lab/android/instrumentation-test "Firebase Test Lab — Run instrumentation tests"
