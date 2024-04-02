package uk.ac.ed.inf.intellijbeginnerlinter.controlstructinspects

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiWhileStatement
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.annotations.NotNull

/**
 * A customized inspection that checks if or how many of the "while loop" is used in the function.
 * The inspection can be configured to apply this inspection to all functions or only to specified functions.
 * Ihe number of "while loop" allowed in each function can also be specified by the user.
 *
 * @author Zhuohang (Stephen) Shen <s2023501@ed.ac.uk>
 */
class WhileLoopUsageInspection : AbstractBaseJavaLocalInspectionTool() {

    // Declare the variable for the inspection specifications provided by the user.
    var specs: String = ""

    /**
     * Override the buildVisitor function to build a Java element visitor that provides
     * the new while loop usage inspection.
     *
     * @param holder The container where the new Java element visitor will register problems it found.
     * @param isOnTheFly Boolean value indicates if the inspection is running in 'on-the-fly' mode.
     * @return A JavaElementVisitor that will inspect the usage of while loops in Java functions.
     */
    @NotNull
    override fun buildVisitor(@NotNull holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {

            // Declare the mutable map for counting the number of "while loop"s used in each function.
            private val methodWhileLoopCounts = mutableMapOf<PsiMethod, Int>()

            // Declare the mutable map for storing the number of "while loop"s allowed in each function.
            private var specsMap = mutableMapOf<String, Int>()

            // One helper function to parse the specs provided by the user in string to mutable map.
            private fun specsParser(): MutableMap<String, Int> {

                // If no spec is provided, no if statement is allowed.
                if (specs.isEmpty()){
                    return mutableMapOf<String, Int>()  // Return the map directly.
                }

                // Split the specs string to elements by ","
                // Example: "methodA: 1, methodB: 1"
                val specElements = specs.split(",")

                // Declare a mutable map for the results
                var results = mutableMapOf<String, Int>()

                // Process each pair of the specs provided by the user.
                // Example: "methodA: 1"
                for (pair in specElements) {
                    try {
                        // Remove any leading and trailing whitespace and split each pair with ":".
                        val parts = pair.trim().split(":").map { it.trim() }

                        // Check if the size of the pair is two and parse the pair to the map.
                        if (parts.size == 2) {
                            val (key, value) = parts
                            results[key] = value.toInt()  // Exception is handled below.
                        }

                        // Check if the size of the pair is one and parse the pair to the map with infinite allowance.
                        else if (parts.size == 1) {
                            results[parts[0]] = -1  // -1 represents infinite allowance.
                        }

                        // In cases of incorrect formats of the pair.
                        else {
                            println("Warning: Incorrect format for pair '$pair'")
                            results = mutableMapOf<String, Int>()  // Return an empty map directly.
                            break
                        }
                    } catch (e: NumberFormatException) {  // For the exception to converting integer.
                        println("Error: Value for '$pair' is not a valid integer.")
                        results = mutableMapOf<String, Int>()  // Return an empty map directly.
                    }
                }
                return results
            }

            // Visit each while loop visit in the java code
            override fun visitWhileStatement(statement: PsiWhileStatement) {

                // Call the visitWhileStatement function from the super class to maintain the original behavior of it.
                super.visitWhileStatement(statement)

                // Check if the specs string has been parsed before.
                if (specsMap.isEmpty()) {
                    specsMap = specsParser()
                }

                // Find the java function which the while loop belongs to or return directly for no method.
                val method = PsiTreeUtil.getParentOfType(statement, PsiMethod::class.java) ?: return

                // Update the number of while loops used in the method.
                val count = methodWhileLoopCounts.getOrDefault(method, 0) + 1  // 0 as the initial values.
                methodWhileLoopCounts[method] = count

                // Find the number of while loops allowed in the method.
                val allowance = specsMap.getOrDefault(method.name, 0)  // 0 as the default values.

                // When the count is larger than the allowance and the allowance is not infinite.
                if (count > allowance && allowance != -1) {
                    holder.registerProblem(
                        statement,
                        "Avoid using 'while' loops within Java methods '${method.name}'.",
                        ProblemHighlightType.WARNING
                    )
                }
            }
        }
    }
}
