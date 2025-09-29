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

## ⚙️ How to Use (30-Second Setup)
The tools are built on Kotlin Script, requiring minimal setup and no complex Gradle configuration.

### Prerequisites
Make sure you have the [Kotlin command-line compiler](https://kotlinlang.org/docs/command-line.html) installed on your system.

### 1. Resource Detector (find_unused_resources.kts)
Hunts for unused drawables, layouts, and XML files.

```bash
kotlinc -script scripts/find_unused_resources.kts
```
### 2. Dead Code Detector (find_dead_code.kts) - NEW!
Hunts for unused Kotlin/Java classes and functions using advanced static analysis.

```bash
kotlinc -script scripts/find_dead_code.kts
```
**Note:** Both tools only report items; they do not delete anything. Manual review is required.

---
## 💡 Roadmap to Pro
The current scripts provide powerful baseline analysis. The next version, **AndroidCodeCleaner Pro**, will be a **paid offering** that directly addresses the needs of professional teams:
* **Automated CI/CD Integration:** Direct integration with GitHub Actions/GitLab CI.
* **Advanced Dependency Analysis:** Accurate detection of code used via Reflection, Dagger/Hilt, and complex runtime structures.
* **Exportable Reports:** Detailed, exportable HTML/PDF reports for technical debt review meetings.


## 🤝 Support Development (Crypto Donations)
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
