package uk.ac.ed.inf.intellijbeginnerlinter

import uk.ac.ed.inf.intellijbeginnerlinter.controlstructinspects.*


import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.profile.codeInspection.InspectionProjectProfileManager
import javax.swing.*
import java.awt.Font
import com.intellij.util.ui.FormBuilder
import java.io.File
import java.io.FileReader
import javax.swing.filechooser.FileNameExtensionFilter
import com.google.gson.Gson
import com.google.gson.JsonObject


/**
 * A control panel that allows users to customise the settings of the tool.
 * It also supports the functionality of importing predefined settings through input JSON files.
 *
 * @author Zhuohang (Stephen) Shen <s2023501@ed.ac.uk>
 */
class ControlPanel(private val project: Project) : Configurable {

    // Declare a empty setting panel for all UI components.
    private val panel: JPanel

    // Declare UI components for the importation of the input settings file.
    private val importSettingsButton = JButton("Import Settings")

    // Declare UI components for the Customised Method Length Inspection.
    private val enableMethodLengthInspectionCheckBox = JCheckBox("Enable Inspection")
    private val maxLengthTextField = JTextField()

    // Declare UI components for the Customised Control Structure Inspections.
    private val enableControlStructInspectionsCheckBox = JCheckBox("Enable Inspections")
    private val ifStatementUsageTextField = JTextField()
    private val switchStatementUsageTextField = JTextField()
    private val whileLoopUsageTextField = JTextField()
    private val forLoopUsageTextField = JTextField()
    private val foreachLoopUsageTextField = JTextField()

    // Declare UI components for the Declaration Issue Inspections.
    private val enableUnusedInspectionCheckBox = JCheckBox("Enable Unused Declaration Inspection")
    private val enableCanBeFinalInspectionCheckBox = JCheckBox("Enable Declaration can have ‘Final’ Modifier Inspection")

    // Declare UI components for the Java Language Level Issue Inspections.
    private val enableSequencedCollectionMethodCanBeUsedInspectionCheckBox = JCheckBox("Enable SequencedCollection Method can be Used Inspection")
    private val enableForCanBeForeachInspectionCheckBox = JCheckBox("Enable ‘For’ Loop can be Replaced with Enhanced For Loop Inspection")
    private val enableConvert2DiamondInspectionCheckBox = JCheckBox("Enable Explicit Type can be Replaced with ‘<>’ Inspection:")
    private val enableManualArrayCopyInspectionCheckBox = JCheckBox("Enable Manual Array Copy Inspection")

    /**
     * Initialization function used to create the panel and create listeners to different UI components.
     */
    init {
        // Retrieves the existing inspection profile for the current project.
        val inspectionProfile = InspectionProjectProfileManager.getInstance(project).currentProfile

        // --------------------------- Custom Method Length Inspection ---------------------------

        // Create a title label for the MethodLengthInspection
        val methodLengthInspectionTitleLabel = JLabel("Method Length Inspection Settings")

        // Change the font of the title label to bold and increase font size
        val methodLengthTitleFont = methodLengthInspectionTitleLabel.font.deriveFont(
            Font.BOLD,
            methodLengthInspectionTitleLabel.font.size * 1.2f
        )
        methodLengthInspectionTitleLabel.font = methodLengthTitleFont

        // Get the MethodLengthInspection inspection wrapper from the inspection profile.
        val methodLengthInspectionTool = inspectionProfile.getInspectionTool("MethodLengthInspection", project)

        // Get the MethodLengthInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not MethodLengthInspection.
        val methodLengthInspection = methodLengthInspectionTool?.tool as? MethodLengthInspection

        // Set the UI components to the stored states of the MethodLengthInspection inspection.
        enableMethodLengthInspectionCheckBox.isSelected =
            methodLengthInspectionTool != null && inspectionProfile.isToolEnabled(methodLengthInspectionTool.displayKey)
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
        val controlStructTitleFont = controlStructInspectionsTitleLabel.font.deriveFont(
            Font.BOLD,
            controlStructInspectionsTitleLabel.font.size * 1.2f
        )
        controlStructInspectionsTitleLabel.font = controlStructTitleFont

        // -------------- If Statement Inspection -------------

        // Get the ifStatementInspection inspection wrapper from the inspection profile.
        val ifStatementInspectionTool = inspectionProfile.getInspectionTool("IfStatementUsageInspection", project)

        // Get the ifStatementInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not ifStatementInspection.
        val ifStatementInspection = ifStatementInspectionTool?.tool as? IfStatementUsageInspection

        // Set the UI components to the stored states of the ifStatementInspection inspection.
        val ifStatementState =
            ifStatementInspectionTool != null && inspectionProfile.isToolEnabled(ifStatementInspectionTool.displayKey)
        ifStatementUsageTextField.text = ifStatementInspection?.specs ?: ""

        // -------------- Switch Statement Inspection -------------

        // Get the switchStatementInspection inspection wrapper from the inspection profile.
        val switchStatementInspectionTool =
            inspectionProfile.getInspectionTool("SwitchStatementUsageInspection", project)

        // Get the switchStatementInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not switchStatementInspection.
        val switchStatementInspection = switchStatementInspectionTool?.tool as? SwitchStatementUsageInspection

        // Set the UI components to the stored states of the switchStatementInspection inspection.
        val switchStatementState =
            switchStatementInspectionTool != null && inspectionProfile.isToolEnabled(switchStatementInspectionTool.displayKey)
        switchStatementUsageTextField.text = switchStatementInspection?.specs ?: ""

        // -------------- While Loop Inspection -------------

        // Get the whileLoopInspection inspection wrapper from the inspection profile.
        val whileLoopInspectionTool = inspectionProfile.getInspectionTool("WhileLoopUsageInspection", project)

        // Get the whileLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not whileLoopInspection.
        val whileLoopInspection = whileLoopInspectionTool?.tool as? WhileLoopUsageInspection

        // Set the UI components to the stored states of the whileLoopInspection inspection.
        val whileLoopState =
            whileLoopInspectionTool != null && inspectionProfile.isToolEnabled(whileLoopInspectionTool.displayKey)
        whileLoopUsageTextField.text = whileLoopInspection?.specs ?: ""

        // -------------- For Loop Inspection -------------

        // Get the forLoopInspection inspection wrapper from the inspection profile.
        val forLoopInspectionTool = inspectionProfile.getInspectionTool("ForLoopUsageInspection", project)

        // Get the forLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not forLoopInspection.
        val forLoopInspection = forLoopInspectionTool?.tool as? ForLoopUsageInspection

        // Set the UI components to the stored states of the forLoopInspection inspection.
        val forLoopState =
            forLoopInspectionTool != null && inspectionProfile.isToolEnabled(forLoopInspectionTool.displayKey)
        forLoopUsageTextField.text = forLoopInspection?.specs ?: ""

        // -------------- Enhanced For Loop Inspection -------------

        // Get the forLoopInspection inspection wrapper from the inspection profile.
        val foreachLoopInspectionTool = inspectionProfile.getInspectionTool("ForeachLoopUsageInspection", project)

        // Get the foreachLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not foreachLoopInspection.
        val foreachLoopInspection = foreachLoopInspectionTool?.tool as? ForeachLoopUsageInspection

        // Set the UI components to the stored states of the foreachLoopInspection inspection.
        val foreachLoopState =
            foreachLoopInspectionTool != null && inspectionProfile.isToolEnabled(foreachLoopInspectionTool.displayKey)
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
            val newState = enableControlStructInspectionsCheckBox.isSelected  // Read the new state.

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

        // --------------------------- Declaration Issue Inspection ---------------------------

        // Create a title label for the Declaration Issue Inspections
        val declarationIssueInspectionsTitleLabel = JLabel("Declaration Issue Inspections Settings")

        // Change the font of the title label to bold and increase font size
        val declarationIssueTitleFont = declarationIssueInspectionsTitleLabel.font.deriveFont(
            Font.BOLD,
            declarationIssueInspectionsTitleLabel.font.size * 1.2f
        )
        declarationIssueInspectionsTitleLabel.font = declarationIssueTitleFont

        // Get the UnusedInspection inspection wrapper from the inspection profile.
        val UnusedInspectionTool = inspectionProfile.getInspectionTool("unused", project)

        // Set the UI component to the stored states of the UnusedInspection inspection.
        enableUnusedInspectionCheckBox.isSelected =
            UnusedInspectionTool != null && inspectionProfile.isToolEnabled(UnusedInspectionTool.displayKey)

        // Add a change listener to enable or disable the UnusedInspection inspection based on user interaction.
        enableUnusedInspectionCheckBox.addChangeListener {
            val newState = enableUnusedInspectionCheckBox.isSelected  // Read the new state.

            // Commit the changes to the inspection profile if the inspection exists.
            if (UnusedInspectionTool != null) {
                inspectionProfile.setToolEnabled(UnusedInspectionTool.shortName, newState)
                inspectionProfile.modifyProfile {
                    it.commit()
                }
            }
        }

        // Get the CanBeFinalInspection inspection wrapper from the inspection profile.
        val CanBeFinalInspectionTool = inspectionProfile.getInspectionTool("CanBeFinal", project)

        // Set the UI component to the stored states of the CanBeFinalInspection inspection.
        enableCanBeFinalInspectionCheckBox.isSelected =
            CanBeFinalInspectionTool != null && inspectionProfile.isToolEnabled(CanBeFinalInspectionTool.displayKey)

        // Add a change listener to enable or disable the CanBeFinalInspection inspection based on user interaction.
        enableCanBeFinalInspectionCheckBox.addChangeListener {
            val newState = enableCanBeFinalInspectionCheckBox.isSelected  // Read the new state.

            // Commit the changes to the inspection profile if the inspection exists.
            if (CanBeFinalInspectionTool != null) {
                inspectionProfile.setToolEnabled(CanBeFinalInspectionTool.shortName, newState)
                inspectionProfile.modifyProfile {
                    it.commit()
                }
            }
        }

        // --------------------------- Java Language Level Issue Inspection ---------------------------

        // Create a title label for the Java Language Level Issue Inspections
        val javaLanguageLevelInspectionsTitleLabel = JLabel("Java Language Level Issue Inspections Settings")

        // Change the font of the title label to bold and increase font size
        val javaLanguageLevelTitleFont = javaLanguageLevelInspectionsTitleLabel.font.deriveFont(
            Font.BOLD,
            javaLanguageLevelInspectionsTitleLabel.font.size * 1.2f
        )
        javaLanguageLevelInspectionsTitleLabel.font = javaLanguageLevelTitleFont

        // Get the sequencedCollectionInspection inspection wrapper from the inspection profile.
        val sequencedCollectionInspectionTool = inspectionProfile.getInspectionTool("SequencedCollectionMethodCanBeUsed", project)

        // Set the UI component to the stored states of the sequencedCollectionInspection inspection.
        enableSequencedCollectionMethodCanBeUsedInspectionCheckBox.isSelected =
            sequencedCollectionInspectionTool != null && inspectionProfile.isToolEnabled(sequencedCollectionInspectionTool.displayKey)

        // Add a change listener to enable or disable the sequencedCollectionInspection inspection based on user interaction.
        enableSequencedCollectionMethodCanBeUsedInspectionCheckBox.addChangeListener {
            val newState = enableSequencedCollectionMethodCanBeUsedInspectionCheckBox.isSelected  // Read the new state.

            // Commit the changes to the inspection profile if the inspection exists.
            if (sequencedCollectionInspectionTool != null) {
                inspectionProfile.setToolEnabled(sequencedCollectionInspectionTool.shortName, newState)
                inspectionProfile.modifyProfile {
                    it.commit()
                }
            }
        }

        // Get the ForCanBeForeach inspection wrapper from the inspection profile.
        val forCanBeForeachInspectionTool = inspectionProfile.getInspectionTool("ForCanBeForeach", project)

        // Set the UI component to the stored states of the forCanBeForeachInspection inspection.
        enableForCanBeForeachInspectionCheckBox.isSelected =
            forCanBeForeachInspectionTool != null && inspectionProfile.isToolEnabled(forCanBeForeachInspectionTool.displayKey)

        // Add a change listener to enable or disable the forCanBeForeachInspection inspection based on user interaction.
        enableForCanBeForeachInspectionCheckBox.addChangeListener {
            val newState = enableForCanBeForeachInspectionCheckBox.isSelected  // Read the new state.

            // Commit the changes to the inspection profile if the inspection exists.
            if (forCanBeForeachInspectionTool != null) {
                inspectionProfile.setToolEnabled(forCanBeForeachInspectionTool.shortName, newState)
                inspectionProfile.modifyProfile {
                    it.commit()
                }
            }
        }

        // Get the Convert2Diamond inspection wrapper from the inspection profile.
        val convert2DiamondInspectionTool = inspectionProfile.getInspectionTool("Convert2Diamond", project)

        // Set the UI component to the stored states of the convert2DiamondInspection inspection.
        enableConvert2DiamondInspectionCheckBox.isSelected =
            convert2DiamondInspectionTool != null && inspectionProfile.isToolEnabled(convert2DiamondInspectionTool.displayKey)

        // Add a change listener to enable or disable the convert2DiamondInspection inspection based on user interaction.
        enableConvert2DiamondInspectionCheckBox.addChangeListener {
            val newState = enableConvert2DiamondInspectionCheckBox.isSelected  // Read the new state.

            // Commit the changes to the inspection profile if the inspection exists.
            if (convert2DiamondInspectionTool != null) {
                inspectionProfile.setToolEnabled(convert2DiamondInspectionTool.shortName, newState)
                inspectionProfile.modifyProfile {
                    it.commit()
                }
            }
        }

        // Get the ManualArrayCopy inspection wrapper from the inspection profile.
        val manualArrayCopyInspectionTool = inspectionProfile.getInspectionTool("ManualArrayCopy", project)

        // Set the UI component to the stored states of the manualArrayCopyInspection inspection.
        enableManualArrayCopyInspectionCheckBox.isSelected =
            manualArrayCopyInspectionTool != null && inspectionProfile.isToolEnabled(manualArrayCopyInspectionTool.displayKey)

        // Add a change listener to enable or disable the manualArrayCopyInspection inspection based on user interaction.
        enableManualArrayCopyInspectionCheckBox.addChangeListener {
            val newState = enableManualArrayCopyInspectionCheckBox.isSelected  // Read the new state.

            // Commit the changes to the inspection profile if the inspection exists.
            if (manualArrayCopyInspectionTool != null) {
                inspectionProfile.setToolEnabled(manualArrayCopyInspectionTool.shortName, newState)
                inspectionProfile.modifyProfile {
                    it.commit()
                }
            }
        }

        // ------------------------------- Setting Panel Formation -------------------------------

        val separator_1 = JSeparator()
        val separator_2 = JSeparator()
        val separator_3 = JSeparator()
        val separator_4 = JSeparator()

        panel = FormBuilder.createFormBuilder()
            .addComponent(importSettingsButton)
            .addComponent(separator_1)
            .addComponent(methodLengthInspectionTitleLabel)
            .addComponent(enableMethodLengthInspectionCheckBox)
            .addLabeledComponent("Max Method Length:", maxLengthTextField)
            .addComponent(separator_2)
            .addComponent(controlStructInspectionsTitleLabel)
            .addComponent(enableControlStructInspectionsCheckBox)
            .addLabeledComponent("If Statement Usage Specification:", ifStatementUsageTextField)
            .addLabeledComponent("Switch Statement Usage Specification:", switchStatementUsageTextField)
            .addLabeledComponent("While Loop Usage Specification:", whileLoopUsageTextField)
            .addLabeledComponent("For Loop Usage Specification:", forLoopUsageTextField)
            .addLabeledComponent("Enhanced For Loop Usage Specification:", foreachLoopUsageTextField)
            .addComponent(controlStructInspectionsNotesLabel)
            .addComponent(separator_3)
            .addComponent(declarationIssueInspectionsTitleLabel)
            .addComponent(enableUnusedInspectionCheckBox)
            .addComponent(enableCanBeFinalInspectionCheckBox)
            .addComponent(separator_4)
            .addComponent(javaLanguageLevelInspectionsTitleLabel)
            .addComponent(enableSequencedCollectionMethodCanBeUsedInspectionCheckBox)
            .addComponent(enableForCanBeForeachInspectionCheckBox)
            .addComponent(enableConvert2DiamondInspectionCheckBox)
            .addComponent(enableManualArrayCopyInspectionCheckBox)
            .panel

        // Add an action listener for input settings file importation.
        importSettingsButton.addActionListener {

            // Open a file choose and allow the user to choose the target JSON file.
            val fileChooser = JFileChooser()
            fileChooser.fileFilter = FileNameExtensionFilter("JSON Files", "json")
            val result = fileChooser.showOpenDialog(panel)

            // Check if the file read is success.
            if (result == JFileChooser.APPROVE_OPTION) {

                // Find and parse the import file.
                val selectedFile = fileChooser.selectedFile
                importInputSettingsFile(selectedFile)
            }
        }
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
        val methodLengthChanged =
            methodLengthInspectionTool != null && enableMethodLengthInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(
                methodLengthInspectionTool.displayKey
            )
        val maxLengthChanged = (methodLengthInspection?.maxLength?.toString() ?: "10") != maxLengthTextField.text

        // --------------------------- Custom control structure Inspections ---------------------------

        // -------------- If Statement Inspection -------------

        // Get the IfStatementInspection inspection wrapper from the inspection profile.
        val ifStatementInspectionTool = inspectionProfile.getInspectionTool("IfStatementUsageInspection", project)

        // Get the IfStatementInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not IfStatementInspection.
        val ifStatementInspection = ifStatementInspectionTool?.tool as? IfStatementUsageInspection

        // Set the UI components to the stored states of the control structure inspections.
        val ifStatementChanged =
            ifStatementInspectionTool != null && enableControlStructInspectionsCheckBox.isSelected != inspectionProfile.isToolEnabled(
                ifStatementInspectionTool.displayKey
            )
        val ifStatementSpecsChanged = (ifStatementInspection?.specs ?: "") != ifStatementUsageTextField.text

        // -------------- Switch Statement Inspection -------------

        // Get the SwitchStatementInspection inspection wrapper from the inspection profile.
        val switchStatementInspectionTool =
            inspectionProfile.getInspectionTool("SwitchStatementUsageInspection", project)

        // Get the SwitchStatementInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not SwitchStatementInspection.
        val switchStatementInspection = switchStatementInspectionTool?.tool as? SwitchStatementUsageInspection

        // Set the UI components to the stored states of the control structure inspections.
        val switchStatementChanged =
            switchStatementInspectionTool != null && enableControlStructInspectionsCheckBox.isSelected != inspectionProfile.isToolEnabled(
                switchStatementInspectionTool.displayKey
            )
        val switchStatementSpecsChanged = (switchStatementInspection?.specs ?: "") != switchStatementUsageTextField.text

        // -------------- While Loop Inspection -------------

        // Get the whileLoopInspection inspection wrapper from the inspection profile.
        val whileLoopInspectionTool = inspectionProfile.getInspectionTool("WhileLoopUsageInspection", project)

        // Get the whileLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not whileLoopInspection.
        val whileLoopInspection = whileLoopInspectionTool?.tool as? WhileLoopUsageInspection

        // Set the UI components to the stored states of the control structure inspections.
        val whileLoopChanged =
            whileLoopInspectionTool != null && enableControlStructInspectionsCheckBox.isSelected != inspectionProfile.isToolEnabled(
                whileLoopInspectionTool.displayKey
            )
        val whileLoopSpecsChanged = (whileLoopInspection?.specs ?: "") != whileLoopUsageTextField.text

        // -------------- For Loop Inspection -------------

        // Get the forLoopInspection inspection wrapper from the inspection profile.
        val forLoopInspectionTool = inspectionProfile.getInspectionTool("ForLoopUsageInspection", project)

        // Get the forLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not forLoopInspection.
        val forLoopInspection = forLoopInspectionTool?.tool as? ForLoopUsageInspection

        // Set the UI components to the stored states of the control structure inspections.
        val forLoopChanged =
            forLoopInspectionTool != null && enableControlStructInspectionsCheckBox.isSelected != inspectionProfile.isToolEnabled(
                forLoopInspectionTool.displayKey
            )
        val forLoopSpecsChanged = (forLoopInspection?.specs ?: "") != forLoopUsageTextField.text

        // -------------- Enhanced For Loop Inspection -------------

        // Get the foreachLoopInspection inspection wrapper from the inspection profile.
        val foreachLoopInspectionTool = inspectionProfile.getInspectionTool("ForeachLoopUsageInspection", project)

        // Get the foreachLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not foreachLoopInspection.
        val foreachLoopInspection = foreachLoopInspectionTool?.tool as? ForeachLoopUsageInspection

        // Set the UI components to the stored states of the control structure inspections.
        val foreachLoopChanged =
            foreachLoopInspectionTool != null && enableControlStructInspectionsCheckBox.isSelected != inspectionProfile.isToolEnabled(
                foreachLoopInspectionTool.displayKey
            )
        val foreachLoopSpecsChanged = (foreachLoopInspection?.specs ?: "") != foreachLoopUsageTextField.text

        // -------------- Final Code for Control Structures  -------------

        val controlStructureChanged =
            ifStatementChanged || switchStatementChanged || whileLoopChanged || forLoopChanged || foreachLoopChanged
        val controlStructureSpecsChanged =
            ifStatementSpecsChanged || switchStatementSpecsChanged || whileLoopSpecsChanged || forLoopSpecsChanged || foreachLoopSpecsChanged

        // --------------------------- Declaration Issue Inspection ---------------------------

        // Get the UnusedInspection inspection wrapper from the inspection profile.
        val UnusedInspectionTool = inspectionProfile.getInspectionTool("unused", project)

        // Set the UI component to the stored states of the UnusedInspection inspection.
        val unusedChanged = UnusedInspectionTool != null && enableUnusedInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(UnusedInspectionTool.displayKey)

        // Get the CanBeFinalInspection inspection wrapper from the inspection profile.
        val CanBeFinalInspectionTool = inspectionProfile.getInspectionTool("CanBeFinal", project)

        // Set the UI component to the stored states of the CanBeFinalInspection inspection.
        val canBeFinalChanged = CanBeFinalInspectionTool != null && enableCanBeFinalInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(CanBeFinalInspectionTool.displayKey)

        val declarationIssueChanged = unusedChanged || canBeFinalChanged

        // --------------------------- Java Language Level Issue Inspection ---------------------------

        // Get the sequencedCollectionInspection inspection wrapper from the inspection profile.
        val sequencedCollectionInspectionTool = inspectionProfile.getInspectionTool("SequencedCollectionMethodCanBeUsed", project)

        // Set the UI component to the stored states of the sequencedCollectionInspection inspection.
        val sequencedCollectionChanged = sequencedCollectionInspectionTool != null && enableSequencedCollectionMethodCanBeUsedInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(sequencedCollectionInspectionTool.displayKey)

        // Get the forCanBeForeachInspection inspection wrapper from the inspection profile.
        val forCanBeForeachInspectionTool = inspectionProfile.getInspectionTool("ForCanBeForeach", project)

        // Set the UI component to the stored states of the forCanBeForeachInspection inspection.
        val forCanBeForeachChanged = forCanBeForeachInspectionTool != null && enableForCanBeForeachInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(forCanBeForeachInspectionTool.displayKey)

        // Get the convert2DiamondInspection inspection wrapper from the inspection profile.
        val convert2DiamondInspectionTool = inspectionProfile.getInspectionTool("Convert2Diamond", project)

        // Set the UI component to the stored states of the sequencedCollectionInspection inspection.
        val convert2DiamondChanged = convert2DiamondInspectionTool != null && enableConvert2DiamondInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(convert2DiamondInspectionTool.displayKey)

        // Get the manualArrayCopyInspection inspection wrapper from the inspection profile.
        val manualArrayCopyInspectionTool = inspectionProfile.getInspectionTool("ManualArrayCopy", project)

        // Set the UI component to the stored states of the manualArrayCopyInspection inspection.
        val manualArrayCopyChanged = manualArrayCopyInspectionTool != null && enableManualArrayCopyInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(manualArrayCopyInspectionTool.displayKey)

        val javaLanguageLevelChanged = sequencedCollectionChanged || forCanBeForeachChanged || convert2DiamondChanged || manualArrayCopyChanged

        // ------------------------------- Return the final result -------------------------------

        return methodLengthChanged || maxLengthChanged || controlStructureChanged || controlStructureSpecsChanged || declarationIssueChanged || javaLanguageLevelChanged
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
        val switchStatementInspectionTool =
            inspectionProfile.getInspectionTool("SwitchStatementUsageInspection", project)

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

        // --------------------------- Declaration Issue Inspection ---------------------------

        // Get the UnusedInspection inspection wrapper from the inspection profile.
        val UnusedInspectionTool = inspectionProfile.getInspectionTool("unused", project)

        // If the UnusedInspection inspection instance exists.
        if (UnusedInspectionTool != null) {
            val newMethodState = enableUnusedInspectionCheckBox.isSelected  // Read the new state.
            inspectionProfile.setToolEnabled(UnusedInspectionTool.shortName, newMethodState)
        }

        // Get the CanBeFinalInspection inspection wrapper from the inspection profile.
        val CanBeFinalInspectionTool = inspectionProfile.getInspectionTool("CanBeFinal", project)

        // If the CanBeFinalInspection inspection instance exists.
        if (CanBeFinalInspectionTool != null) {
            val newMethodState = enableCanBeFinalInspectionCheckBox.isSelected  // Read the new state.
            inspectionProfile.setToolEnabled(CanBeFinalInspectionTool.shortName, newMethodState)
        }

        // --------------------------- Java Language Level Issue Inspection ---------------------------

        // Get the sequencedCollectionInspection inspection wrapper from the inspection profile.
        val sequencedCollectionInspectionTool = inspectionProfile.getInspectionTool("SequencedCollectionMethodCanBeUsed", project)

        // If the sequencedCollectionInspection inspection instance exists.
        if (sequencedCollectionInspectionTool != null) {
            val newMethodState = enableSequencedCollectionMethodCanBeUsedInspectionCheckBox.isSelected  // Read the new state.
            inspectionProfile.setToolEnabled(sequencedCollectionInspectionTool.shortName, newMethodState)
        }

        // Get the forCanBeForeachInspection inspection wrapper from the inspection profile.
        val forCanBeForeachInspectionTool = inspectionProfile.getInspectionTool("ForCanBeForeach", project)

        // If the forCanBeForeachInspection inspection instance exists.
        if (forCanBeForeachInspectionTool != null) {
            val newMethodState = enableForCanBeForeachInspectionCheckBox.isSelected  // Read the new state.
            inspectionProfile.setToolEnabled(forCanBeForeachInspectionTool.shortName, newMethodState)
        }

        // Get the convert2DiamondInspection inspection wrapper from the inspection profile.
        val convert2DiamondInspectionTool = inspectionProfile.getInspectionTool("Convert2Diamond", project)

        // If the convert2DiamondInspection inspection instance exists.
        if (convert2DiamondInspectionTool != null) {
            val newMethodState = enableConvert2DiamondInspectionCheckBox.isSelected  // Read the new state.
            inspectionProfile.setToolEnabled(convert2DiamondInspectionTool.shortName, newMethodState)
        }

        // Get the manualArrayCopyInspection inspection wrapper from the inspection profile.
        val manualArrayCopyInspectionTool = inspectionProfile.getInspectionTool("ManualArrayCopy", project)

        // If the manualArrayCopyInspection inspection instance exists.
        if (manualArrayCopyInspectionTool != null) {
            val newMethodState = enableManualArrayCopyInspectionCheckBox.isSelected  // Read the new state.
            inspectionProfile.setToolEnabled(manualArrayCopyInspectionTool.shortName, newMethodState)
        }

        // --------------------------- Final Code ---------------------------

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
        enableMethodLengthInspectionCheckBox.isSelected =
            methodLengthInspectionTool != null && inspectionProfile.isToolEnabled(methodLengthInspectionTool.displayKey)
        maxLengthTextField.text = methodLengthInspection?.maxLength?.toString() ?: "10"

        // --------------------------- Custom control structure Inspections ---------------------------

        // -------------- If Statement Inspection -------------

        // Get the IfStatementInspection inspection wrapper from the inspection profile.
        val ifStatementInspectionTool = inspectionProfile.getInspectionTool("IfStatementUsageInspection", project)

        // Get the IfStatementInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not ifStatementInspection.
        val ifStatementInspection = ifStatementInspectionTool?.tool as? IfStatementUsageInspection

        // Set the UI components to the stored states of the control structure inspections.
        val ifStatementState =
            ifStatementInspectionTool != null && inspectionProfile.isToolEnabled(ifStatementInspectionTool.displayKey)
        ifStatementUsageTextField.text = ifStatementInspection?.specs ?: ""

        // -------------- Switch Statement Inspection -------------

        // Get the SwitchStatementInspection inspection wrapper from the inspection profile.
        val switchStatementInspectionTool =
            inspectionProfile.getInspectionTool("SwitchStatementUsageInspection", project)

        // Get the SwitchStatementInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not switchStatementInspection.
        val switchStatementInspection = switchStatementInspectionTool?.tool as? SwitchStatementUsageInspection

        // Set the UI components to the stored states of the control structure inspections.
        val switchStatementState =
            switchStatementInspectionTool != null && inspectionProfile.isToolEnabled(switchStatementInspectionTool.displayKey)
        switchStatementUsageTextField.text = switchStatementInspection?.specs ?: ""

        // -------------- While Loop Inspection -------------

        // Get the whileLoopInspection inspection wrapper from the inspection profile.
        val whileLoopInspectionTool = inspectionProfile.getInspectionTool("WhileLoopUsageInspection", project)

        // Get the whileLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not whileLoopInspection.
        val whileLoopInspection = whileLoopInspectionTool?.tool as? WhileLoopUsageInspection

        // Set the UI components to the stored states of the whileLoopInspection inspection.
        val whileLoopState =
            whileLoopInspectionTool != null && inspectionProfile.isToolEnabled(whileLoopInspectionTool.displayKey)
        whileLoopUsageTextField.text = whileLoopInspection?.specs ?: ""

        // -------------- For Loop Inspection -------------

        // Get the forLoopInspection inspection wrapper from the inspection profile.
        val forLoopInspectionTool = inspectionProfile.getInspectionTool("ForLoopUsageInspection", project)

        // Get the forLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not forLoopInspection.
        val forLoopInspection = forLoopInspectionTool?.tool as? ForLoopUsageInspection

        // Set the UI components to the stored states of the forLoopInspection inspection.
        val forLoopState =
            forLoopInspectionTool != null && inspectionProfile.isToolEnabled(forLoopInspectionTool.displayKey)
        forLoopUsageTextField.text = forLoopInspection?.specs ?: ""

        // -------------- Enhanced For Loop Inspection -------------

        // Get the forLoopInspection inspection wrapper from the inspection profile.
        val foreachLoopInspectionTool = inspectionProfile.getInspectionTool("ForeachLoopUsageInspection", project)

        // Get the foreachLoopInspection inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not foreachLoopInspection.
        val foreachLoopInspection = foreachLoopInspectionTool?.tool as? ForeachLoopUsageInspection

        // Set the UI components to the stored states of the foreachLoopInspection inspection.
        val foreachLoopState =
            foreachLoopInspectionTool != null && inspectionProfile.isToolEnabled(foreachLoopInspectionTool.displayKey)
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

        // --------------------------- Declaration Issue Inspection ---------------------------

        // Get the UnusedInspection inspection wrapper from the inspection profile.
        val UnusedInspectionTool = inspectionProfile.getInspectionTool("unused", project)

        // Set the UI component to the stored states of the UnusedInspection inspection.
        enableUnusedInspectionCheckBox.isSelected =
            UnusedInspectionTool != null && inspectionProfile.isToolEnabled(UnusedInspectionTool.displayKey)

        // Get the CanBeFinalInspection inspection wrapper from the inspection profile.
        val CanBeFinalInspectionTool = inspectionProfile.getInspectionTool("CanBeFinal", project)

        // Set the UI component to the stored states of the CanBeFinalInspection inspection.
        enableCanBeFinalInspectionCheckBox.isSelected =
            CanBeFinalInspectionTool != null && inspectionProfile.isToolEnabled(CanBeFinalInspectionTool.displayKey)

        // --------------------------- Java Language Level Issue Inspection ---------------------------

        // Get the sequencedCollectionInspection inspection wrapper from the inspection profile.
        val sequencedCollectionInspectionTool = inspectionProfile.getInspectionTool("SequencedCollectionMethodCanBeUsed", project)

        // Set the UI component to the stored states of the sequencedCollectionInspection inspection.
        enableSequencedCollectionMethodCanBeUsedInspectionCheckBox.isSelected =
            sequencedCollectionInspectionTool != null && inspectionProfile.isToolEnabled(sequencedCollectionInspectionTool.displayKey)

        // Get the ForCanBeForeach inspection wrapper from the inspection profile.
        val forCanBeForeachInspectionTool = inspectionProfile.getInspectionTool("ForCanBeForeach", project)

        // Set the UI component to the stored states of the forCanBeForeachInspection inspection.
        enableForCanBeForeachInspectionCheckBox.isSelected =
            forCanBeForeachInspectionTool != null && inspectionProfile.isToolEnabled(forCanBeForeachInspectionTool.displayKey)

        // Get the Convert2Diamond inspection wrapper from the inspection profile.
        val convert2DiamondInspectionTool = inspectionProfile.getInspectionTool("Convert2Diamond", project)

        // Set the UI component to the stored states of the convert2DiamondInspection inspection.
        enableConvert2DiamondInspectionCheckBox.isSelected =
            convert2DiamondInspectionTool != null && inspectionProfile.isToolEnabled(convert2DiamondInspectionTool.displayKey)

        // Get the ManualArrayCopy inspection wrapper from the inspection profile.
        val manualArrayCopyInspectionTool = inspectionProfile.getInspectionTool("ManualArrayCopy", project)

        // Set the UI component to the stored states of the manualArrayCopyInspection inspection.
        enableManualArrayCopyInspectionCheckBox.isSelected =
            manualArrayCopyInspectionTool != null && inspectionProfile.isToolEnabled(manualArrayCopyInspectionTool.displayKey)
    }

    override fun getDisplayName(): String {
        return "EasyCode Linter Panel"
    }

    // A helper function to handle the importatin of input settings file.
    private fun importInputSettingsFile(file: File) {
        try {

            // Create a FileReader to read from the input file
            FileReader(file).use { reader ->

                // Parse the file into a JsonObject using Google GSON
                val json = Gson().fromJson(reader, JsonObject::class.java)

                // Parsing "MethodLengthInspection" settings
                json.getAsJsonObject("MethodLengthInspection")?.let { methodLengthJson ->
                    enableMethodLengthInspectionCheckBox.isSelected = methodLengthJson.get("status").asBoolean
                    maxLengthTextField.text = methodLengthJson.get("maxLength").asString
                }

                // Parse "ControlStructuresInspection" settings
                json.getAsJsonObject("ControlStructuresInspection")?.let { controlStructuresJson ->
                    enableControlStructInspectionsCheckBox.isSelected = controlStructuresJson.get("status").asBoolean
                    ifStatementUsageTextField.text = controlStructuresJson.get("IfStatementSpecs").asString
                    switchStatementUsageTextField.text = controlStructuresJson.get("SwitchStatementSpecs").asString
                    whileLoopUsageTextField.text = controlStructuresJson.get("WhileLoopSpecs").asString
                    forLoopUsageTextField.text = controlStructuresJson.get("ForLoopSpecs").asString
                    foreachLoopUsageTextField.text = controlStructuresJson.get("EnhancedForLoopSpecs").asString
                }

                // Parse "CanBeFinalInspection" settings
                json.getAsJsonObject("CanBeFinalInspection")?.let { canBeFinalJson ->
                    enableCanBeFinalInspectionCheckBox.isSelected = canBeFinalJson.get("status").asBoolean
                }

                // Parse "UnusedInspection" settings
                json.getAsJsonObject("UnusedInspection")?.let { unusedJson ->
                    enableUnusedInspectionCheckBox.isSelected = unusedJson.get("status").asBoolean
                }

                // Parse "SequencedCollectionMethodCanBeUsedInspection" settings
                json.getAsJsonObject("SequencedCollectionMethodCanBeUsedInspection")?.let { sequencedCollectionJson ->
                    enableSequencedCollectionMethodCanBeUsedInspectionCheckBox.isSelected = sequencedCollectionJson.get("status").asBoolean
                }

                // Parse "ForCanBeForeachInspection" settings
                json.getAsJsonObject("ForCanBeForeachInspection")?.let { forCanBeForeachJson ->
                    enableForCanBeForeachInspectionCheckBox.isSelected = forCanBeForeachJson.get("status").asBoolean
                }

                // Parse "Convert2DiamondInspection" settings
                json.getAsJsonObject("Convert2DiamondInspection")?.let { convert2DiamondJson ->
                    enableConvert2DiamondInspectionCheckBox.isSelected = convert2DiamondJson.get("status").asBoolean
                }

                // Parse "ManualArrayCopyInspection" settings
                json.getAsJsonObject("ManualArrayCopyInspection")?.let { manualArrayCopyJson ->
                    enableManualArrayCopyInspectionCheckBox.isSelected = manualArrayCopyJson.get("status").asBoolean
                }
            }

        } catch (e: Exception) {

            // Show an error message when the format of the input settings file is not correct.
            JOptionPane.showMessageDialog(panel, "Error reading settings file: " + e.message, "Import Error", JOptionPane.ERROR_MESSAGE)
        }
    }

}