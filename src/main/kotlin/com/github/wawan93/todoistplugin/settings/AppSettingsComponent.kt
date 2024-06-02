package com.github.wawan93.todoistplugin.settings

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import org.jetbrains.annotations.NotNull
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class AppSettingsComponent {
    private var panel: JPanel? = null

    private var todoistAPIKey: JBTextField = JBTextField()
    var loadButton: JButton = JButton("Load Projects")
    var projectsDropdown: JBList<KeyValueItem> = JBList<KeyValueItem>()

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Todoist API Key: "), todoistAPIKey, 1, false)
            .addComponent(loadButton)
            .addLabeledComponent(JBLabel("Projects: "), projectsDropdown, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getPreferredFocusedComponent(): JComponent {
        return todoistAPIKey
    }

    fun getPanel(): JComponent? {
        return panel
    }

    fun getTodoistAPIKey(): String {
        return todoistAPIKey.text
    }

    fun setTodoistAPIKey(@NotNull value: String) {
        todoistAPIKey.text = value
    }

    fun setProjects(projects: Array<KeyValueItem>) {
        projectsDropdown.setListData(projects)
    }

    fun getSelectedProject(): String {
        if (projectsDropdown.selectedValue == null) {
            return ""
        }
        return projectsDropdown.selectedValue.key
    }
}