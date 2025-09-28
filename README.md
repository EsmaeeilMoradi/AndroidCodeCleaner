# 🤖 AndroidCodeCleaner

**Simple, yet powerful Kotlin Script to identify dead resources and unnecessary files in Android projects.**

---

## 🎯 The Problem: Silent Technical Debt
As a **Senior Android Engineer** specializing in architectural health, I know that **Technical Debt** isn't just about messy code—it's about slow build times, large APK size, and decreased team productivity.

The first, easiest, and often overlooked step in cleaning up this debt is removing **dead resources** (unused drawables, old icons, forgotten layouts, and XML files) that silently bloat your codebase.

> 💡 **For a deeper dive into the cost of this problem, see my analysis on the Uber Driver App's architectural overhaul:** [Lessons from the Uber Driver App Rewrite](https://www.linkedin.com/posts/esmaeeil-moradi700_technical-debt-uber-activity-7376190784598994946-aCOl?utm_source=share&utm_medium=member_desktop&rcm=ACoAADWuvhABqcAOz8yTpJZ-60zMobpn48jMb6w)

---

## 🚀 Real-World Proof: The Unstoppable Wallet Test
To prove its effectiveness, we ran `AndroidCodeCleaner` on the highly reputable and well-maintained open-source project, **[Unstoppable Wallet](https://github.com/horizontalsystems/unstoppable-wallet-android)**.

| Metric | Result |
| :--- | :--- |
| Total Resource Files Scanned | **351** files |
| Total Code/XML Files Scanned | **1,497** files |
| **Dead Resources Identified** | **17 critical resources!** |

**Conclusion:** This tool found **17 forgotten resources** ready for deletion, even in a well-managed codebase. This proves its precision and immediate value.

---

## ⚙️ How to Use (30-Second Setup)
`AndroidCodeCleaner` is built on Kotlin Script, requiring minimal setup and no complex Gradle configuration.

### Prerequisites
Make sure you have the [Kotlin command-line compiler](https://kotlinlang.org/docs/command-line.html) installed on your system.

### Running the Script
1.  Place the `scripts/find_unused_resources.kts` file in the root of your Android project (the same level as your `app/` folder).
2.  Run the script directly from your terminal:

```bash
kotlinc -script scripts/find_unused_resources.kts
```
**Note:** This tool only **reports** files; it **does not delete** anything. Manual review and deletion are required.

---

## 🤝 Support Development (Crypto Donations)
This tool is built and maintained **completely free** for the Android community by an experienced architect. If `AndroidCodeCleaner` saved you time and helped clean up your project, please consider supporting its ongoing development.

Your support helps fund the next big features, such as:
* Advanced dead code detection (finding unreachable Kotlin/Java code).
* Automatic deletion commands.

| Crypto Currency | Address | QR Code |
| :--- | :--- | :--- |
| **Ethereum (ETH)** | `0x2c6497d4492cdBAbB38D226353d5C656d4D71eB8` | **<img src="assets/eth_qrcode.png" width="100"/>** |
| **Pay via Trust Wallet Link:** | <a href="https://link.trustwallet.com/send?coin=60&address=0x2c6497d4492cdBAbB38D226353d5C656d4D71eB8">Click to Donate</a> | - |

Thank you for your support!

---

## 📜 License & Author
This project is licensed under the **[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)**.
