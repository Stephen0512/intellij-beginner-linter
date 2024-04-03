package uk.ac.ed.inf.intellijbeginnerlinter

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
import uk.ac.ed.inf.intellijbeginnerlinter.controlstructinspects.*
import java.awt.Font

class Panel(private val project: Project) : Configurable {

    // Declare a empty setting panel for all UI components.
    private val panel: JPanel

    // Declare UI components for the Custom Method Length Inspection.
    private val enableMethodLengthInspectionCheckBox = JCheckBox("Enable Inspection")
    private val maxLengthTextField = JTextField()
    private val enableControlStructInspectionsCheckBox = JCheckBox("Enable Inspections")
    private val ifStatementUsageTextField = JTextField()
    private val switchStatementUsageTextField = JTextField()
    private val whileLoopUsageTextField = JTextField()
    private val forLoopUsageTextField = JTextField()
    private val foreachLoopUsageTextField = JTextField()

    init {
        // Retrieves the existing inspection profile for the current project.
        val inspectionProfile = InspectionProjectProfileManager.getInstance(project).currentProfile

        // --------------------------- Custom Method Length Inspection ---------------------------

        // Create a title label for the MethodLengthInspection
        val methodLengthInspectionTitleLabel = JLabel("Method Length Inspection Settings")

        // Change the font of the title label to bold and increase font size
        val methodLengthTitleFont = methodLengthInspectionTitleLabel.font.deriveFont(Font.BOLD, methodLengthInspectionTitleLabel.font.size * 1.2f)
        methodLengthInspectionTitleLabel.font = methodLengthTitleFont

        // Get the MethodLengthInspection inspection wrapper from the inspection profile.
        val methodLengthInspectionTool = inspectionProfile.getInspectionTool("MethodLengthInspection", project)

        // Get the MethodLengthInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not MethodLengthInspection.
        val methodLengthInspection = methodLengthInspectionTool?.tool as? MethodLengthInspection

        // Set the UI components to the stored states of the MethodLengthInspection inspection.
        enableMethodLengthInspectionCheckBox.isSelected = methodLengthInspectionTool != null && inspectionProfile.isToolEnabled(methodLengthInspectionTool.displayKey)
        maxLengthTextField.text = methodLengthInspection?.maxLength?.toString() ?: "10"

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

        // --------------------------- Custom control structure Inspections ---------------------------

        // Create a title label for the inspections of control structures
        val controlStructInspectionsTitleLabel = JLabel("Control Structure Inspections Settings")

        val spaces = "&nbsp;".repeat(11)
        val notesLabelText = """
                <html>
                <body>
                Notes: The format of specifications are: method_name: times_allowed, ...<br>
                $spaces Method name can be written as regex expressions to represent multiple functions.<br>
                $spaces Only enter method_name without numbers if no usage limit is set.
                </body>
                </html>
                """.trimIndent()
        val controlStructInspectionsNotesLabel = JLabel(notesLabelText)

        // Change the font of the title label to bold and increase font size
        val controlStructTitleFont = controlStructInspectionsTitleLabel.font.deriveFont(Font.BOLD, controlStructInspectionsTitleLabel.font.size * 1.2f)
        controlStructInspectionsTitleLabel.font = controlStructTitleFont

        // -------------- If Statement Inspection -------------

        // Get the ifStatementInspection inspection wrapper from the inspection profile.
        val ifStatementInspectionTool = inspectionProfile.getInspectionTool("IfStatementUsageInspection", project)

        // Get the ifStatementInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not ifStatementInspection.
        val ifStatementInspection = ifStatementInspectionTool?.tool as? IfStatementUsageInspection

        // Set the UI components to the stored states of the ifStatementInspection inspection.
        val ifStatementState = ifStatementInspectionTool != null && inspectionProfile.isToolEnabled(ifStatementInspectionTool.displayKey)
        ifStatementUsageTextField.text = ifStatementInspection?.specs ?: ""

        // -------------- Switch Statement Inspection -------------

        // Get the switchStatementInspection inspection wrapper from the inspection profile.
        val switchStatementInspectionTool = inspectionProfile.getInspectionTool("SwitchStatementUsageInspection", project)

        // Get the switchStatementInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not switchStatementInspection.
        val switchStatementInspection = switchStatementInspectionTool?.tool as? SwitchStatementUsageInspection

        // Set the UI components to the stored states of the switchStatementInspection inspection.
        val switchStatementState = switchStatementInspectionTool != null && inspectionProfile.isToolEnabled(switchStatementInspectionTool.displayKey)
        switchStatementUsageTextField.text = switchStatementInspection?.specs ?: ""

        // -------------- While Loop Inspection -------------

        // Get the whileLoopInspection inspection wrapper from the inspection profile.
        val whileLoopInspectionTool = inspectionProfile.getInspectionTool("WhileLoopUsageInspection", project)

        // Get the whileLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not whileLoopInspection.
        val whileLoopInspection = whileLoopInspectionTool?.tool as? WhileLoopUsageInspection

        // Set the UI components to the stored states of the whileLoopInspection inspection.
        val whileLoopState = whileLoopInspectionTool != null && inspectionProfile.isToolEnabled(whileLoopInspectionTool.displayKey)
        whileLoopUsageTextField.text = whileLoopInspection?.specs ?: ""

        // -------------- For Loop Inspection -------------

        // Get the forLoopInspection inspection wrapper from the inspection profile.
        val forLoopInspectionTool = inspectionProfile.getInspectionTool("ForLoopUsageInspection", project)

        // Get the forLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not forLoopInspection.
        val forLoopInspection = forLoopInspectionTool?.tool as? ForLoopUsageInspection

        // Set the UI components to the stored states of the forLoopInspection inspection.
        val forLoopState = forLoopInspectionTool != null && inspectionProfile.isToolEnabled(forLoopInspectionTool.displayKey)
        forLoopUsageTextField.text = forLoopInspection?.specs ?: ""

        // -------------- Enhanced For Loop Inspection -------------

        // Get the forLoopInspection inspection wrapper from the inspection profile.
        val foreachLoopInspectionTool = inspectionProfile.getInspectionTool("ForeachLoopUsageInspection", project)

        // Get the foreachLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not foreachLoopInspection.
        val foreachLoopInspection = foreachLoopInspectionTool?.tool as? ForeachLoopUsageInspection

        // Set the UI components to the stored states of the foreachLoopInspection inspection.
        val foreachLoopState = foreachLoopInspectionTool != null && inspectionProfile.isToolEnabled(foreachLoopInspectionTool.displayKey)
        foreachLoopUsageTextField.text = foreachLoopInspection?.specs ?: ""

        // -------------- Final Code for Control Structures  -------------

        // Check all the control structure inspections have the same state.
        if (ifStatementState == switchStatementState == whileLoopState == forLoopState == foreachLoopState) {
            enableControlStructInspectionsCheckBox.isSelected = ifStatementState
        } else {

            // Set all the control structure inspections to false
            enableControlStructInspectionsCheckBox.isSelected = false
            if (ifStatementInspectionTool != null) {
                inspectionProfile.setToolEnabled(ifStatementInspectionTool.shortName, false)
            }
            if (switchStatementInspectionTool != null) {
                inspectionProfile.setToolEnabled(switchStatementInspectionTool.shortName, false)
            }
            if (whileLoopInspectionTool != null) {
                inspectionProfile.setToolEnabled(whileLoopInspectionTool.shortName, false)
            }
            if (forLoopInspectionTool != null) {
                inspectionProfile.setToolEnabled(forLoopInspectionTool.shortName, false)
            }
            if (foreachLoopInspectionTool != null) {
                inspectionProfile.setToolEnabled(foreachLoopInspectionTool.shortName, false)
            }

            // Commit the changes to the inspection profile
            inspectionProfile.modifyProfile {
                it.commit()
            }
        }

        // Add change listeners to enable or disable the control structure inspections based on user interaction.
        enableControlStructInspectionsCheckBox.addChangeListener {
            val newState =  enableControlStructInspectionsCheckBox.isSelected  // Read the new state.

            // Commit the changes to the inspection profile if the ifStatementInspection exists.
            if (ifStatementInspectionTool != null) {
                inspectionProfile.setToolEnabled(ifStatementInspectionTool.shortName, newState)
            }

            // Commit the changes to the inspection profile if the switchStatementInspectionTool exists.
            if (switchStatementInspectionTool != null) {
                inspectionProfile.setToolEnabled(switchStatementInspectionTool.shortName, newState)
            }

            // Commit the changes to the inspection profile if the whileLoopInspectionTool exists.
            if (whileLoopInspectionTool != null) {
                inspectionProfile.setToolEnabled(whileLoopInspectionTool.shortName, newState)
            }

            // Commit the changes to the inspection profile if the forLoopInspectionTool exists.
            if (forLoopInspectionTool != null) {
                inspectionProfile.setToolEnabled(forLoopInspectionTool.shortName, newState)
            }

            // Commit the changes to the inspection profile if the foreachLoopInspectionTool exists.
            if (foreachLoopInspectionTool != null) {
                inspectionProfile.setToolEnabled(foreachLoopInspectionTool.shortName, newState)
            }

            inspectionProfile.modifyProfile {
                it.commit()
            }
        }

        // ------------------------------- Setting Panel Formation -------------------------------

        val separator_1 = JSeparator()
        val separator_2 = JSeparator()

        panel = FormBuilder.createFormBuilder()
                .addComponent(methodLengthInspectionTitleLabel)
                .addComponent(enableMethodLengthInspectionCheckBox)
                .addLabeledComponent("Max Method Length:", maxLengthTextField)
                .addComponent(separator_1)
                .addComponent(controlStructInspectionsTitleLabel)
                .addComponent(enableControlStructInspectionsCheckBox)
                .addLabeledComponent("If Statement Usage Specification:", ifStatementUsageTextField)
                .addLabeledComponent("Switch Statement Usage Specification:", switchStatementUsageTextField)
                .addLabeledComponent("While Loop Usage Specification:", whileLoopUsageTextField)
                .addLabeledComponent("For Loop Usage Specification:", forLoopUsageTextField)
                .addLabeledComponent("Enhanced For Loop Usage Specification:", foreachLoopUsageTextField)
                .addComponent(controlStructInspectionsNotesLabel)
                .addComponent(separator_2)
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

        // --------------------------- Custom control structure Inspections ---------------------------

        // -------------- If Statement Inspection -------------

        // Get the IfStatementInspection inspection wrapper from the inspection profile.
        val ifStatementInspectionTool = inspectionProfile.getInspectionTool("IfStatementUsageInspection", project)

        // Get the IfStatementInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not IfStatementInspection.
        val ifStatementInspection = ifStatementInspectionTool?.tool as? IfStatementUsageInspection

        // Set the UI components to the stored states of the control structure inspections.
        val ifStatementChanged =  ifStatementInspectionTool != null && enableControlStructInspectionsCheckBox.isSelected != inspectionProfile.isToolEnabled(ifStatementInspectionTool.displayKey)
        val ifStatementSpecsChanged =  (ifStatementInspection?.specs ?: "") != ifStatementUsageTextField.text

        // -------------- Switch Statement Inspection -------------

        // Get the SwitchStatementInspection inspection wrapper from the inspection profile.
        val switchStatementInspectionTool = inspectionProfile.getInspectionTool("SwitchStatementUsageInspection", project)

        // Get the SwitchStatementInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not SwitchStatementInspection.
        val switchStatementInspection = switchStatementInspectionTool?.tool as? SwitchStatementUsageInspection

        // Set the UI components to the stored states of the control structure inspections.
        val switchStatementChanged =  switchStatementInspectionTool != null && enableControlStructInspectionsCheckBox.isSelected != inspectionProfile.isToolEnabled(switchStatementInspectionTool.displayKey)
        val switchStatementSpecsChanged =  (switchStatementInspection?.specs ?: "") != switchStatementUsageTextField.text

        // -------------- While Loop Inspection -------------

        // Get the whileLoopInspection inspection wrapper from the inspection profile.
        val whileLoopInspectionTool = inspectionProfile.getInspectionTool("WhileLoopUsageInspection", project)

        // Get the whileLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not whileLoopInspection.
        val whileLoopInspection = whileLoopInspectionTool?.tool as? WhileLoopUsageInspection

        // Set the UI components to the stored states of the control structure inspections.
        val whileLoopChanged =  whileLoopInspectionTool != null && enableControlStructInspectionsCheckBox.isSelected != inspectionProfile.isToolEnabled(whileLoopInspectionTool.displayKey)
        val whileLoopSpecsChanged =  (whileLoopInspection?.specs ?: "") != whileLoopUsageTextField.text

        // -------------- For Loop Inspection -------------

        // Get the forLoopInspection inspection wrapper from the inspection profile.
        val forLoopInspectionTool = inspectionProfile.getInspectionTool("ForLoopUsageInspection", project)

        // Get the forLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not forLoopInspection.
        val forLoopInspection = forLoopInspectionTool?.tool as? ForLoopUsageInspection

        // Set the UI components to the stored states of the control structure inspections.
        val forLoopChanged =  forLoopInspectionTool != null && enableControlStructInspectionsCheckBox.isSelected != inspectionProfile.isToolEnabled(forLoopInspectionTool.displayKey)
        val forLoopSpecsChanged =  (forLoopInspection?.specs ?: "") != forLoopUsageTextField.text

        // -------------- Enhanced For Loop Inspection -------------

        // Get the foreachLoopInspection inspection wrapper from the inspection profile.
        val foreachLoopInspectionTool = inspectionProfile.getInspectionTool("ForeachLoopUsageInspection", project)

        // Get the foreachLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not foreachLoopInspection.
        val foreachLoopInspection = foreachLoopInspectionTool?.tool as? ForeachLoopUsageInspection

        // Set the UI components to the stored states of the control structure inspections.
        val foreachLoopChanged =  foreachLoopInspectionTool != null && enableControlStructInspectionsCheckBox.isSelected != inspectionProfile.isToolEnabled(foreachLoopInspectionTool.displayKey)
        val foreachLoopSpecsChanged =  (foreachLoopInspection?.specs ?: "") != foreachLoopUsageTextField.text

        // -------------- Final Code for Control Structures  -------------

        val controlStructureChanged = ifStatementChanged || switchStatementChanged || whileLoopChanged || forLoopChanged || foreachLoopChanged
        val controlStructureSpecsChanged = ifStatementSpecsChanged || switchStatementSpecsChanged || whileLoopSpecsChanged || forLoopSpecsChanged || foreachLoopSpecsChanged

        // ------------------------------- Return the final result -------------------------------

        return methodLengthChanged || maxLengthChanged || controlStructureChanged || controlStructureSpecsChanged
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
            val newMethodState = enableMethodLengthInspectionCheckBox.isSelected  // Read the new state.
            inspectionProfile.setToolEnabled(methodLengthInspectionTool.shortName, newMethodState)
        }

        // --------------------------- Custom control structure Inspections ---------------------------

        val newControlStructureState = enableControlStructInspectionsCheckBox.isSelected  // Read the new state.

        // -------------- If Statement Inspection -------------

        // Get the IfStatementInspection inspection wrapper from the inspection profile.
        val ifStatementInspectionTool = inspectionProfile.getInspectionTool("IfStatementUsageInspection", project)

        // Get the IfStatementInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not IfStatementInspection.
        val ifStatementInspection = ifStatementInspectionTool?.tool as? IfStatementUsageInspection

        // If the IfStatementInspection inspection instance exists.
        if (ifStatementInspection != null) {

            // Store the settings in the panel to the IfStatementInspection inspection instance.
            ifStatementInspection.specs = ifStatementUsageTextField.text ?: ""
            inspectionProfile.setToolEnabled(ifStatementInspectionTool.shortName, newControlStructureState)
        }

        // -------------- Switch Statement Inspection -------------

        // Get the SwitchStatementInspection inspection wrapper from the inspection profile.
        val switchStatementInspectionTool = inspectionProfile.getInspectionTool("SwitchStatementUsageInspection", project)

        // Get the SwitchStatementInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not switchStatementInspection.
        val switchStatementInspection = switchStatementInspectionTool?.tool as? SwitchStatementUsageInspection

        // If the switchStatementInspection inspection instance exists.
        if (switchStatementInspection != null) {

            // Store the settings in the panel to the switchStatementInspection inspection instance.
            switchStatementInspection.specs = switchStatementUsageTextField.text ?: ""
            inspectionProfile.setToolEnabled(switchStatementInspectionTool.shortName, newControlStructureState)
        }

        // -------------- While Loop Inspection -------------

        // Get the whileLoopInspection inspection wrapper from the inspection profile.
        val whileLoopInspectionTool = inspectionProfile.getInspectionTool("WhileLoopUsageInspection", project)

        // Get the whileLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not whileLoopInspection.
        val whileLoopInspection = whileLoopInspectionTool?.tool as? WhileLoopUsageInspection

        // If the whileLoopInspection inspection instance exists.
        if (whileLoopInspection != null) {

            // Store the settings in the panel to the whileLoopInspection inspection instance.
            whileLoopInspection.specs = whileLoopUsageTextField.text ?: ""
            inspectionProfile.setToolEnabled(whileLoopInspectionTool.shortName, newControlStructureState)
        }

        // -------------- For Loop Inspection -------------

        // Get the forLoopInspection inspection wrapper from the inspection profile.
        val forLoopInspectionTool = inspectionProfile.getInspectionTool("ForLoopUsageInspection", project)

        // Get the forLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not forLoopInspection.
        val forLoopInspection = forLoopInspectionTool?.tool as? ForLoopUsageInspection

        // If the forLoopInspection inspection instance exists.
        if (forLoopInspection != null) {

            // Store the settings in the panel to the forLoopInspection inspection instance.
            forLoopInspection.specs = forLoopUsageTextField.text ?: ""
            inspectionProfile.setToolEnabled(forLoopInspectionTool.shortName, newControlStructureState)
        }

        // -------------- Enhanced For Loop Inspection -------------

        // Get the foreachLoopInspection inspection wrapper from the inspection profile.
        val foreachLoopInspectionTool = inspectionProfile.getInspectionTool("ForeachLoopUsageInspection", project)

        // Get the foreachLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not foreachLoopInspection.
        val foreachLoopInspection = foreachLoopInspectionTool?.tool as? ForeachLoopUsageInspection

        // If the foreachLoopInspection inspection instance exists.
        if (foreachLoopInspection != null) {

            // Store the settings in the panel to the foreachLoopInspection inspection instance.
            foreachLoopInspection.specs = foreachLoopUsageTextField.text ?: ""
            inspectionProfile.setToolEnabled(foreachLoopInspectionTool.shortName, newControlStructureState)
        }

        // Commit the changes to the inspection profile
        inspectionProfile.modifyProfile {
            it.commit()
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

        // --------------------------- Custom control structure Inspections ---------------------------

        // -------------- If Statement Inspection -------------

        // Get the IfStatementInspection inspection wrapper from the inspection profile.
        val ifStatementInspectionTool = inspectionProfile.getInspectionTool("IfStatementUsageInspection", project)

        // Get the IfStatementInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not ifStatementInspection.
        val ifStatementInspection = ifStatementInspectionTool?.tool as? IfStatementUsageInspection

        // Set the UI components to the stored states of the control structure inspections.
        val ifStatementState = ifStatementInspectionTool != null && inspectionProfile.isToolEnabled(ifStatementInspectionTool.displayKey)
        ifStatementUsageTextField.text = ifStatementInspection?.specs ?: ""

        // -------------- Switch Statement Inspection -------------

        // Get the SwitchStatementInspection inspection wrapper from the inspection profile.
        val switchStatementInspectionTool = inspectionProfile.getInspectionTool("SwitchStatementUsageInspection", project)

        // Get the SwitchStatementInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not switchStatementInspection.
        val switchStatementInspection = switchStatementInspectionTool?.tool as? SwitchStatementUsageInspection

        // Set the UI components to the stored states of the control structure inspections.
        val switchStatementState = switchStatementInspectionTool != null && inspectionProfile.isToolEnabled(switchStatementInspectionTool.displayKey)
        switchStatementUsageTextField.text = switchStatementInspection?.specs ?: ""

        // -------------- While Loop Inspection -------------

        // Get the whileLoopInspection inspection wrapper from the inspection profile.
        val whileLoopInspectionTool = inspectionProfile.getInspectionTool("WhileLoopUsageInspection", project)

        // Get the whileLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not whileLoopInspection.
        val whileLoopInspection = whileLoopInspectionTool?.tool as? WhileLoopUsageInspection

        // Set the UI components to the stored states of the whileLoopInspection inspection.
        val whileLoopState = whileLoopInspectionTool != null && inspectionProfile.isToolEnabled(whileLoopInspectionTool.displayKey)
        whileLoopUsageTextField.text = whileLoopInspection?.specs ?: ""

        // -------------- For Loop Inspection -------------

        // Get the forLoopInspection inspection wrapper from the inspection profile.
        val forLoopInspectionTool = inspectionProfile.getInspectionTool("ForLoopUsageInspection", project)

        // Get the forLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not forLoopInspection.
        val forLoopInspection = forLoopInspectionTool?.tool as? ForLoopUsageInspection

        // Set the UI components to the stored states of the forLoopInspection inspection.
        val forLoopState = forLoopInspectionTool != null && inspectionProfile.isToolEnabled(forLoopInspectionTool.displayKey)
        forLoopUsageTextField.text = forLoopInspection?.specs ?: ""

        // -------------- Enhanced For Loop Inspection -------------

        // Get the forLoopInspection inspection wrapper from the inspection profile.
        val foreachLoopInspectionTool = inspectionProfile.getInspectionTool("ForeachLoopUsageInspection", project)

        // Get the foreachLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not foreachLoopInspection.
        val foreachLoopInspection = foreachLoopInspectionTool?.tool as? ForeachLoopUsageInspection

        // Set the UI components to the stored states of the foreachLoopInspection inspection.
        val foreachLoopState = foreachLoopInspectionTool != null && inspectionProfile.isToolEnabled(foreachLoopInspectionTool.displayKey)
        foreachLoopUsageTextField.text = foreachLoopInspection?.specs ?: ""

        // -------------- Final Code for Control Structures  -------------

        // Check all the control structure inspections have the same state.
        if (ifStatementState == switchStatementState == whileLoopState == forLoopState == foreachLoopState) {
            enableControlStructInspectionsCheckBox.isSelected = ifStatementState
        } else {

            // Set all the control structure inspections to false
            enableControlStructInspectionsCheckBox.isSelected = false
            if (ifStatementInspectionTool != null) {
                inspectionProfile.setToolEnabled(ifStatementInspectionTool.shortName, false)
            }
            if (switchStatementInspectionTool != null) {
                inspectionProfile.setToolEnabled(switchStatementInspectionTool.shortName, false)
            }
            if (whileLoopInspectionTool != null) {
                inspectionProfile.setToolEnabled(whileLoopInspectionTool.shortName, false)
            }
            if (forLoopInspectionTool != null) {
                inspectionProfile.setToolEnabled(forLoopInspectionTool.shortName, false)
            }
            if (foreachLoopInspectionTool != null) {
                inspectionProfile.setToolEnabled(foreachLoopInspectionTool.shortName, false)
            }

            // Commit the changes to the inspection profile
            inspectionProfile.modifyProfile {
                it.commit()
            }
        }
    }

    override fun getDisplayName(): String {
        return "Beginner Plugin Panel"
    }

}