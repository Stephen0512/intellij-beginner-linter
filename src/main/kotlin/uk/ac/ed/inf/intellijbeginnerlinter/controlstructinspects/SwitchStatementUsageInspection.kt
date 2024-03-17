package uk.ac.ed.inf.intellijbeginnerlinter.custominspects

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiSwitchStatement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.annotations.NotNull

class SwitchStatementUsageInspection : AbstractBaseJavaLocalInspectionTool() {

    @NotNull
    override fun buildVisitor(@NotNull holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitSwitchStatement(statement: PsiSwitchStatement) {
                super.visitSwitchStatement(statement)
                val method = PsiTreeUtil.getParentOfType(statement, PsiMethod::class.java)
                if (method != null) {
                    holder.registerProblem(
                        statement,
                        "Avoid using 'switch' statements within Java methods '${method.name}'."
                    )
                }
            }
        }
    }
}