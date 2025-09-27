import java.io.File

var continueExecution = true
val projectPath = File("..") // Correctly sets path to the project root

// --- Step 1: Validate Project Path ---
if (!projectPath.exists() || !projectPath.isDirectory) {
    println("Error: Project path is invalid or does not exist.")
    continueExecution = false
}

if (continueExecution) {
    println("Scanning project at: ${projectPath.absolutePath}")

    // --- Step 2: Find all Resource Files ---
    val resFolder = File(projectPath, "app/src/main/res")
    if (!resFolder.exists() || !resFolder.isDirectory) {
        println("Error: 'app/src/main/res' folder not found.")
        continueExecution = false
    }

    if (continueExecution) {
        val allResourceFiles = resFolder.walkTopDown().filter { it.isFile }.toList()
        println("Total resource files found: ${allResourceFiles.size}")

        if (allResourceFiles.isEmpty()) {
            println("No resource files to check.")
            continueExecution = false
        }

        // --- Step 3: Find all Code Files (.kt and .xml) ---
        val mainFolder = File(projectPath, "app/src/main")
        if (!mainFolder.exists() || !mainFolder.isDirectory) {
            println("Error: 'app/src/main' folder not found.")
            continueExecution = false
        }

        if (continueExecution) {
            val allCodeFiles = mainFolder.walkTopDown()
                .filter { it.isFile && (it.extension == "kt" || it.extension == "xml") }
                .toList()

            println("Total code and XML files found for search: ${allCodeFiles.size}")

            if (allCodeFiles.isEmpty()) {
                println("No code files found for search.")
                // No need to set continueExecution to false here, as an empty search is still a result.
            } else {
                // Concatenate all code content for a single, fast search
                val allCodeContent = allCodeFiles.joinToString("\n") { it.readText() }

                // --- Step 4: Check for Unused Resources ---
                val unusedFiles = allResourceFiles.filter { resourceFile ->
                    val resourceName = resourceFile.nameWithoutExtension

                    // A smart filter to skip strings.xml files (False Positives)
                    val isStringsFileInValues = resourceFile.name == "strings.xml" && resourceFile.parentFile.name.startsWith("values")

                    if (isStringsFileInValues) {
                        // Skip reporting strings.xml files
                        false
                    } else {
                        !allCodeContent.contains(resourceName)
                    }
                }

                // --- Step 5: Final Report ---
                if (unusedFiles.isNotEmpty()) {
                    println("\n--- UNUSED FILES FOUND ---")
                    unusedFiles.forEach { file ->
                        println("${file.relativeTo(projectPath)}")
                    }
                    println("--------------------------")
                } else {
                    println("\nNo unused files found.")
                }
            }
        }
    }
}

println("\nNOTE: This tool only scans and reports files; it does not delete anything.")