package com.github.wawan93.todoistplugin.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.github.wawan93.todoistplugin.services.ApiCallback
import com.github.wawan93.todoistplugin.services.MyProjectService
import com.github.wawan93.todoistplugin.services.TodoistTask
import com.github.wawan93.todoistplugin.settings.AppSettingsState
import com.intellij.ui.components.JBCheckBox
import java.io.IOException
import javax.swing.BoxLayout


class MyToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<MyProjectService>()
        private val appSettingsState = toolWindow.project.getService(AppSettingsState::class.java)

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            service.getTasks(
                appSettingsState.todoistToken,
                appSettingsState.selectedProjectId,
                object : ApiCallback<Array<TodoistTask>> {
                    override fun onSuccess(result: Array<TodoistTask>) {
                        result.forEach { task ->
                            add(createTaskCheckbox(task))
                        }
                    }

                    override fun onFailure(error: IOException) {
                        add(JBLabel("Error: ${error.message}"))
                    }
                })
        }

        private fun createTaskCheckbox(task: TodoistTask): JBCheckBox {
            val checkBox = JBCheckBox(task.content)
            if (task.isCompleted!!) {
                checkBox.isSelected = true
            }
            checkBox.addActionListener {
                if (!checkBox.isSelected) {
                    return@addActionListener
                }

                service.closeTask(
                    appSettingsState.todoistToken,
                    task.id!!,
                    object : ApiCallback<String> {
                        override fun onSuccess(result: String) {
                            thisLogger().warn("Task closed")
                        }

                        override fun onFailure(error: IOException) {
                            thisLogger().error("Can't close task", error)
                        }
                    }
                )
            }
            return checkBox
        }
    }
}
