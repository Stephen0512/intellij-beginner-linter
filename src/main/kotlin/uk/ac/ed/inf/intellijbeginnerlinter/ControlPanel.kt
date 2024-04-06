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

    // Declare a UI button component for the importation of the input settings file.
    private val importSettingsButton = JButton("Import Settings")

    // Declare two UI components for the Customised Method Length Inspection.
    private val enableMethodLengthInspectionCheckBox = JCheckBox("Enable Inspection")
    private val maxLengthTextField = JTextField()

    // Declare six UI components for the Customised Control Structure Inspections.
    private val enableControlStructInspectionsCheckBox = JCheckBox("Enable Inspections")
    private val ifStatementUsageTextField = JTextField()
    private val switchStatementUsageTextField = JTextField()
    private val whileLoopUsageTextField = JTextField()
    private val forLoopUsageTextField = JTextField()
    private val foreachLoopUsageTextField = JTextField()

    // Declare two UI components for the Declaration Issue Inspections.
    private val enableUnusedInspectionCheckBox = JCheckBox("Enable Unused Declaration Inspection")
    private val enableCanBeFinalInspectionCheckBox =
        JCheckBox("Enable Declaration can have ‘Final’ Modifier Inspection")

    // Declare four UI components for the Java Language Level Issue Inspections.
    private val enableSequencedCollectionMethodCanBeUsedInspectionCheckBox =
        JCheckBox("Enable SequencedCollection Method can be Used Inspection")
    private val enableForCanBeForeachInspectionCheckBox =
        JCheckBox("Enable ‘For’ Loop can be Replaced with Enhanced For Loop Inspection")
    private val enableConvert2DiamondInspectionCheckBox =
        JCheckBox("Enable Explicit Type can be Replaced with ‘<>’ Inspection:")
    private val enableManualArrayCopyInspectionCheckBox = JCheckBox("Enable Manual Array Copy Inspection")

    /**
     * Initialization function used to create the control panel and create listeners to different UI components.
     * The control panel will be updated using the current settings stored in the inspection profile.
     * Default values will be entered if no settings have been set before.
     */
    init {
        // Retrieves the existing inspection profile for the current project.
        val inspectionProfile = InspectionProjectProfileManager.getInstance(project).currentProfile

        // --------------------------- Custom Method Length Inspection ---------------------------

        // Create a title label for the Method Length Inspection.
        val methodLengthInspectionTitleLabel = JLabel("Method Length Inspection Settings")

        // Change the font of the title label to bold and increase font size.
        val methodLengthTitleFont = methodLengthInspectionTitleLabel.font.deriveFont(
            Font.BOLD,
            methodLengthInspectionTitleLabel.font.size * 1.2f
        )
        methodLengthInspectionTitleLabel.font = methodLengthTitleFont

        // Get the Method Length Inspection wrapper from the inspection profile.
        val methodLengthInspectionTool = inspectionProfile.getInspectionTool("MethodLengthInspection", project)

        // Get the Method Length Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val methodLengthInspection = methodLengthInspectionTool?.tool as? MethodLengthInspection

        // Set the value of the UI components to the stored states in the inspection profile.
        enableMethodLengthInspectionCheckBox.isSelected =
            methodLengthInspectionTool != null && inspectionProfile.isToolEnabled(methodLengthInspectionTool.displayKey)
        maxLengthTextField.text = methodLengthInspection?.maxLength?.toString() ?: "10"

        // Add a change listener to the enabled status of the Method Length Inspection based on user interactions.
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

        // --------------------------- Custom Control Structure Inspections ---------------------------

        // Create a title label for the Control Structure Inspections.
        val controlStructInspectionsTitleLabel = JLabel("Control Structure Inspections Settings")

        // Create the notes for the text fields used in this inspection.
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

        // Get the If Statement Inspection wrapper from the inspection profile.
        val ifStatementInspectionTool = inspectionProfile.getInspectionTool("IfStatementUsageInspection", project)

        // Get the If Statement Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val ifStatementInspection = ifStatementInspectionTool?.tool as? IfStatementUsageInspection

        // Set the value of the UI components to the stored states in the inspection profile.
        val ifStatementState =
            ifStatementInspectionTool != null && inspectionProfile.isToolEnabled(ifStatementInspectionTool.displayKey)
        ifStatementUsageTextField.text = ifStatementInspection?.specs ?: ""

        // -------------- Switch Statement Inspection -------------

        // Get the Switch Statement Inspection wrapper from the inspection profile.
        val switchStatementInspectionTool =
            inspectionProfile.getInspectionTool("SwitchStatementUsageInspection", project)

        // Get the Switch Statement Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val switchStatementInspection = switchStatementInspectionTool?.tool as? SwitchStatementUsageInspection

        // Set the value of the UI components to the stored states in the inspection profile.
        val switchStatementState =
            switchStatementInspectionTool != null && inspectionProfile.isToolEnabled(switchStatementInspectionTool.displayKey)
        switchStatementUsageTextField.text = switchStatementInspection?.specs ?: ""

        // -------------- While Loop Inspection -------------

        // Get the While Loop Inspection wrapper from the inspection profile.
        val whileLoopInspectionTool = inspectionProfile.getInspectionTool("WhileLoopUsageInspection", project)

        // Get the While Loop Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val whileLoopInspection = whileLoopInspectionTool?.tool as? WhileLoopUsageInspection

        // Set the value of the UI components to the stored states in the inspection profile.
        val whileLoopState =
            whileLoopInspectionTool != null && inspectionProfile.isToolEnabled(whileLoopInspectionTool.displayKey)
        whileLoopUsageTextField.text = whileLoopInspection?.specs ?: ""

        // -------------- For Loop Inspection -------------

        // Get the For Loop Inspection wrapper from the inspection profile.
        val forLoopInspectionTool = inspectionProfile.getInspectionTool("ForLoopUsageInspection", project)

        // Get the For Loop Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val forLoopInspection = forLoopInspectionTool?.tool as? ForLoopUsageInspection

        // Set the value of the UI components to the stored states in the inspection profile.
        val forLoopState =
            forLoopInspectionTool != null && inspectionProfile.isToolEnabled(forLoopInspectionTool.displayKey)
        forLoopUsageTextField.text = forLoopInspection?.specs ?: ""

        // -------------- Enhanced For Loop Inspection -------------

        // Get the Enhanced For Loop Inspection wrapper from the inspection profile.
        val foreachLoopInspectionTool = inspectionProfile.getInspectionTool("ForeachLoopUsageInspection", project)

        // Get the Enhanced For Loop Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val foreachLoopInspection = foreachLoopInspectionTool?.tool as? ForeachLoopUsageInspection

        // Set the value of the UI components to the stored states in the inspection profile.
        val foreachLoopState =
            foreachLoopInspectionTool != null && inspectionProfile.isToolEnabled(foreachLoopInspectionTool.displayKey)
        foreachLoopUsageTextField.text = foreachLoopInspection?.specs ?: ""

        // -------------- Final Code for Control Structures  -------------

        // Check all the Control Structure Inspections have the same state.
        if (ifStatementState == switchStatementState == whileLoopState == forLoopState == foreachLoopState) {
            enableControlStructInspectionsCheckBox.isSelected = ifStatementState
        } else {
            // Set all the Control Structure Inspections to false if the status are not consistent.
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

            // Commit the changes to the inspection profile.
            inspectionProfile.modifyProfile {
                it.commit()
            }
        }

        // Add a change listener to the enabled status of the Control Structure Inspections based on user interactions.
        enableControlStructInspectionsCheckBox.addChangeListener {
            val newState = enableControlStructInspectionsCheckBox.isSelected  // Read the new state.

            // Commit the changes to the inspection profile if the ifStatementInspectionTool exists.
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

            // Commit the changes to the inspection profile.
            inspectionProfile.modifyProfile {
                it.commit()
            }
        }

        // --------------------------- Declaration Issue Inspection ---------------------------

        // Create a title label for the Declaration Issue Inspections.
        val declarationIssueInspectionsTitleLabel = JLabel("Declaration Issue Inspections Settings")

        // Change the font of the title label to bold and increase font size
        val declarationIssueTitleFont = declarationIssueInspectionsTitleLabel.font.deriveFont(
            Font.BOLD,
            declarationIssueInspectionsTitleLabel.font.size * 1.2f
        )
        declarationIssueInspectionsTitleLabel.font = declarationIssueTitleFont

        // Get the Unused Inspection wrapper from the inspection profile.
        val unusedInspectionTool = inspectionProfile.getInspectionTool("unused", project)

        // Set the value of the UI components to the stored states in the inspection profile.
        enableUnusedInspectionCheckBox.isSelected =
            unusedInspectionTool != null && inspectionProfile.isToolEnabled(unusedInspectionTool.displayKey)

        // Add a change listener to enable or disable the Unused Inspection based on user interactions.
        enableUnusedInspectionCheckBox.addChangeListener {
            val newState = enableUnusedInspectionCheckBox.isSelected  // Read the new state.

            // Commit the changes to the inspection profile if the inspection exists.
            if (unusedInspectionTool != null) {
                inspectionProfile.setToolEnabled(unusedInspectionTool.shortName, newState)
                inspectionProfile.modifyProfile {
                    it.commit()
                }
            }
        }

        // Get the Can Be Final Inspection wrapper from the inspection profile.
        val canBeFinalInspectionTool = inspectionProfile.getInspectionTool("CanBeFinal", project)

        // Set the value of the UI components to the stored states in the inspection profile.
        enableCanBeFinalInspectionCheckBox.isSelected =
            canBeFinalInspectionTool != null && inspectionProfile.isToolEnabled(canBeFinalInspectionTool.displayKey)

        // Add a change listener to enable or disable the Can Be Final Inspection based on user interactions.
        enableCanBeFinalInspectionCheckBox.addChangeListener {
            val newState = enableCanBeFinalInspectionCheckBox.isSelected  // Read the new state.

            // Commit the changes to the inspection profile if the inspection exists.
            if (canBeFinalInspectionTool != null) {
                inspectionProfile.setToolEnabled(canBeFinalInspectionTool.shortName, newState)
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

        // Get the sequenced Collection Inspection wrapper from the inspection profile.
        val sequencedCollectionInspectionTool =
            inspectionProfile.getInspectionTool("SequencedCollectionMethodCanBeUsed", project)

        // Set the value of the UI components to the stored states in the inspection profile.
        enableSequencedCollectionMethodCanBeUsedInspectionCheckBox.isSelected =
            sequencedCollectionInspectionTool != null && inspectionProfile.isToolEnabled(
                sequencedCollectionInspectionTool.displayKey
            )

        // Add a change listener to enable or disable the sequenced Collection Inspection based on user interaction.
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

        // Get the For Can Be Foreach Inspection wrapper from the inspection profile.
        val forCanBeForeachInspectionTool = inspectionProfile.getInspectionTool("ForCanBeForeach", project)

        // Set the value of the UI components to the stored states in the inspection profile.
        enableForCanBeForeachInspectionCheckBox.isSelected =
            forCanBeForeachInspectionTool != null && inspectionProfile.isToolEnabled(forCanBeForeachInspectionTool.displayKey)

        // Add a change listener to enable or disable the For Can Be Foreach Inspection based on user interactions.
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

        // Get the Convert 2Diamond Inspection wrapper from the inspection profile.
        val convert2DiamondInspectionTool = inspectionProfile.getInspectionTool("Convert2Diamond", project)

        // Set the value of the UI components to the stored states in the inspection profile.
        enableConvert2DiamondInspectionCheckBox.isSelected =
            convert2DiamondInspectionTool != null && inspectionProfile.isToolEnabled(convert2DiamondInspectionTool.displayKey)

        // Add a change listener to enable or disable the Convert 2Diamond Inspection based on user interactions.
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

        // Get the Manual Array Copy Inspection wrapper from the inspection profile.
        val manualArrayCopyInspectionTool = inspectionProfile.getInspectionTool("ManualArrayCopy", project)

        // Set the value of the UI components to the stored states in the inspection profile.
        enableManualArrayCopyInspectionCheckBox.isSelected =
            manualArrayCopyInspectionTool != null && inspectionProfile.isToolEnabled(manualArrayCopyInspectionTool.displayKey)

        // Add a change listener to enable or disable the Manual Array Copy Inspection based on user interactions.
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

        // Create Swing separator for the control panel of the plugin.
        val separator1 = JSeparator()
        val separator2 = JSeparator()
        val separator3 = JSeparator()
        val separator4 = JSeparator()

        // Build the control panel of the tool using UI components created before.
        panel = FormBuilder.createFormBuilder()
            .addComponent(importSettingsButton)
            .addComponent(separator1)
            .addComponent(methodLengthInspectionTitleLabel)
            .addComponent(enableMethodLengthInspectionCheckBox)
            .addLabeledComponent("Max Method Length:", maxLengthTextField)
            .addComponent(separator2)
            .addComponent(controlStructInspectionsTitleLabel)
            .addComponent(enableControlStructInspectionsCheckBox)
            .addLabeledComponent("If Statement Usage Specification:", ifStatementUsageTextField)
            .addLabeledComponent("Switch Statement Usage Specification:", switchStatementUsageTextField)
            .addLabeledComponent("While Loop Usage Specification:", whileLoopUsageTextField)
            .addLabeledComponent("For Loop Usage Specification:", forLoopUsageTextField)
            .addLabeledComponent("Enhanced For Loop Usage Specification:", foreachLoopUsageTextField)
            .addComponent(controlStructInspectionsNotesLabel)
            .addComponent(separator3)
            .addComponent(declarationIssueInspectionsTitleLabel)
            .addComponent(enableUnusedInspectionCheckBox)
            .addComponent(enableCanBeFinalInspectionCheckBox)
            .addComponent(separator4)
            .addComponent(javaLanguageLevelInspectionsTitleLabel)
            .addComponent(enableSequencedCollectionMethodCanBeUsedInspectionCheckBox)
            .addComponent(enableForCanBeForeachInspectionCheckBox)
            .addComponent(enableConvert2DiamondInspectionCheckBox)
            .addComponent(enableManualArrayCopyInspectionCheckBox)
            .panel

        // Add an action listener for the input settings file importation.
        importSettingsButton.addActionListener {

            // Open a file choose and allow the user to choose the target JSON file.
            val fileChooser = JFileChooser()
            fileChooser.fileFilter = FileNameExtensionFilter("JSON Files", "json")
            val result = fileChooser.showOpenDialog(panel)

            // Check if the file read is success.
            if (result == JFileChooser.APPROVE_OPTION) {

                // Parse the settings in the import file to the control panel.
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

        // Get the Method Length Inspection wrapper from the inspection profile.
        val methodLengthInspectionTool = inspectionProfile.getInspectionTool("MethodLengthInspection", project)

        // Get the Method Length Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val methodLengthInspection = methodLengthInspectionTool?.tool as? MethodLengthInspection

        // Compare the results between the panel and the inspection profile.
        val methodLengthChanged =
            methodLengthInspectionTool != null && enableMethodLengthInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(
                methodLengthInspectionTool.displayKey
            )
        val maxLengthChanged = (methodLengthInspection?.maxLength?.toString() ?: "10") != maxLengthTextField.text

        // --------------------------- Custom control structure Inspections ---------------------------

        // -------------- If Statement Inspection -------------

        // Get the If Statement Inspection wrapper from the inspection profile.
        val ifStatementInspectionTool = inspectionProfile.getInspectionTool("IfStatementUsageInspection", project)

        // Get the If Statement Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val ifStatementInspection = ifStatementInspectionTool?.tool as? IfStatementUsageInspection

        // Compare the results between the panel and the inspection profile.
        val ifStatementChanged =
            ifStatementInspectionTool != null && enableControlStructInspectionsCheckBox.isSelected != inspectionProfile.isToolEnabled(
                ifStatementInspectionTool.displayKey
            )
        val ifStatementSpecsChanged = (ifStatementInspection?.specs ?: "") != ifStatementUsageTextField.text

        // -------------- Switch Statement Inspection -------------

        // Get the Switch Statement Inspection wrapper from the inspection profile.
        val switchStatementInspectionTool =
            inspectionProfile.getInspectionTool("SwitchStatementUsageInspection", project)

        // Get the Switch Statement Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val switchStatementInspection = switchStatementInspectionTool?.tool as? SwitchStatementUsageInspection

        // Compare the results between the panel and the inspection profile.
        val switchStatementChanged =
            switchStatementInspectionTool != null && enableControlStructInspectionsCheckBox.isSelected != inspectionProfile.isToolEnabled(
                switchStatementInspectionTool.displayKey
            )
        val switchStatementSpecsChanged = (switchStatementInspection?.specs ?: "") != switchStatementUsageTextField.text

        // -------------- While Loop Inspection -------------

        // Get the While Loop Inspection wrapper from the inspection profile.
        val whileLoopInspectionTool = inspectionProfile.getInspectionTool("WhileLoopUsageInspection", project)

        // Get the While Loop Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val whileLoopInspection = whileLoopInspectionTool?.tool as? WhileLoopUsageInspection

        // Compare the results between the panel and the inspection profile.
        val whileLoopChanged =
            whileLoopInspectionTool != null && enableControlStructInspectionsCheckBox.isSelected != inspectionProfile.isToolEnabled(
                whileLoopInspectionTool.displayKey
            )
        val whileLoopSpecsChanged = (whileLoopInspection?.specs ?: "") != whileLoopUsageTextField.text

        // -------------- For Loop Inspection -------------

        // Get the For Loop Inspection wrapper from the inspection profile.
        val forLoopInspectionTool = inspectionProfile.getInspectionTool("ForLoopUsageInspection", project)

        // Get the For Loop Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val forLoopInspection = forLoopInspectionTool?.tool as? ForLoopUsageInspection

        // Compare the results between the panel and the inspection profile.
        val forLoopChanged =
            forLoopInspectionTool != null && enableControlStructInspectionsCheckBox.isSelected != inspectionProfile.isToolEnabled(
                forLoopInspectionTool.displayKey
            )
        val forLoopSpecsChanged = (forLoopInspection?.specs ?: "") != forLoopUsageTextField.text

        // -------------- Enhanced For Loop Inspection -------------

        // Get the Enhanced For Loop Inspection wrapper from the inspection profile.
        val foreachLoopInspectionTool = inspectionProfile.getInspectionTool("ForeachLoopUsageInspection", project)

        // Get the Enhanced For Loop Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val foreachLoopInspection = foreachLoopInspectionTool?.tool as? ForeachLoopUsageInspection

        // Compare the results between the panel and the inspection profile.
        val foreachLoopChanged =
            foreachLoopInspectionTool != null && enableControlStructInspectionsCheckBox.isSelected != inspectionProfile.isToolEnabled(
                foreachLoopInspectionTool.displayKey
            )
        val foreachLoopSpecsChanged = (foreachLoopInspection?.specs ?: "") != foreachLoopUsageTextField.text

        // -------------- Final Code for Control Structures  -------------

        // Summarise the results of comparison between the panel and the inspection profile.
        val controlStructureChanged =
            ifStatementChanged || switchStatementChanged || whileLoopChanged || forLoopChanged || foreachLoopChanged
        val controlStructureSpecsChanged =
            ifStatementSpecsChanged || switchStatementSpecsChanged || whileLoopSpecsChanged || forLoopSpecsChanged || foreachLoopSpecsChanged

        // --------------------------- Declaration Issue Inspection ---------------------------

        // Get the Unused Inspection wrapper from the inspection profile.
        val unusedInspectionTool = inspectionProfile.getInspectionTool("unused", project)

        // Compare the results between the panel and the inspection profile.
        val unusedChanged =
            unusedInspectionTool != null && enableUnusedInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(
                unusedInspectionTool.displayKey
            )

        // Get the Can Be Final Inspection wrapper from the inspection profile.
        val canBeFinalInspectionTool = inspectionProfile.getInspectionTool("CanBeFinal", project)

        // Compare the results between the panel and the inspection profile.
        val canBeFinalChanged =
            canBeFinalInspectionTool != null && enableCanBeFinalInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(
                canBeFinalInspectionTool.displayKey
            )

        // Summarise the results of comparison between the panel and the inspection profile.
        val declarationIssueChanged = unusedChanged || canBeFinalChanged

        // --------------------------- Java Language Level Issue Inspection ---------------------------

        // Get the Sequenced Collection Inspection wrapper from the inspection profile.
        val sequencedCollectionInspectionTool =
            inspectionProfile.getInspectionTool("SequencedCollectionMethodCanBeUsed", project)

        // Compare the results between the panel and the inspection profile.
        val sequencedCollectionChanged =
            sequencedCollectionInspectionTool != null && enableSequencedCollectionMethodCanBeUsedInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(
                sequencedCollectionInspectionTool.displayKey
            )

        // Get the For Can Be Foreach Inspection wrapper from the inspection profile.
        val forCanBeForeachInspectionTool = inspectionProfile.getInspectionTool("ForCanBeForeach", project)

        // Compare the results between the panel and the inspection profile.
        val forCanBeForeachChanged =
            forCanBeForeachInspectionTool != null && enableForCanBeForeachInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(
                forCanBeForeachInspectionTool.displayKey
            )

        // Get the Convert 2Diamond Inspection wrapper from the inspection profile.
        val convert2DiamondInspectionTool = inspectionProfile.getInspectionTool("Convert2Diamond", project)

        // Compare the results between the panel and the inspection profile.
        val convert2DiamondChanged =
            convert2DiamondInspectionTool != null && enableConvert2DiamondInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(
                convert2DiamondInspectionTool.displayKey
            )

        // Get the Manual Array Copy Inspection wrapper from the inspection profile.
        val manualArrayCopyInspectionTool = inspectionProfile.getInspectionTool("ManualArrayCopy", project)

        // Compare the results between the panel and the inspection profile.
        val manualArrayCopyChanged =
            manualArrayCopyInspectionTool != null && enableManualArrayCopyInspectionCheckBox.isSelected != inspectionProfile.isToolEnabled(
                manualArrayCopyInspectionTool.displayKey
            )

        // Summarise the results of comparison between the panel and the inspection profile.
        val javaLanguageLevelChanged =
            sequencedCollectionChanged || forCanBeForeachChanged || convert2DiamondChanged || manualArrayCopyChanged

        // ------------------------------- Return the final result -------------------------------

        return methodLengthChanged || maxLengthChanged || controlStructureChanged || controlStructureSpecsChanged || declarationIssueChanged || javaLanguageLevelChanged
    }

    override fun apply() {
        // Retrieves the existing inspection profile for the current project.
        val inspectionProfile = InspectionProjectProfileManager.getInstance(project).currentProfile

        // --------------------------- Custom Method Length Inspection ---------------------------

        // Get the Method Length Inspection wrapper from the inspection profile.
        val methodLengthInspectionTool = inspectionProfile.getInspectionTool("MethodLengthInspection", project)

        // Get the Method Length Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val methodLengthInspection = methodLengthInspectionTool?.tool as? MethodLengthInspection

        // If the Method Length Inspection instance exists.
        if (methodLengthInspection != null) {

            // Store the settings in the panel to the inspection profile.
            methodLengthInspection.maxLength = maxLengthTextField.text.toIntOrNull() ?: 0
            val newMethodState = enableMethodLengthInspectionCheckBox.isSelected  // Read the new state.
            inspectionProfile.setToolEnabled(methodLengthInspectionTool.shortName, newMethodState)
        }

        // --------------------------- Custom control structure Inspections ---------------------------

        // Read the new state of the Control Structure Inspections.
        val newControlStructureState = enableControlStructInspectionsCheckBox.isSelected

        // -------------- If Statement Inspection -------------

        // Get the If Statement Inspection wrapper from the inspection profile.
        val ifStatementInspectionTool = inspectionProfile.getInspectionTool("IfStatementUsageInspection", project)

        // Get the If Statement Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val ifStatementInspection = ifStatementInspectionTool?.tool as? IfStatementUsageInspection

        // If the If Statement Inspection instance exists.
        if (ifStatementInspection != null) {

            // Store the settings in the panel to the inspection profile.
            ifStatementInspection.specs = ifStatementUsageTextField.text ?: ""
            inspectionProfile.setToolEnabled(ifStatementInspectionTool.shortName, newControlStructureState)
        }

        // -------------- Switch Statement Inspection -------------

        // Get the Switch Statement Inspection wrapper from the inspection profile.
        val switchStatementInspectionTool =
            inspectionProfile.getInspectionTool("SwitchStatementUsageInspection", project)

        // Get the Switch Statement Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val switchStatementInspection = switchStatementInspectionTool?.tool as? SwitchStatementUsageInspection

        // If the Switch Statement Inspection instance exists.
        if (switchStatementInspection != null) {

            // Store the settings in the panel to the inspection profile.
            switchStatementInspection.specs = switchStatementUsageTextField.text ?: ""
            inspectionProfile.setToolEnabled(switchStatementInspectionTool.shortName, newControlStructureState)
        }

        // -------------- While Loop Inspection -------------

        // Get the While Loop Inspection wrapper from the inspection profile.
        val whileLoopInspectionTool = inspectionProfile.getInspectionTool("WhileLoopUsageInspection", project)

        // Get the While Loop Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val whileLoopInspection = whileLoopInspectionTool?.tool as? WhileLoopUsageInspection

        // If the While Loop Inspection instance exists.
        if (whileLoopInspection != null) {

            // Store the settings in the panel to the inspection profile.
            whileLoopInspection.specs = whileLoopUsageTextField.text ?: ""
            inspectionProfile.setToolEnabled(whileLoopInspectionTool.shortName, newControlStructureState)
        }

        // -------------- For Loop Inspection -------------

        // Get the For Loop Inspection wrapper from the inspection profile.
        val forLoopInspectionTool = inspectionProfile.getInspectionTool("ForLoopUsageInspection", project)

        // Get the For Loop Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val forLoopInspection = forLoopInspectionTool?.tool as? ForLoopUsageInspection

        // If the For Loop Inspection instance exists.
        if (forLoopInspection != null) {

            // Store the settings in the panel to the inspection profile.
            forLoopInspection.specs = forLoopUsageTextField.text ?: ""
            inspectionProfile.setToolEnabled(forLoopInspectionTool.shortName, newControlStructureState)
        }

        // -------------- Enhanced For Loop Inspection -------------

        // Get the Foreach Loop Inspection wrapper from the inspection profile.
        val foreachLoopInspectionTool = inspectionProfile.getInspectionTool("ForeachLoopUsageInspection", project)

        // Get the Foreach Loop Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val foreachLoopInspection = foreachLoopInspectionTool?.tool as? ForeachLoopUsageInspection

        // If the Foreach Loop Inspection instance exists.
        if (foreachLoopInspection != null) {

            // Store the settings in the panel to the inspection profile.
            foreachLoopInspection.specs = foreachLoopUsageTextField.text ?: ""
            inspectionProfile.setToolEnabled(foreachLoopInspectionTool.shortName, newControlStructureState)
        }

        // --------------------------- Declaration Issue Inspection ---------------------------

        // Get the Unused Inspection wrapper from the inspection profile.
        val unusedInspectionTool = inspectionProfile.getInspectionTool("unused", project)

        // If the Unused Inspection instance exists.
        if (unusedInspectionTool != null) {

            // Store the settings in the panel to the inspection profile.
            val newMethodState = enableUnusedInspectionCheckBox.isSelected  // Read the new state.
            inspectionProfile.setToolEnabled(unusedInspectionTool.shortName, newMethodState)
        }

        // Get the Can Be Final Inspection wrapper from the inspection profile.
        val canBeFinalInspectionTool = inspectionProfile.getInspectionTool("CanBeFinal", project)

        // If the Can Be Final Inspection instance exists.
        if (canBeFinalInspectionTool != null) {

            // Store the settings in the panel to the inspection profile.
            val newMethodState = enableCanBeFinalInspectionCheckBox.isSelected  // Read the new state.
            inspectionProfile.setToolEnabled(canBeFinalInspectionTool.shortName, newMethodState)
        }

        // --------------------------- Java Language Level Issue Inspection ---------------------------

        // Get the Sequenced Collection Inspection wrapper from the inspection profile.
        val sequencedCollectionInspectionTool =
            inspectionProfile.getInspectionTool("SequencedCollectionMethodCanBeUsed", project)

        // If the Sequenced Collection Inspection instance exists.
        if (sequencedCollectionInspectionTool != null) {

            // Store the settings in the panel to the inspection profile.
            val newMethodState =
                enableSequencedCollectionMethodCanBeUsedInspectionCheckBox.isSelected  // Read the new state.
            inspectionProfile.setToolEnabled(sequencedCollectionInspectionTool.shortName, newMethodState)
        }

        // Get the For Can Be Foreach Inspection wrapper from the inspection profile.
        val forCanBeForeachInspectionTool = inspectionProfile.getInspectionTool("ForCanBeForeach", project)

        // If the For Can Be Foreach Inspection instance exists.
        if (forCanBeForeachInspectionTool != null) {

            // Store the settings in the panel to the inspection profile.
            val newMethodState = enableForCanBeForeachInspectionCheckBox.isSelected  // Read the new state.
            inspectionProfile.setToolEnabled(forCanBeForeachInspectionTool.shortName, newMethodState)
        }

        // Get the Convert 2Diamond Inspection wrapper from the inspection profile.
        val convert2DiamondInspectionTool = inspectionProfile.getInspectionTool("Convert2Diamond", project)

        // If the Convert 2Diamond Inspection instance exists.
        if (convert2DiamondInspectionTool != null) {

            // Store the settings in the panel to the inspection profile.
            val newMethodState = enableConvert2DiamondInspectionCheckBox.isSelected  // Read the new state.
            inspectionProfile.setToolEnabled(convert2DiamondInspectionTool.shortName, newMethodState)
        }

        // Get the Manual Array Copy Inspection wrapper from the inspection profile.
        val manualArrayCopyInspectionTool = inspectionProfile.getInspectionTool("ManualArrayCopy", project)

        // If the Manual Array Copy Inspection instance exists.
        if (manualArrayCopyInspectionTool != null) {

            // Store the settings in the panel to the inspection profile.
            val newMethodState = enableManualArrayCopyInspectionCheckBox.isSelected  // Read the new state.
            inspectionProfile.setToolEnabled(manualArrayCopyInspectionTool.shortName, newMethodState)
        }

        // --------------------------- Final Code ---------------------------

        // Commit the changes to the inspection profile.
        inspectionProfile.modifyProfile {
            it.commit()
        }
    }

    override fun reset() {
        // Retrieves the existing inspection profile for the current project.
        val inspectionProfile = InspectionProjectProfileManager.getInstance(project).currentProfile

        // --------------------------- Custom Method Length Inspection ---------------------------

        // Get the Method Length Inspection wrapper from the inspection profile.
        val methodLengthInspectionTool = inspectionProfile.getInspectionTool("MethodLengthInspection", project)

        // Get the Method Length Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val methodLengthInspection = methodLengthInspectionTool?.tool as? MethodLengthInspection

        // Set the value of the UI components to the stored states in the inspection profile.
        enableMethodLengthInspectionCheckBox.isSelected =
            methodLengthInspectionTool != null && inspectionProfile.isToolEnabled(methodLengthInspectionTool.displayKey)
        maxLengthTextField.text = methodLengthInspection?.maxLength?.toString() ?: "10"

        // --------------------------- Custom control structure Inspections ---------------------------

        // -------------- If Statement Inspection -------------

        // Get the If Statement Inspection wrapper from the inspection profile.
        val ifStatementInspectionTool = inspectionProfile.getInspectionTool("IfStatementUsageInspection", project)

        // Get the If Statement Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val ifStatementInspection = ifStatementInspectionTool?.tool as? IfStatementUsageInspection

        // Set the value of the UI components to the stored states in the inspection profile.
        val ifStatementState =
            ifStatementInspectionTool != null && inspectionProfile.isToolEnabled(ifStatementInspectionTool.displayKey)
        ifStatementUsageTextField.text = ifStatementInspection?.specs ?: ""

        // -------------- Switch Statement Inspection -------------

        // Get the Switch Statement Inspection wrapper from the inspection profile.
        val switchStatementInspectionTool =
            inspectionProfile.getInspectionTool("SwitchStatementUsageInspection", project)

        // Get the Switch Statement Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val switchStatementInspection = switchStatementInspectionTool?.tool as? SwitchStatementUsageInspection

        // Set the value of the UI components to the stored states in the inspection profile.
        val switchStatementState =
            switchStatementInspectionTool != null && inspectionProfile.isToolEnabled(switchStatementInspectionTool.displayKey)
        switchStatementUsageTextField.text = switchStatementInspection?.specs ?: ""

        // -------------- While Loop Inspection -------------

        // Get the While Loop Inspection wrapper from the inspection profile.
        val whileLoopInspectionTool = inspectionProfile.getInspectionTool("WhileLoopUsageInspection", project)

        // Get the While Loop Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val whileLoopInspection = whileLoopInspectionTool?.tool as? WhileLoopUsageInspection

        // Set the value of the UI components to the stored states in the inspection profile.
        val whileLoopState =
            whileLoopInspectionTool != null && inspectionProfile.isToolEnabled(whileLoopInspectionTool.displayKey)
        whileLoopUsageTextField.text = whileLoopInspection?.specs ?: ""

        // -------------- For Loop Inspection -------------

        // Get the For Loop Inspection wrapper from the inspection profile.
        val forLoopInspectionTool = inspectionProfile.getInspectionTool("ForLoopUsageInspection", project)

        // Get the For Loop Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val forLoopInspection = forLoopInspectionTool?.tool as? ForLoopUsageInspection

        // Set the value of the UI components to the stored states in the inspection profile.
        val forLoopState =
            forLoopInspectionTool != null && inspectionProfile.isToolEnabled(forLoopInspectionTool.displayKey)
        forLoopUsageTextField.text = forLoopInspection?.specs ?: ""

        // -------------- Enhanced For Loop Inspection -------------

        // Get the Enhanced For Loop Inspection wrapper from the inspection profile.
        val foreachLoopInspectionTool = inspectionProfile.getInspectionTool("ForeachLoopUsageInspection", project)

        // Get the Enhanced For Loop Inspection instance from the wrapper.
        // Null if the wrapper does not exist or the type of the inspection instance is not correct.
        val foreachLoopInspection = foreachLoopInspectionTool?.tool as? ForeachLoopUsageInspection

        // Set the value of the UI components to the stored states in the inspection profile.
        val foreachLoopState =
            foreachLoopInspectionTool != null && inspectionProfile.isToolEnabled(foreachLoopInspectionTool.displayKey)
        foreachLoopUsageTextField.text = foreachLoopInspection?.specs ?: ""

        // -------------- Final Code for Control Structures  -------------

        // Check all the Control Structure Inspections have the same state.
        if (ifStatementState == switchStatementState == whileLoopState == forLoopState == foreachLoopState) {
            enableControlStructInspectionsCheckBox.isSelected = ifStatementState
        } else {
            // Set all the Control Structure Inspections to false if the status are not consistent.
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

            // Commit the changes to the inspection profile.
            inspectionProfile.modifyProfile {
                it.commit()
            }
        }

        // --------------------------- Declaration Issue Inspection ---------------------------

        // Get the Unused Inspection wrapper from the inspection profile.
        val unusedInspectionTool = inspectionProfile.getInspectionTool("unused", project)

        // Set the value of the UI components to the stored states in the inspection profile.
        enableUnusedInspectionCheckBox.isSelected =
            unusedInspectionTool != null && inspectionProfile.isToolEnabled(unusedInspectionTool.displayKey)

        // Get the Can Be Final Inspection wrapper from the inspection profile.
        val canBeFinalInspectionTool = inspectionProfile.getInspectionTool("CanBeFinal", project)

        // Set the value of the UI components to the stored states in the inspection profile.
        enableCanBeFinalInspectionCheckBox.isSelected =
            canBeFinalInspectionTool != null && inspectionProfile.isToolEnabled(canBeFinalInspectionTool.displayKey)

        // --------------------------- Java Language Level Issue Inspection ---------------------------

        // Get the sequenced Collection Inspection wrapper from the inspection profile.
        val sequencedCollectionInspectionTool =
            inspectionProfile.getInspectionTool("SequencedCollectionMethodCanBeUsed", project)

        // Set the value of the UI components to the stored states in the inspection profile.
        enableSequencedCollectionMethodCanBeUsedInspectionCheckBox.isSelected =
            sequencedCollectionInspectionTool != null && inspectionProfile.isToolEnabled(
                sequencedCollectionInspectionTool.displayKey
            )

        // Get the For Can Be Foreach Inspection wrapper from the inspection profile.
        val forCanBeForeachInspectionTool = inspectionProfile.getInspectionTool("ForCanBeForeach", project)

        // Set the value of the UI components to the stored states in the inspection profile.
        enableForCanBeForeachInspectionCheckBox.isSelected =
            forCanBeForeachInspectionTool != null && inspectionProfile.isToolEnabled(forCanBeForeachInspectionTool.displayKey)

        // Get the Convert 2Diamond Inspection wrapper from the inspection profile.
        val convert2DiamondInspectionTool = inspectionProfile.getInspectionTool("Convert2Diamond", project)

        // Set the value of the UI components to the stored states in the inspection profile.
        enableConvert2DiamondInspectionCheckBox.isSelected =
            convert2DiamondInspectionTool != null && inspectionProfile.isToolEnabled(convert2DiamondInspectionTool.displayKey)

        // Get the Manual Array Copy Inspection wrapper from the inspection profile.
        val manualArrayCopyInspectionTool = inspectionProfile.getInspectionTool("ManualArrayCopy", project)

        // Set the value of the UI components to the stored states in the inspection profile.
        enableManualArrayCopyInspectionCheckBox.isSelected =
            manualArrayCopyInspectionTool != null && inspectionProfile.isToolEnabled(manualArrayCopyInspectionTool.displayKey)
    }

    override fun getDisplayName(): String {
        return "EasyCode Linter Panel"  // Name of the control panel.
    }

    // One helper function to handle the importation of the input settings file.
    private fun importInputSettingsFile(file: File) {

        // Declare a variable to store the inspection name
        var inspectionName = ""

        try {
            // Create a FileReader to read from the input file
            FileReader(file).use { reader ->

                // Parse the file into a JsonObject using Google GSON
                val json = Gson().fromJson(reader, JsonObject::class.java)

                // Parsing "MethodLengthInspection" settings
                inspectionName = "MethodLengthInspection"
                json.getAsJsonObject("MethodLengthInspection")?.let { methodLengthJson ->
                    enableMethodLengthInspectionCheckBox.isSelected = methodLengthJson.get("status").asBoolean
                    maxLengthTextField.text = methodLengthJson.get("maxLength").asString
                }

                // Parse "ControlStructuresInspection" settings
                inspectionName = "ControlStructuresInspection"
                json.getAsJsonObject("ControlStructuresInspection")?.let { controlStructuresJson ->
                    enableControlStructInspectionsCheckBox.isSelected = controlStructuresJson.get("status").asBoolean
                    ifStatementUsageTextField.text = controlStructuresJson.get("IfStatementSpecs").asString
                    switchStatementUsageTextField.text = controlStructuresJson.get("SwitchStatementSpecs").asString
                    whileLoopUsageTextField.text = controlStructuresJson.get("WhileLoopSpecs").asString
                    forLoopUsageTextField.text = controlStructuresJson.get("ForLoopSpecs").asString
                    foreachLoopUsageTextField.text = controlStructuresJson.get("EnhancedForLoopSpecs").asString
                }

                // Parse "CanBeFinalInspection" settings
                inspectionName = "CanBeFinalInspection"
                json.getAsJsonObject("CanBeFinalInspection")?.let { canBeFinalJson ->
                    enableCanBeFinalInspectionCheckBox.isSelected = canBeFinalJson.get("status").asBoolean
                }

                // Parse "UnusedInspection" settings
                inspectionName = "UnusedInspection"
                json.getAsJsonObject("UnusedInspection")?.let { unusedJson ->
                    enableUnusedInspectionCheckBox.isSelected = unusedJson.get("status").asBoolean
                }

                // Parse "SequencedCollectionMethodCanBeUsedInspection" settings
                inspectionName = "SequencedCollectionMethodCanBeUsedInspection"
                json.getAsJsonObject("SequencedCollectionMethodCanBeUsedInspection")?.let { sequencedCollectionJson ->
                    enableSequencedCollectionMethodCanBeUsedInspectionCheckBox.isSelected =
                        sequencedCollectionJson.get("status").asBoolean
                }

                // Parse "ForCanBeForeachInspection" settings
                inspectionName = "ForCanBeForeachInspection"
                json.getAsJsonObject("ForCanBeForeachInspection")?.let { forCanBeForeachJson ->
                    enableForCanBeForeachInspectionCheckBox.isSelected = forCanBeForeachJson.get("status").asBoolean
                }

                // Parse "Convert2DiamondInspection" settings
                inspectionName = "Convert2DiamondInspection"
                json.getAsJsonObject("Convert2DiamondInspection")?.let { convert2DiamondJson ->
                    enableConvert2DiamondInspectionCheckBox.isSelected = convert2DiamondJson.get("status").asBoolean
                }

                // Parse "ManualArrayCopyInspection" settings
                inspectionName = "ManualArrayCopyInspection"
                json.getAsJsonObject("ManualArrayCopyInspection")?.let { manualArrayCopyJson ->
                    enableManualArrayCopyInspectionCheckBox.isSelected = manualArrayCopyJson.get("status").asBoolean
                }
            }
        } catch (e: Exception) {
            // Show an error message when the format of the input settings file is not correct.
            JOptionPane.showMessageDialog(
                panel,
                "Error reading in ${inspectionName} object!",
                "Import Error",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

}