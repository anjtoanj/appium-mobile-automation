# ApiDemos E2E — Appium + Java + TestNG + Cucumber

Interview Reference: https://claude.ai/code/artifact/32917098-b7d5-434c-8e75-64c815fe3d93

Mobile test automation suite for the **Appium ApiDemos** app (`io.appium.android.apis`),
which lives in the parent folder of this project.

| Layer    | Choice                                                |
| -------- | ----------------------------------------------------- |
| Driver   | Appium Java client 10.1.1 (UiAutomator2)              |
| Language | Java 17                                               |
| Build    | Maven                                                 |
| Runner   | TestNG 7.12.0 via Surefire                            |
| BDD      | Cucumber-JVM 7.34.7 (`cucumber-testng`)               |
| Design   | Page Object Model with PageFactory (`@AndroidFindBy`) |

---

## Prerequisites

1. **JDK 17+** — `JAVA_HOME` set. (The app build already needs this.)
2. **Android SDK** — `ANDROID_HOME` set, `adb` on the `PATH`.
3. **Appium 2 server** running on `http://127.0.0.1:4723`:
   ```
   npm i -g appium
   appium driver install uiautomator2
   appium
   ```
4. **An emulator or device** booted and visible to `adb devices`.
5. **ApiDemos installed** on it:
   ```powershell
   cd ..
   .\build-and-install.ps1
   ```

---

## Running

```bash
mvn test                                    # every scenario
mvn test -Dcucumber.filter.tags="@smoke"    # smoke only
mvn test -Dcucumber.filter.tags="@preferences and not @wip"
mvn test -Ddevice.name=emulator-5554        # override any config value
```

Reports land in `target/cucumber-reports/` — `cucumber.html` is the readable one.
Failed scenarios attach a screenshot automatically.

---

## VS Code setup

The editor config lives in `tools/vscode/` (version-controlled) and is installed
into `.vscode/` by:

```powershell
.\setup-vscode.ps1
```

Existing files are kept unless you pass `-Force`.

That gives you step navigation (Ctrl+click a Gherkin step to reach its Java
method), debug configurations, and `mvn test` tasks.

**Open the `e2e` folder as the VS Code workspace root**, not the parent repo.
The Cucumber globs are relative to the workspace root, and the Java extension
gets confused trying to import the Android Gradle project alongside this one.

Install the recommended extensions when prompted, then
Ctrl+Shift+P -> _Developer: Reload Window_.

---

## Configuration

Everything lives in `src/test/resources/config/config.properties`. **Any key can be
overridden with `-D` on the command line**, which is what CI should do rather than
editing the file.

| Key                            | Default                                | Notes                                                                                                                     |
| ------------------------------ | -------------------------------------- | ------------------------------------------------------------------------------------------------------------------------- |
| `appium.server.url`            | `http://127.0.0.1:4723`                | Appium 2 base URL                                                                                                         |
| `device.name`                  | `Android Emulator`                     |                                                                                                                           |
| `device.udid`                  | _(blank)_                              | Pin to a serial from `adb devices`                                                                                        |
| `app.package` / `app.activity` | `io.appium.android.apis` / `.ApiDemos` |                                                                                                                           |
| `app.path`                     | _(blank)_                              | Set to an APK path to reinstall each run; blank drives the installed copy                                                 |
| `no.reset`                     | `true`                                 |                                                                                                                           |
| `element.timeout`              | `15`                                   | Seconds, used for every explicit wait                                                                                     |
| `new.command.timeout`          | `120`                                  | Seconds. Raise it when debugging - Appium kills a session that is idle this long, including while you sit on a breakpoint |
| `close.app.after.scenario`     | `true`                                 | Set false to leave the app on screen after a scenario                                                                     |

---

## Layout

```
e2e/
├── pom.xml
├── testng.xml                        Surefire entry point → the Cucumber runner
├── setup-vscode.ps1                  Installs tools/vscode/* into .vscode/
├── tools/vscode/                     Version-controlled editor config
└── src/test/
    ├── java/com/apidemos/e2e/
    │   ├── config/Config.java        config.properties + -D overrides
    │   ├── core/
    │   │   ├── DriverFactory.java    UiAutomator2Options → AndroidDriver
    │   │   ├── DriverManager.java    ThreadLocal session holder
    │   │   ├── AppLauncher.java      terminate / clear / activate the app
    │   │   └── Diagnostics.java      dumps UI XML + PNG on failure
    │   ├── pages/
    │   │   ├── BasePage.java         waits, scroll-to-text, tap helpers
    │   │   ├── SampleListPage.java   any ApiDemos list screen
    │   │   ├── HomePage.java         the launch screen + its categories
    │   │   ├── PreferenceDependenciesPage.java
    │   │   └── AlertDialogsPage.java
    │   ├── steps/                    Hooks + one class per feature
    │   └── runners/CucumberTestNGRunner.java
    └── resources/
        ├── config/config.properties
        └── features/*.feature
```

**Why one session per scenario.** `Hooks` creates and quits a driver around every
scenario, so no scenario can inherit UI state from the one before it. It costs a few
seconds per scenario; if that becomes the bottleneck, move session creation to
`@BeforeClass` and reset the app between scenarios instead.

**Why pages are constructed inside steps.** Cucumber does not guarantee that glue
classes are instantiated after the `@Before` hook, so page objects are created lazily
via a `page()` helper once the driver definitely exists. PageFactory proxies are lazy,
so this is cheap.

---

## Coverage

| Feature                   | Screen                                  | What it proves                                                                                         |
| ------------------------- | --------------------------------------- | ------------------------------------------------------------------------------------------------------ |
| `home_navigation`         | Home list                               | All 11 categories render; a category opens and Back returns                                            |
| `preference_dependencies` | Preference → 3. Preference dependencies | A dependent preference is disabled until its parent checkbox is ticked, and the entered value persists |
| `alert_dialogs`           | App → Alert Dialogs                     | A two-button dialog opens, exposes OK/Cancel, and dismisses                                            |

Every string these tests assert on was read out of the app's own
`AndroidManifest.xml` and `res/values/strings.xml`, not guessed — the category list
is the set of activities registered under the `SAMPLE_CODE` intent category.

---

## Verified on a real device

Run against a Pixel emulator, Android 11 (API 30), uiautomator2 2.45.1 - 6/6 scenarios pass.

Two things this run settled:

1. **Reading `enabled` off the preference title works.** The "WiFi settings"
   `TextView` really does report `enabled=false` while its dependency is unmet,
   so `PreferenceDependenciesPage.isEnabled(...)` is sound. No ancestor XPath needed.
2. **Preferences persist between scenarios.** Ticking a checkbox writes to
   SharedPreferences, and neither `noReset: true` nor terminating the app clears
   it. Two consequences, both handled:
   - `AppLauncher` calls `mobile: clearApp` before activating, so every scenario
     starts from empty app data.
   - `setChecked(title, desired)` reads the current state and taps only when it
     needs to change. A tap on a persisted preference is a _toggle_, not a _set_ -
     writing the step the naive way makes results depend on execution order and
     on what the previous run left behind.

The second point is the one to remember when adding screens: any step that
changes persisted state must assert or establish the state it needs, not assume it.

## Debugging

- Failures write `target/diagnostics/<scenario>-page.xml` (full UI hierarchy) and
  `-screen.png`. Look at the PNG first; grep the XML for the text or id you expected.
- Run the Appium server with a log the whole team can read:
  `appium --log-level debug --log ../appium.log`
- `Hooks.beforeEachStep` exists purely as a breakpoint anchor: a breakpoint on its
  counter line pauses before **every** Gherkin step, which is as close as Cucumber
  gets to stepping through a feature file.
- Raise `new.command.timeout` when debugging, or Appium will drop the session
  while you sit on a breakpoint. The launch configs already do this.

---

## Next steps

- Tag scenarios and wire `@smoke` into CI.
- Turn on parallel scenarios (`@DataProvider(parallel = true)`) once you have more
  than one device — the driver layer is already `ThreadLocal`.
- Add an `ExtentReports` or Allure adapter if you want richer reporting than the
  built-in Cucumber HTML.
