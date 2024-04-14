package com.github.wawan93.todoistplugin.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

class AppSettingsConfigurable(private val project: Project) : Configurable {
    private var appSettingsComponent: AppSettingsComponent? = null

    override fun getPreferredFocusedComponent(): JComponent? {
        return appSettingsComponent?.getPreferredFocusedComponent()
    }

    override fun createComponent(): JComponent? {
        appSettingsComponent = AppSettingsComponent()
        return appSettingsComponent?.getPanel()
    }

    override fun isModified(): Boolean {
        val settings = project.getService(AppSettingsState::class.java)
        return settings.todoistToken !=
                appSettingsComponent?.getTodoistAPIKey()
    }

    override fun apply() {
        val settings = project.getService(AppSettingsState::class.java)
        settings.todoistToken = appSettingsComponent?.getTodoistAPIKey() ?: ""
    }

    override fun reset() {
        val settings = project.getService(AppSettingsState::class.java)
        appSettingsComponent?.setTodoistAPIKey(settings.todoistToken)
    }

    override fun getDisplayName(): String {
        return "Todoist Plugin"
    }

    override fun disposeUIResources() {
        appSettingsComponent = null
    }

}