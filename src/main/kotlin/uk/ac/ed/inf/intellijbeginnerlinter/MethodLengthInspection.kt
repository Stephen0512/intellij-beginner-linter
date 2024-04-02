package uk.ac.ed.inf.intellijbeginnerlinter

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiMethod
import org.jetbrains.annotations.NotNull


/**
 * A customized inspection that checks if the length of a function exceeds a specified limit.
 * The inspection can be configured to apply this inspection to all functions or only to specified functions.
 * Ihe specified length limit of functions can also be configured by the users.
 *
 * @author Zhuohang (Stephen) Shen <s2023501@ed.ac.uk>
 */
class MethodLengthInspection : AbstractBaseJavaLocalInspectionTool() {

    // Declare the variables for the length limit.
    var maxLength: Int = 10

    /**
     * Override the buildVisitor function to build a Java element visitor that provides
     * the new function length inspection.
     *
     * @param holder The container where the new Java element visitor will register problems it found.
     * @param isOnTheFly Boolean value indicates if the inspection is running in 'on-the-fly' mode.
     * @return A JavaElementVisitor that will inspect the length of Java functions.
     */
    @NotNull
    override fun buildVisitor(@NotNull holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {

            // Visit each function's declaration in the java code
            override fun visitMethod(method: PsiMethod) {

                // Call the visitMethod function from the super class to maintain the original behavior of it.
                super.visitMethod(method)

                // Calculate the function length
                val methodBody = method.body
                val methodLength: Int = countLines(method.text)

                // Check if the function body is not null and exceeds the specified limit.
                if (methodBody != null && methodLength > maxLength) {

                    // Register a problem in the problem holder if the method exceeds the specified limit.
                    holder.registerProblem(
                        method.nameIdentifier ?: method,
                        "Method '${method.name}' is too long (current: ${methodLength}, limit: $maxLength).",
                        ProblemHighlightType.WARNING
                    )
                }
            }

            // One helper function to calculate the length of the function
            private fun countLines(text: String): Int {
                return text.split("\n").size
            }
        }
    }
}
