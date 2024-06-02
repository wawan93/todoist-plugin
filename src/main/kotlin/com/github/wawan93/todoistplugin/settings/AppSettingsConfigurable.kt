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
                    .getTodoistProjects(appSettingsComponent!!.getTodoistAPIKey(),
                        object : ApiCallback<Array<TodoistProject>> {
                            override fun onSuccess(result: Array<TodoistProject>) {
                                appSettingsComponent!!.projectsDropdown.setListData(result.map {
                                    KeyValueItem(
                                        it.id!!, it.name!!
                                    )
                                }.toTypedArray())
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
        return todoistTokenModified(settings) || todoistProjectModified(settings)
    }

    override fun apply() {
        val settings = project.getService(AppSettingsState::class.java)

        settings.todoistToken = appSettingsComponent?.getTodoistAPIKey() ?: ""

        val map = mutableMapOf<String, String>()
        if (appSettingsComponent?.projectsDropdown?.model != null && appSettingsComponent?.projectsDropdown?.model?.size != 0) {
            for (i in 0 until appSettingsComponent?.projectsDropdown?.model?.size!!) {
                val item = appSettingsComponent?.projectsDropdown?.model?.getElementAt(i)
                if (item != null) {
                    map[item.key] = item.value
                }
            }
        }

        settings.projects = map

        settings.selectedProjectId = appSettingsComponent?.getSelectedProject() ?: ""
    }

    override fun reset() {
        val settings = project.getService(AppSettingsState::class.java)

        appSettingsComponent?.setTodoistAPIKey(settings.todoistToken)

        val projects = settings.projects.map { KeyValueItem(it.key, it.value) }.toTypedArray()
        appSettingsComponent?.setProjects(projects)

        appSettingsComponent?.projectsDropdown?.setSelectedValue(
            KeyValueItem(settings.selectedProjectId, settings.projects[settings.selectedProjectId] ?: ""),
            true
        )
    }

    override fun getDisplayName(): String {
        return "Todoist Plugin"
    }

    override fun disposeUIResources() {
        appSettingsComponent = null
    }

    private fun todoistTokenModified(settings: AppSettingsState): Boolean {
        return settings.todoistToken != appSettingsComponent?.getTodoistAPIKey()
    }

    private fun todoistProjectModified(settings: AppSettingsState): Boolean {
        return settings.selectedProjectId != appSettingsComponent?.getSelectedProject()
    }
}