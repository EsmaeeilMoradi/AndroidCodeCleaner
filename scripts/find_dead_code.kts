import java.io.File

// --- Configuration ---
val LIFECYCLE_METHODS = setOf(
    "onCreate", "onStart", "onResume", "onPause", "onStop", "onDestroy",
    "onBind", "onAttach", "onDetach", "onCleared", "onViewCreated", "onActivityCreated",
    "main", "onGlobalLayout", "doWork", "getTheme", "provideGlance" // Added Android Infrastructure methods
)

val EXCLUDED_ANNOTATIONS = setOf(
    "@Inject", "@Module", "@Provides", "@Binds", "@JvmStatic", "@JvmField",
    "@SerializedName", "@Keep", "@BindingAdapter", "@Provides", "@Before", "@Test",
    "@Composable" // Exclude Composable functions globally
)

// List of filenames/paths that use heavy reflection/system calls and should be ignored for declarations
val INFRASTRUCTURE_FILES = setOf(
    "DatabaseConverters.kt", // Room Reflection
    "Worker.kt", "Delegate.kt", "Host.kt", // WorkManager / Delegation / Navigation
    "Widget.kt" // Widget infrastructure
)

val CLASS_DECLARATION_REGEX = Regex("""\b(?:class|interface|object)\s+(\w+)""", setOf(RegexOption.MULTILINE))
val FUNCTION_DECLARATION_REGEX = Regex("""\b(?:fun|private fun|public fun|internal fun|protected fun)\s+([\w<>]+)\s*\(.*?\)\s*[:\{]""", setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))

val declarations = mutableMapOf<String, File>()
var continueExecution = true
var moduleDirectory: File? = null

// --- Flow Manager Function ---
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

// --- Helper Functions (Fix applied here) ---

fun scanFileForDeclarations(file: File) {
    val content = file.readText()
    val isAbstractOrInterface = content.contains(Regex("""\b(?:interface|abstract class)\b"""))

    // New Infrastructure Filter: Skip files known to use reflection
    if (INFRASTRUCTURE_FILES.any { file.name.contains(it) }) {
        return
    }

    val allMatches = CLASS_DECLARATION_REGEX.findAll(content) + FUNCTION_DECLARATION_REGEX.findAll(content)

    for (match in allMatches) {
        val name = match.groups[1]?.value ?: continue

        // 1. Exclude Android Lifecycle, Infra Methods, and Compose Previews (by name)
        if (LIFECYCLE_METHODS.contains(name) || name.startsWith("Preview_") || name.startsWith("Preview") || name.endsWith("Preview")) {
            continue
        }

        // 2. Exclude common methods in interfaces/abstract classes
        if (isAbstractOrInterface && FUNCTION_DECLARATION_REGEX.matches(match.value)) {
            continue
        }

        // 3. Exclude elements near common annotations (D.I., Compose, etc.)
        // This is safe because we now cap the search window
        val endBound = minOf(content.length, match.range.endInclusive + 50)
        val contextAroundMatch = content.substring(maxOf(0, match.range.start - 50), endBound)

        if (EXCLUDED_ANNOTATIONS.any { contextAroundMatch.contains(it) }) {
            continue
        }

        declarations[name] = file
    }
}

fun findReferences(identifier: String, allCodeContent: String): Int {
    val pattern = Regex.escape(identifier)
    val regex = Regex("""\b$pattern\b""")

    // -1 because the declaration itself counts as one reference.
    return regex.findAll(allCodeContent).count() - 1
}

fun showSpinner(index: Int, total: Int) {
    val spinnerChars = listOf("\\", "|", "/", "-")
    val percent = if (total > 0) ((index.toDouble() / total) * 100).toInt() else 0
    val currentSpinner = spinnerChars[index % spinnerChars.size]

    // Use \r to return the cursor to the start of the line
    print("\rProcessing declarations: $currentSpinner ($index / $total | $percent%)")

    // CRITICAL: Pause for 10ms to make the spinner visible in the terminal
    // This allows the terminal to update the output for each character.
    try {
        Thread.sleep(10)
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
    }
}

// --- Main Execution Logic ---

// 1. Get and Validate Path
promptAndValidatePath()

if (continueExecution) {
    val actualModuleDirectory = moduleDirectory!!

    val sourceFolders = listOf(
        File(actualModuleDirectory, "src/main/kotlin"),
        File(actualModuleDirectory, "src/main/java")
    ).filter { it.exists() }

    if (sourceFolders.isEmpty()) {
        println("Error: Could not find 'src/main/kotlin' or 'src/main/java' inside the module.")
        continueExecution = false
    }

    if (continueExecution) {
        // 2. Collect all files and full code content
        val allSourceFiles = sourceFolders.flatMap { folder ->
            folder.walkTopDown().filter { it.isFile && (it.extension == "kt" || it.extension == "java") }
        }

        println("Scanning ${allSourceFiles.size} source files...")
        val allCodeContent = allSourceFiles.joinToString("\n") { it.readText() }

        // 3. Scan all files for declarations
        allSourceFiles.forEach { scanFileForDeclarations(it) }
        println("Found ${declarations.size} potential declarations to check.")

        // 4. Check usage for each declaration
        val deadCode = mutableMapOf<String, File>()
        val totalDeclarations = declarations.size
        var processedCount = 0

        declarations.forEach { (name, file) ->

            showSpinner(processedCount, totalDeclarations)
            val referencesCount = findReferences(name, allCodeContent)

            if (referencesCount == 0) {
                deadCode[name] = file
            }
            processedCount++
        }
        // Clear the spinner line before printing the final report
        if (processedCount > 0) {
            println("\rProcessing complete!                               ")
        }


        // 5. Final Report
        println("\n--- DEAD CODE REPORT for ${actualModuleDirectory.name} ---")

        if (deadCode.isNotEmpty()) {
            deadCode.forEach { (name, file) ->
                println("DEAD: '$name' declared in ${file.relativeTo(actualModuleDirectory.parentFile)}")
            }
            println("----------------------------------------------")
            println("Found ${deadCode.size} potentially unused declarations.")
        } else {
            println("Congratulations! No likely dead code found in this module.")
        }
    }
}

println("\nNOTE: This is a static analysis based on identifier matching. Manual review is required before deletion.")

