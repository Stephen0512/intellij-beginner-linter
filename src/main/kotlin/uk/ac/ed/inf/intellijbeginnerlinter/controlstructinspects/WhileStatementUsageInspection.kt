package uk.ac.ed.inf.intellijbeginnerlinter.custominspects

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiWhileStatement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.annotations.NotNull

class WhileStatementUsageInspection : AbstractBaseJavaLocalInspectionTool() {

    @NotNull
    override fun buildVisitor(@NotNull holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitWhileStatement(statement: PsiWhileStatement) {
                super.visitWhileStatement(statement)
                val method = PsiTreeUtil.getParentOfType(statement, PsiMethod::class.java)
                if (method != null) {
                    holder.registerProblem(
                        statement,
                        "Avoid using 'while' statements within Java methods '${method.name}'."
                    )
                }
            }
        }
    }
}