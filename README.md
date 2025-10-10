# 🤖 AndroidCodeCleaner

**The essential Kotlin Script suite for Senior Android Engineers to eliminate Technical Debt, shrink APK size, and boost build speeds.**

---

## 🎯 The Problem: Invisible Technical Debt
As a **Senior Android Engineer** specializing in architectural health, I know that **Technical Debt** isn't just about messy code—it's about slow build times, large APK size, and decreased team productivity.

Cleaning up this debt requires hunting down both **dead resources** and **dead Kotlin/Java code** that silently bloat your codebase.

> 💡 **For a deeper dive into the cost of this problem, see my analysis on the Uber Driver App's architectural overhaul:** [Lessons from the Uber Driver App Rewrite](https://www.linkedin.com/posts/esmaeeil-moradi700_technical-debt-uber-activity-7376190784598994946-aCOl?utm_source=share&utm_medium=member_desktop&rcm=ACoAADWuvhABqcAOz8yTpJZ-60zMobpn48jMb6w)

---

## 🚀 Real-World Proof: The Unstoppable Wallet Test (V2 Update)
To prove the suite's effectiveness, we ran both detectors on the highly reputable and well-maintained open-source project, **[Unstoppable Wallet](https://github.com/horizontalsystems/unstoppable-wallet-android)**.

### **Results Summary**
| Detector | Metric | Result |
| :--- | :--- | :--- |
| **Resource Detector** | Dead Resources Identified | **17 critical resources!** |
| **Dead Code Detector** | Dead Declarations Found | **66 potentially unused classes/methods!** |

**Conclusion:** The **AndroidCodeCleaner** suite found a combined total of **83 items** ready for deletion in a professional codebase, proving its precision and immediate value.

---

## ⚙️ How to Use (Free & Pro Modes)

The tools are built on Kotlin Script, requiring minimal setup and no complex Gradle configuration. For the simplest execution, we recommend using the **`kotlin` runtime** instead of `kotlinc -script`.

### Prerequisites
Make sure you have the [Kotlin command-line compiler and runtime](https://kotlinlang.org/docs/command-line.html) installed on your system.

### Free Mode: Local Development

These core tools are **free to use** and excellent for quick, local checks. The script will prompt you for the module path.

#### 1. Resource Detector (find_unused_resources.kts)
Hunts for unused drawables, layouts, and XML files.

```bash
kotlin scripts/find_unused_resources.kts
```

### 2. Dead Code Detector (find_dead_code.kts) - NEW!
Hunts for unused Kotlin/Java classes and functions using advanced static analysis.

```bash
kotlinc -script scripts/find_dead_code.kts
```
**Note:** Both tools only report items; they do not delete anything. Manual review is required.

---
---
## 💎 AndroidCodeCleaner PRO

The **AndroidCodeCleaner Pro** features are designed for **professional engineering teams** that need **automation and standardized reporting** within a **CI/CD** environment.

**Pro features require a valid license key set as the `DEAD_CODE_LICENSE_KEY` environment variable.**

### PRO 1: CI/CD Gatekeeper 🛡️

**Why Pay?** This is the **only way** to automatically block merging new dead code into your main branch. It ensures architectural quality at the source.

The `--ci-mode` flag disables interactive prompts and causes the script to exit with a non-zero code (`1`) if any dead code is found, failing the build instantly.

```bash
# Set your license key first
export DEAD_CODE_LICENSE_KEY="YOUR_PURCHASED_KEY" 

# Run the Pro tool
kotlin scripts/find_dead_code.kts <MODULE_PATH> --ci-mode

# Example: kotlin scripts/find_dead_code.kts ../app/ --ci-mode
```
### PRO 2: JSON Reporting 📊

**Value for Teams:** Generate machine-readable output for integration with dashboards, custom reporting tools, or automated issue generation in Jira/GitHub Issues.

Use the `--json-output` flag to save the results instead of printing them to the console.

```bash
# Execute this command to see the power of JSON reports.
kotlin scripts/find_dead_code.kts <MODULE_PATH> --json-output <FILENAME>.json

# Example: kotlin scripts/find_dead_code.kts ../app/ --json-output dead_code_report.json
```
---
## 🤝 Next Step: Expert Consulting & Integration

If you have executed the $\text{Pro}$ commands and see the significant value in automating this process for your team, please contact me directly. The full power of $\text{CI/CD}$ integration is available through consultation.

I specialize in integrating architectural quality tools into professional $\text{CI/CD}$ pipelines to guarantee code health.

> **[📞 Contact Esmaeeil Moradi for Expert Consulting (LinkedIn)](https://www.linkedin.com/in/esmaeeil-moradi700/)**

### Support Development (Crypto Donations)
This tool is built and maintained **completely free** for the Android community by an experienced architect. If `AndroidCodeCleaner` saved you time and helped clean up your project, please consider supporting its ongoing development.

Your support helps fund the next big features, such as:
* Advanced dead code detection (finding unreachable Kotlin/Java code).
* Automatic deletion commands.

| Crypto Currency | Address | QR Code |
| :--- | :--- | :--- |
| **Ethereum (ETH)** | `0x2c6497d4492cdBAbB38D226353d5C656d4D71eB8` | **<img src="assets/eth_qrcode.jpg" width="100"/>** |
| **Pay via Trust Wallet Link:** | <a href="https://link.trustwallet.com/send?coin=60&address=0x2c6497d4492cdBAbB38D226353d5C656d4D71eB8">Click to Donate</a> | - |

Thank you for your support!

---

## 📜 License & Author

This project is licensed under the **[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)**.
