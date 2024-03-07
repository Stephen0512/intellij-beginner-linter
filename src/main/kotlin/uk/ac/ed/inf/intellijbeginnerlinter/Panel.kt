package uk.ac.ed.inf.intellijbeginnerlinter

import uk.ac.ed.inf.intellijbeginnerlinter.custominspects.MethodLengthInspection

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.profile.codeInspection.InspectionProjectProfileManager
import javax.swing.JPanel
import javax.swing.JCheckBox
import javax.swing.JTextField
import javax.swing.JLabel
import javax.swing.JComponent
import javax.swing.JSeparator
import com.intellij.util.ui.FormBuilder
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridLayout

class Panel(private val project: Project) : Configurable {

    // Declare a empty setting panel for all UI components.
    private val panel: JPanel

    // Declare UI components for the Custom Method Length Inspection.
    private val enableMethodLengthInspectionCheckBox = JCheckBox("Enable Inspection")
    private val maxLengthTextField = JTextField()
    private val methodNamesTextField = JTextField()

    init {
        // Retrieves the existing inspection profile for the current project.
        val inspectionProfile = InspectionProjectProfileManager.getInstance(project).currentProfile

        // --------------------------- Custom Method Length Inspection ---------------------------

        // Create a title label for the MethodLengthInspection
        val methodLengthInspectionTitleLabel = JLabel("Method Length Inspection Settings")

        // Change the font of the title label to bold and increase font size
        val titleFont = methodLengthInspectionTitleLabel.font.deriveFont(Font.BOLD, methodLengthInspectionTitleLabel.font.size * 1.2f)
        methodLengthInspectionTitleLabel.font = titleFont

        // Get the MethodLengthInspection inspection wrapper from the inspection profile.
        val methodLengthInspectionTool = inspectionProfile.getInspectionTool("MethodLengthInspection", project)

        // Get the MethodLengthInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not MethodLengthInspection.
        val methodLengthInspection = methodLengthInspectionTool?.tool as? MethodLengthInspection

        // Set the UI components to the stored states of the MethodLengthInspection inspection.
        enableMethodLengthInspectionCheckBox.isSelected = methodLengthInspectionTool != null && inspectionProfile.isToolEnabled(methodLengthInspectionTool.displayKey)
        maxLengthTextField.text = methodLengthInspection?.maxLength?.toString() ?: "10"
        methodNamesTextField.text = methodLengthInspection?.methodNames ?: ""

        // Add a change listener to enable or disable the MethodLengthInspection inspection based on user interaction.
        enableMethodLengthInspectionCheckBox.addChangeListener {
            val newState = enableMethodLengthInspectionCheckBox.isSelected  // Read the new state.

            // Commit the changes to the inspection profile if the inspection exists.
            if (methodLengthInspectionTool != null) {
                inspectionProfile.setToolEnabled(methodLengthInspectionTool.shortName, newState)
                inspectionProfile.modifyProfile {
                    it.commit()
                }
            }
        }

        // ------------------------------- Setting Panel Formation -------------------------------

        val separator = JSeparator()

        panel = FormBuilder.createFormBuilder()
                .addComponent(methodLengthInspectionTitleLabel)
                .addComponent(enableMethodLengthInspectionCheckBox)
                .addLabeledComponent("Max Method Length:", maxLengthTextField)
                .addLabeledComponent("Method Name Filter (Regex):", methodNamesTextField)
                .addComponent(separator)
                .panel
    }

    override fun createComponent(): JComponent {
        return panel
    }

    override fun isModified(): Boolean {
        // Retrieves the existing inspection profile for the current project.
        val inspectionProfile = InspectionProjectProfileManager.getInstance(project).currentProfile

        // --------------------------- Custom Method Length Inspection ---------------------------

        // Get the MethodLengthInspection inspection wrapper from the inspection profile.
        val methodLengthInspectionTool = inspectionProfile.getInspectionTool("MethodLengthInspection", project)

        // Get the MethodLengthInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not MethodLengthInspection.
        val methodLengthInspection = methodLengthInspectionTool?.tool as? MethodLengthInspection

        // Compare the results between the panel and the inspection profile.
        val methodLengthChanged = methodLengthInspectionTool != null && enableMethodLengthInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(methodLengthInspectionTool.displayKey)
        val maxLengthChanged = (methodLengthInspection?.maxLength?.toString() ?: "10") != maxLengthTextField.text
        val methodNameChanged = (methodLengthInspection?.methodNames ?: "") != methodNamesTextField.text

        // ------------------------------- Return the final result -------------------------------

        return methodLengthChanged || maxLengthChanged || methodNameChanged
    }

    override fun apply() {
        // Retrieves the existing inspection profile for the current project.
        val inspectionProfile = InspectionProjectProfileManager.getInstance(project).currentProfile

        // --------------------------- Custom Method Length Inspection ---------------------------

        // Get the MethodLengthInspection inspection wrapper from the inspection profile.
        val methodLengthInspectionTool = inspectionProfile.getInspectionTool("MethodLengthInspection", project)

        // Get the MethodLengthInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not MethodLengthInspection.
        val methodLengthInspection = methodLengthInspectionTool?.tool as? MethodLengthInspection

        // If the MethodLengthInspection inspection instance exists.
        if (methodLengthInspection != null) {

            // Store the settings in the panel to the MethodLengthInspection inspection instance.
            methodLengthInspection.maxLength = maxLengthTextField.text.toIntOrNull() ?: 0
            methodLengthInspection.methodNames = methodNamesTextField.text
            val newState = enableMethodLengthInspectionCheckBox.isSelected  // Read the new state.
            inspectionProfile.setToolEnabled(methodLengthInspectionTool.shortName, newState)

            // Commit the changes to the inspection profile
            inspectionProfile.modifyProfile {
                it.commit()
            }
        }
    }

    override fun reset() {
        // Retrieves the existing inspection profile for the current project.
        val inspectionProfile = InspectionProjectProfileManager.getInstance(project).currentProfile

        // --------------------------- Custom Method Length Inspection ---------------------------

        // Get the MethodLengthInspection inspection wrapper from the inspection profile.
        val methodLengthInspectionTool = inspectionProfile.getInspectionTool("MethodLengthInspection", project)

        // Get the MethodLengthInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not MethodLengthInspection.
        val methodLengthInspection = methodLengthInspectionTool?.tool as? MethodLengthInspection

        // Reset the settings of the MethodLengthInspection inspection using the inspection profile
        enableMethodLengthInspectionCheckBox.isSelected = methodLengthInspectionTool != null && inspectionProfile.isToolEnabled(methodLengthInspectionTool.displayKey)
        maxLengthTextField.text = methodLengthInspection?.maxLength?.toString() ?: "10"
        methodNamesTextField.text = methodLengthInspection?.methodNames ?: ""
    }

    override fun getDisplayName(): String {
        return "Beginner Plugin Panel"
    }

}