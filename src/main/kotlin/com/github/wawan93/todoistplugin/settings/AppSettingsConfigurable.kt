package com.github.wawan93.todoistplugin.settings

import com.github.wawan93.todoistplugin.services.ApiCallback
import com.github.wawan93.todoistplugin.services.MyProjectService
import com.github.wawan93.todoistplugin.services.TodoistProject
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import java.io.IOException
import javax.swing.JComponent

class AppSettingsConfigurable(private val project: Project) : Configurable {
    private var appSettingsComponent: AppSettingsComponent? = null

    override fun getPreferredFocusedComponent(): JComponent? {
        return appSettingsComponent?.getPreferredFocusedComponent()
    }

    override fun createComponent(): JComponent? {
        appSettingsComponent = AppSettingsComponent()
        appSettingsComponent!!.loadButton.addActionListener {
            try {
                project.getService(MyProjectService::class.java)
                    .getTodoistProjects(appSettingsComponent!!.getTodoistAPIKey(), object:
                        ApiCallback<Array<TodoistProject>> {
                        override fun onSuccess(result: Array<TodoistProject>) {
                            appSettingsComponent!!.projectsDropdown.setListData(result.map { it.name }.toTypedArray())
                        }

                        override fun onFailure(error: IOException) {
                            thisLogger().error("Can't get Todoist projects", "Error: ${error.message}")
                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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