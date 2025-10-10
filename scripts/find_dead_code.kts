import java.io.File
import kotlin.system.exitProcess

// --- Configuration (Remains the same) ---
val LIFECYCLE_METHODS = setOf(
                "onCreate", "onStart", "onResume", "onPause", "onStop", "onDestroy",
            "onBind", "onAttach", "onDetach", "onCleared", "onViewCreated", "onActivityCreated",
            "main", "onGlobalLayout", "doWork", "getTheme", "provideGlance"
)

val EXCLUDED_ANNOTATIONS = setOf(
                "@Inject", "@Module", "@Provides", "@Binds", "@JvmStatic", "@JvmField",
            "@SerializedName", "@Keep", "@BindingAdapter", "@Provides", "@Before", "@Test",
            "@Composable"
)

val INFRASTRUCTURE_FILES = setOf(
                "DatabaseConverters.kt",
            "Worker.kt", "Delegate.kt", "Host.kt",
            "Widget.kt"
)

val CLASS_DECLARATION_REGEX = Regex("""\b(?:class|interface|object)\s+(\w+)""", setOf(RegexOption.MULTILINE))
val FUNCTION_DECLARATION_REGEX = Regex("""\b(?:fun|private fun|public fun|internal fun|protected fun)\s+([\w<>]+)\s*\(.*?\)\s*[:\{]""", setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))

val declarations = mutableMapOf<String, File>()
var continueExecution = true
var moduleDirectory: File? = null

// --- PRO FEATURE: JSON Report Generator (NEW FUNCTION) ---
fun generateJsonReport(deadCode: Map<String, File>, actualModuleDirectory: File, outputFile: String) {
    val jsonContent = StringBuilder()
    jsonContent.append("[\n")

    var isFirst = true
    deadCode.forEach { (name, file) ->
        if (!isFirst) {
            jsonContent.append(",\n")
        }

        val relativePath = file.relativeTo(actualModuleDirectory.parentFile).path.replace("\\", "/")

        jsonContent.append("""
    {
        "declaration_name": "$name",
        "file_path": "$relativePath"
    }
        """.trimIndent())

        isFirst = false
    }

    jsonContent.append("\n]")

    try {
        File(outputFile).writeText(jsonContent.toString())
        println("\nSUCCESS: PRO JSON report saved to $outputFile")
    } catch (e: Exception) {
        println("\nERROR: Could not write PRO JSON report to $outputFile. Reason: ${e.message}")
    }
}


// --- Flow Manager Function (Remains the same, but now used conditionally) ---
fun promptAndValidatePath() {
                print("Enter the relative path to the module directory (e.g., app/ or ../app/): ")
                val modulePathInput = readLine()?.trim()

                if (modulePathInput.isNullOrBlank()) {
                                println("Error: Module path cannot be empty.")
                                continueExecution = false
                                return
                    }

                val moduleFile = File(modulePathInput)
                if (!moduleFile.exists() || !moduleFile.isDirectory) {
                                println("Error: Directory '$modulePathInput' not found. Ensure you use the correct relative path.")
                                continueExecution = false
                                return
                    }
                moduleDirectory = moduleFile
}


// --- Helper Functions (Your original functions, added for completeness) ---
fun scanFileForDeclarations(file: File) {
                val content = file.readText()
                val isAbstractOrInterface = content.contains(Regex("""\b(?:interface|abstract class)\b"""))

                if (INFRASTRUCTURE_FILES.any { file.name.contains(it) }) return

                val allMatches = CLASS_DECLARATION_REGEX.findAll(content) + FUNCTION_DECLARATION_REGEX.findAll(content)

                for (match in allMatches) {
                                val name = match.groups[1]?.value ?: continue

                                if (LIFECYCLE_METHODS.contains(name) || name.startsWith("Preview_") || name.startsWith("Preview") || name.endsWith("Preview")) continue
                                if (isAbstractOrInterface && FUNCTION_DECLARATION_REGEX.matches(match.value)) continue

                                val endBound = minOf(content.length, match.range.endInclusive + 50)
                                val contextAroundMatch = content.substring(maxOf(0, match.range.start - 50), endBound)

                                if (EXCLUDED_ANNOTATIONS.any { contextAroundMatch.contains(it) }) continue

                                declarations[name] = file
                    }
}

fun findReferences(identifier: String, allCodeContent: String): Int {
                val pattern = Regex.escape(identifier)
                val regex = Regex("""\b$pattern\b""")
                return regex.findAll(allCodeContent).count() - 1
}

fun showSpinner(index: Int, total: Int) {
                val spinnerChars = listOf("\\", "|", "/", "-")
                val percent = if (total > 0) ((index.toDouble() / total) * 100).toInt() else 0
                val currentSpinner = spinnerChars[index % spinnerChars.size]

                print("\rProcessing declarations: $currentSpinner ($index / $total | $percent%)")

                try {
                                Thread.sleep(10)
                    } catch (e: InterruptedException) {
                                Thread.currentThread().interrupt()
                    }
}


// --- Main Execution Logic (Modified to handle both interactive and Pro modes) ---

val scriptArgs = this.args
val isCiMode = scriptArgs.contains("--ci-mode")
val isJsonMode = scriptArgs.contains("--json-output")
val jsonOutputIndex = scriptArgs.indexOf("--json-output")
val jsonOutputFile = if (isJsonMode && jsonOutputIndex + 1 < scriptArgs.size) scriptArgs[jsonOutputIndex + 1] else null

val modulePathArgIndex = scriptArgs.indexOfFirst { !it.startsWith("--") }
val modulePathArg = if (modulePathArgIndex != -1) scriptArgs[modulePathArgIndex] else null


if (modulePathArg != null) {
    // 1. NON-INTERACTIVE Mode (CI/CD or path provided via CLI)
    moduleDirectory = File(modulePathArg)
    if (!moduleDirectory!!.exists() || !moduleDirectory!!.isDirectory) {
        println("Error: Directory '${modulePathArg}' not found.")
        exitProcess(0)
    }
} else {
    // 2. INTERACTIVE Mode (No arguments provided)
    promptAndValidatePath()
}


if (continueExecution && moduleDirectory != null) {
                val actualModuleDirectory = moduleDirectory!!

                val sourceFolders = listOf(
                            File(actualModuleDirectory, "src/main/kotlin"),
                            File(actualModuleDirectory, "src/main/java")
                ).filter { it.exists() }

                if (sourceFolders.isEmpty()) {
                                println("Error: Could not find 'src/main/kotlin' or 'src/main/java' inside the module.")
                                exitProcess(0) // Use exitProcess(0) instead of continueExecution = false
                    }

    // (Code collection, scanning, and processing logic remain the same)

                val allSourceFiles = sourceFolders.flatMap { folder ->
                                folder.walkTopDown().filter { it.isFile && (it.extension == "kt" || it.extension == "java") }
                    }

                println("Scanning ${allSourceFiles.size} source files...")
                val allCodeContent = allSourceFiles.joinToString("\n") { it.readText() }

                allSourceFiles.forEach { scanFileForDeclarations(it) }
                println("Found ${declarations.size} potential declarations to check.")

                val deadCode = mutableMapOf<String, File>()
                val totalDeclarations = declarations.size
                var processedCount = 0

                declarations.forEach { (name, file) ->
        if (!isCiMode) {
                                                showSpinner(processedCount, totalDeclarations)
        }
                                val referencesCount = findReferences(name, allCodeContent)

                                if (referencesCount == 0) {
                                            deadCode[name] = file
                                }
                                processedCount++
                    }
                if (!isCiMode && processedCount > 0) {
                                println("\rProcessing complete!                                                                                               ")
                    }


                // 5. Final Report
                println("\n--- DEAD CODE REPORT for ${actualModuleDirectory.name} ---")

                if (deadCode.isNotEmpty()) {
                                deadCode.forEach { (name, file) ->
                                            println("DEAD: '$name' declared in ${file.relativeTo(actualModuleDirectory.parentFile)}")
                                }
                                println("----------------------------------------------")
                                println("Found ${deadCode.size} potentially unused declarations.")
                               
        // PRO FEATURE 2: JSON Report
        if (isJsonMode && jsonOutputFile != null) {
            generateJsonReport(deadCode, actualModuleDirectory, jsonOutputFile)
        }

        // PRO FEATURE 1: CI/CD FAILURE
        if (isCiMode) {
            println("\nCI/CD FAILURE: Found ${deadCode.size} new dead declarations. Aborting merge.")
            exitProcess(1) // Non-zero exit code to fail the CI build
        }
                    } else {
                                println("Congratulations! No likely dead code found in this module.")
                    }
}

println("\nNOTE: This is a static analysis based on identifier matching. Manual review is required before deletion.")
