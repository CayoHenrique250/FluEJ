# FluEJ 

FluEJ was created to solve one of the greatest vulnerabilities in the Junior Enterprise Movement (MEJ): the continuous loss of organizational knowledge caused by high turnover rates among members and managers. Due to the university cycle, the constant transition of teams causes documented processes, Key Performance Indicators (KPIs), and project histories to be lost in informal handovers.

To mitigate this issue, this project delivers an interactive management platform and initial onboarding guide. The tool ensures long-term organizational and quality standards within Junior Enterprises (JEs), enabling member production tracking, onboarding/offboarding management, strategic reporting, and the establishment of a long-term knowledge base.

---

## Environment Prerequisites

Before getting started, make sure you have the following software installed on your machine:

* **Git**
* **JDK 25**
* **Android SDK** (API 37 / Min SDK 24)
* **IDE:** [Android Studio](https://developer.android.com/studio) or [VS Code](https://code.visualstudio.com/)

---

## 1. Cloning the Repository

```bash
git clone https://github.com/CayoHenrique250/FluEJ.git
cd FluEJ

```

---

## 2. How to Run the Project

### Option A: Android Studio

1. Open Android Studio and select **Open**.
2. Navigate to the project folder and confirm.
3. Wait for the **Gradle Sync** to finish.
4. Verify in `Settings > Build Tools > Gradle` that the **Gradle JDK** points to version **25**.
5. Select the target device or emulator and click **Run** (`Shift + F10`).

---

### Option B: VS Code

1. Install the required extensions: **Kotlin**, **Extension Pack for Java**, and **Gradle for Java**.
2. Create a `local.properties` file in the root directory pointing to your local SDK:
```properties
sdk.dir=/your/local/path/to/android/sdk

```


3. With an active device or emulator connected, run the following command in the integrated terminal:
* **Windows:** `.\gradlew.bat installDebug`
* **macOS/Linux:** `./gradlew installDebug`



---

### Other Utility Commands (Gradle Wrapper)

* **Clean Build:**
* **Windows:** `.\gradlew.bat clean` | **macOS/Linux:** `./gradlew clean`


* **Generate Debug APK:**
* **Windows:** `.\gradlew.bat assembleDebug` | **macOS/Linux:** `./gradlew assembleDebug`


* **Run Unit Tests:**
* **Windows:** `.\gradlew.bat test` | **macOS/Linux:** `./gradlew test`



---

## Git & Collaboration Guidelines

To keep the codebase clean, maintainable, and consistent across the team, all contributors must follow these Git conventions.

---

### 1. Branch Naming Strategy

Create branches off the `main` branch using the following prefix conventions:

* `feat/short-description` — For new features (e.g., `feat/onboarding-screen`)
* `fix/short-description` — For bug fixes (e.g., `fix/login-crash`)
* `docs/short-description` — For documentation updates (e.g., `docs/update-readme`)
* `refactor/short-description` — Code restructuring without changing functionality (e.g., `refactor/viewmodel-logic`)
* `chore/short-description` — Build configuration, dependencies, or tool updates (e.g., `chore/gradle-update`)

---

### 2. Commit Message Standards

We follow the **Conventional Commits** specification. Commit messages must be written in English and structured as follows:

`<type>: <short description in present tense>`

#### **Allowed Commit Types:**

* **`feat`**: A new feature added to the application.
* *Example:* `feat: add member onboarding checklist component`


* **`fix`**: A bug fix.
* *Example:* `fix: resolve crash when generating PDF report`


* **`docs`**: Documentation-only changes.
* *Example:* `docs: update environment setup instructions`


* **`refactor`**: Code changes that neither fix a bug nor add a feature.
* *Example:* `refactor: optimize state management in dashboard`


* **`style`**: Formatting, missing semicolons, or code style adjustments (no logic change).
* *Example:* `style: apply Ktlint formatting rules`


* **`test`**: Adding missing tests or refactoring existing tests.
* *Example:* `test: add unit tests for member validation`


* **`chore`**: Updating build tasks, Gradle configs, or dependencies.
* *Example:* `chore: bump AGP to compatible Java 25 version`



---

### 3. Pull Request (PR) Naming & Process

#### **PR Title Format**

Structure PR titles using the `[TYPE] Description` convention:

* **Examples:**
* `[FEAT] Implement onboarding progress tracker`
* `[FIX] Fix state restoration issue on screen rotation`
* `[CHORE] Upgrade Kotlin Compose compiler extension`



#### **PR Submission Checklist**

Before requesting a code review, ensure you have:

* [ ] Successfully built the project locally (`./gradlew assembleDebug`).
* [ ] Run and passed all unit tests (`./gradlew test`).
* [ ] Applied proper formatting rules according to Kotlin style guidelines.
* [ ] Included screenshots or screen recordings for any UI (Jetpack Compose) changes.
* [ ] Assigned at least one team member for code review.

