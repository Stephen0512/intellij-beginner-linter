package uk.ac.ed.inf.intellijbeginnerlinter

import uk.ac.ed.inf.intellijbeginnerlinter.custominspects.MethodLengthInspection

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.profile.codeInspection.InspectionProjectProfileManager
import com.intellij.util.ui.FormBuilder
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class TogglePanel(private val project: Project) : Configurable {
    private val panel: JPanel
    private val enableMethodLengthInspectionCheckBox = JCheckBox("Enable Custom Method Length Inspection")
    private val enableEnhancedForLoopInspectionCheckBox = JCheckBox("Enable Enhanced For-Loop Inspection")
    private val maxLengthTextField = JTextField()
    private val methodNameTextField = JTextField()

    init {
        val inspectionProfile = InspectionProjectProfileManager.getInstance(project).currentProfile

        // Method Length Inspection
        val methodLengthInspectionTool = inspectionProfile.getInspectionTool("MethodLengthInspection", project)
        val methodLengthInspection = methodLengthInspectionTool?.tool as? MethodLengthInspection

        enableMethodLengthInspectionCheckBox.isSelected = methodLengthInspectionTool != null && inspectionProfile.isToolEnabled(methodLengthInspectionTool.displayKey)
        maxLengthTextField.text = methodLengthInspection?.maxLength?.toString() ?: "10"
        methodNameTextField.text = methodLengthInspection?.methodName ?: ""

        enableMethodLengthInspectionCheckBox.addChangeListener {
            val newState = enableMethodLengthInspectionCheckBox.isSelected
            if (methodLengthInspectionTool != null) {
                inspectionProfile.setToolEnabled(methodLengthInspectionTool.shortName, newState)
                inspectionProfile.modifyProfile { it.commit() }
            }
        }

        // Enhanced For-Loop Inspection
        val enhancedForLoopInspectionTool = inspectionProfile.getInspectionTool("ForCanBeForeach", project)
        enableEnhancedForLoopInspectionCheckBox.isSelected = enhancedForLoopInspectionTool != null && inspectionProfile.isToolEnabled(enhancedForLoopInspectionTool.displayKey)

        enableEnhancedForLoopInspectionCheckBox.addChangeListener {
            val newState = enableEnhancedForLoopInspectionCheckBox.isSelected
            if (enhancedForLoopInspectionTool != null) {
                inspectionProfile.setToolEnabled(enhancedForLoopInspectionTool.shortName, newState)
                inspectionProfile.modifyProfile { it.commit() }
            }
        }

        panel = FormBuilder.createFormBuilder()
                .addComponent(enableMethodLengthInspectionCheckBox)
                .addLabeledComponent("Max Method Length:", maxLengthTextField)
                .addLabeledComponent("Method Name Filter:", methodNameTextField)
                .addComponent(enableEnhancedForLoopInspectionCheckBox)
                .panel
    }

    override fun createComponent(): JComponent = panel

    override fun isModified(): Boolean {
        val inspectionProfile = InspectionProjectProfileManager.getInstance(project).currentProfile

        val methodLengthInspectionTool = inspectionProfile.getInspectionTool("MethodLengthInspection", project)
        val methodLengthInspection = methodLengthInspectionTool?.tool as? MethodLengthInspection

        val enhancedForLoopInspectionTool = inspectionProfile.getInspectionTool("ForCanBeForeach", project)

        val methodLengthEnabled = methodLengthInspectionTool != null && enableMethodLengthInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(methodLengthInspectionTool.displayKey)
        val maxLengthModified = (methodLengthInspection?.maxLength?.toString() ?: "10") != maxLengthTextField.text
        val methodNameModified = (methodLengthInspection?.methodName ?: "") != methodNameTextField.text

        val enhancedForLoopEnabled = enhancedForLoopInspectionTool != null && enableEnhancedForLoopInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(enhancedForLoopInspectionTool.displayKey)

        return methodLengthEnabled || maxLengthModified || methodNameModified || enhancedForLoopEnabled
    }

    override fun apply() {
        val inspectionProfile = InspectionProjectProfileManager.getInstance(project).currentProfile

        val methodLengthInspectionTool = inspectionProfile.getInspectionTool("MethodLengthInspection", project)
        val methodLengthInspection = methodLengthInspectionTool?.tool as? MethodLengthInspection

        val enhancedForLoopInspectionTool = inspectionProfile.getInspectionTool("ForCanBeForeach", project)

        if (methodLengthInspection != null) {
            methodLengthInspection.maxLength = maxLengthTextField.text.toIntOrNull() ?: 10
            methodLengthInspection.methodName = methodNameTextField.text

            inspectionProfile.setToolEnabled(methodLengthInspectionTool.shortName, enableMethodLengthInspectionCheckBox.isSelected)
            inspectionProfile.modifyProfile { it.commit() }
        }

        if (enhancedForLoopInspectionTool != null) {
            inspectionProfile.setToolEnabled(enhancedForLoopInspectionTool.shortName, enableEnhancedForLoopInspectionCheckBox.isSelected)
            inspectionProfile.modifyProfile { it.commit() }
        }
    }

    override fun reset() {
        val inspectionProfile = InspectionProjectProfileManager.getInstance(project).currentProfile

        val methodLengthInspectionTool = inspectionProfile.getInspectionTool("MethodLengthInspection", project)
        val methodLengthInspection = methodLengthInspectionTool?.tool as? MethodLengthInspection

        val enhancedForLoopInspectionTool = inspectionProfile.getInspectionTool("ForCanBeForeach", project)

        enableMethodLengthInspectionCheckBox.isSelected = methodLengthInspectionTool != null && inspectionProfile.isToolEnabled(methodLengthInspectionTool.displayKey)
        maxLengthTextField.text = methodLengthInspection?.maxLength?.toString() ?: "10"
        methodNameTextField.text = methodLengthInspection?.methodName ?: ""

        enableEnhancedForLoopInspectionCheckBox.isSelected = enhancedForLoopInspectionTool != null && inspectionProfile.isToolEnabled(enhancedForLoopInspectionTool.displayKey)
    }

    override fun getDisplayName(): String = "Panel"

    override fun disposeUIResources() {
        // Typically, you would clean up resources here if you had any to dispose.
    }

    // Add any additional required methods or logic here.
}