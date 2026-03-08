# CI Pipeline Overview

## Trigger

The pipeline runs **only on Pull Requests** targeting the `master` branch. It does **not** run on direct pushes or merges — only when a PR is opened, updated, or reopened.

---

## Pipeline Structure

The pipeline consists of **3 sequential jobs**:

```
build → test → publish-report
```

Each job runs on a fresh `ubuntu-latest` runner with **Java 21 (Temurin)** and Maven dependency caching enabled.

---

## Job 1: Build and Validate

**Purpose:** Compile both modules (`app` and `tests`) and enforce code style.

| Step | What it does |
|------|-------------|
| Checkout code | Clones the repository |
| Set up Java 21 | Installs Temurin JDK 21 with Maven cache |
| Build and Validate with Checkstyle | Runs `mvn clean install -DskipTests` — compiles code, packages JARs, and runs the Checkstyle plugin (bound to `validate` phase) against both `app` and `tests` modules |
| Upload Checkstyle Reports | Uploads `checkstyle-result.xml` from both modules as artifacts (kept for 30 days). Runs even if the build fails (`if: always()`) |

**Key detail:** Tests are skipped here (`-DskipTests`) — this job focuses purely on compilation and code style validation.

---

## Job 2: Run Component Tests

**Purpose:** Execute the Spring Boot component tests from the `tests` module.

**Depends on:** `build` (runs only if build succeeds)

| Step | What it does |
|------|-------------|
| Checkout code | Fresh clone of the repository |
| Set up Java 21 | Installs JDK with cached Maven dependencies |
| Run Component Tests | Runs `mvn test` in the `tests/` directory — this starts the Spring Boot app with `@SpringBootTest(RANDOM_PORT)`, H2 in-memory DB, and executes all JUnit 5 tests using REST Assured |
| Upload Allure Results | Uploads raw Allure JSON results from `tests/target/allure-results/` (kept for 30 days) |
| Upload Surefire Reports | Uploads Maven Surefire XML reports from `tests/target/surefire-reports/` (kept for 30 days) |

**Key detail:** Both artifact uploads run with `if: always()`, so test results are preserved even when tests fail.

---

## Job 3: Publish Allure Report

**Purpose:** Generate an interactive HTML Allure Report and deploy it to GitHub Pages.

**Depends on:** `test` (runs regardless of test outcome thanks to `if: always()`)

| Step | What it does |
|------|-------------|
| Checkout code | Fresh clone |
| Download Allure Results | Downloads the `allure-results` artifact produced by the test job |
| Load Allure Report History | Clones the `gh-pages` branch to retrieve previous report history (for trend charts). Gracefully handles the case when the branch doesn't exist yet |
| Generate Allure Report | Downloads Allure CLI 2.32.0, copies history from previous runs, generates the HTML report into `allure-report/` |
| Deploy to GitHub Pages | Pushes the generated report to the `gh-pages` branch under `pr-<number>/` folder. Each PR gets its own isolated report |
| Add link to Job Summary | Adds an Allure Report link to the GitHub Actions job summary page |
| Post PR Comment | Posts (or updates) a bot comment on the PR with a direct link to the Allure Report |

---

## How to See the Results on GitHub

### 1. Allure Report (Interactive HTML)

After the pipeline completes, you can access the Allure Report in **three ways**:

- **PR Comment:** A bot comment will appear on your Pull Request with a clickable link:
  ```
  📊 Allure Report
  🔗 Open Allure Report
  ```
  The link format: `https://ivankuleshin.github.io/VideoGameDB/pr-<PR_NUMBER>/`

- **Job Summary:** Go to the **Actions** tab → select the workflow run → scroll down to the **Summary** section. The Allure Report link is displayed there.

- **Direct URL:** Navigate to `https://ivankuleshin.github.io/VideoGameDB/pr-<PR_NUMBER>/` replacing `<PR_NUMBER>` with your PR number.

### 2. Pipeline Status & Logs

- Go to the **Actions** tab in your GitHub repository.
- Select the latest workflow run for your PR.
- Click on any job (`Build and Validate`, `Run Component Tests`, `Publish Allure Report`) to see step-by-step logs.

### 3. Downloadable Artifacts

On the workflow run summary page, scroll to the **Artifacts** section to download:

| Artifact | Contents |
|----------|----------|
| `checkstyle-reports` | Checkstyle XML results for both `app` and `tests` modules |
| `allure-results` | Raw Allure JSON data |
| `surefire-reports` | Maven Surefire XML test reports |

All artifacts are retained for **30 days**.

### 4. PR Status Checks

Each job appears as a **status check** on the Pull Request. You'll see green ✅ or red ❌ indicators next to:
- `Build and Validate`
- `Run Component Tests`
- `Publish Allure Report`

---

## Prerequisites for GitHub Pages

For the Allure Report deployment to work, ensure:

1. **GitHub Pages** is enabled in **Settings → Pages** with source set to `gh-pages` branch.
2. The `GITHUB_TOKEN` has `contents: write` and `pull-requests: write` permissions (already configured in the workflow).

