package uk.ac.ed.inf.intellijbeginnerlinter.custominspects

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiMethod
import java.util.regex.PatternSyntaxException
import org.jetbrains.annotations.NotNull


/**
 * A customized inspection that checks if the length of a function exceeds a specified limit.
 * The inspection can be configured to apply this inspection to all functions or only to specified functions.
 * Ihe specified length limit of functions can also be configured by the users.
 */
class MethodLengthInspection : AbstractBaseJavaLocalInspectionTool() {

    // Declare the variables for the length limit and the functions to be checked.
    var maxLength: Int = 10
    var methodNames: String = ""  // Empty means checking all the functions.

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

                // Get the function body and calculate the function length
                val methodBody = method.body
                val methodLength: Int = countLines(method.text)

                // Declare the new variable indicating if the function name matches the ones specified by the user.
                var isNameMatch = false

                // The function name matches if no string is given by the user.
                if (methodNames.isEmpty()) {
                    isNameMatch = true
                } else {

                    // Convert the string given by the user to regex expression and null for exceptions.
                    var regex: Regex? = null
                    try {
                        regex = Regex(methodNames)
                    } catch (e: PatternSyntaxException) {
                        e.printStackTrace()  // When exceptions, no function would be matched.
                    }

                    // If the conversion is success, matches the function name with the regex expression.
                    if (regex != null) {
                        isNameMatch = regex.matches(method.name)
                    }
                }

                // Check if the function body is not null and exceeds the specified limit.
                // Also check if the function name matches the ones provided by the user (if specified).
                if (methodBody != null && methodLength > maxLength && isNameMatch) {

                    // Register a problem in the problem holder if the method exceeds the specified limit.
                    holder.registerProblem(
                        method.nameIdentifier ?: method,
                        "Method '${method.name}' is too long (current: ${methodLength}, limit: $maxLength)",
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
