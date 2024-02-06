package uk.ac.ed.inf.intellijbeginnerlinter.custominspects

import com.intellij.codeInspection.*
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethod

class MethodLengthInspection : LocalInspectionTool() {

    var maxLength: Int = 10
    var methodName: String = ""

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethod(method: PsiMethod) {
                super.visitMethod(method)
                val body = method.body
                if (body != null && body.text.split("\n").size > maxLength
                        && (methodName.isEmpty() || method.name == methodName)) {
                    holder.registerProblem(
                            method.nameIdentifier ?: method,
                            "Method '${method.name}' is too long (current: " +
                                    "${body.text.split("\n").size}, limit: $maxLength)",
                            ProblemHighlightType.WARNING
                    )
                }
            }
        }
    }
}
