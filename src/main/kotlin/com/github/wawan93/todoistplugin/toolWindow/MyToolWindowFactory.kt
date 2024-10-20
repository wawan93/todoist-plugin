package com.github.wawan93.todoistplugin.toolWindow

import com.github.wawan93.todoistplugin.services.ApiCallback
import com.github.wawan93.todoistplugin.services.MyProjectService
import com.github.wawan93.todoistplugin.services.TodoistTask
import com.github.wawan93.todoistplugin.settings.AppSettingsState
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import java.awt.event.ActionEvent
import java.io.IOException
import javax.swing.BoxLayout
import javax.swing.JButton

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
            val syncButton = createSyncButton()
            add(syncButton)
            triggerSyncButtonAction(syncButton)
        }

        private fun JBPanel<JBPanel<*>>.createSyncButton(): JButton {
            val syncButton = JButton("Sync")
            val apiCallback = createApiCallback(syncButton)

            syncButton.addActionListener {
                service.getTasks(
                    appSettingsState.todoistToken,
                    appSettingsState.selectedProjectId,
                    apiCallback
                )
            }
            return syncButton
        }

        private fun JBPanel<JBPanel<*>>.createApiCallback(syncButton: JButton) =
            object : ApiCallback<Array<TodoistTask>> {
                override fun onSuccess(result: Array<TodoistTask>) {
                    removeAll()
                    add(syncButton)
                    renderTasks(result)
                    revalidate()
                    repaint()
                }

                override fun onFailure(error: IOException) {
                    add(JBLabel("Error: ${error.message}"))
                }
            }

        private fun triggerSyncButtonAction(syncButton: JButton) {
            syncButton.actionListeners.first().actionPerformed(
                ActionEvent(syncButton, ActionEvent.ACTION_PERFORMED, "buttonClicked")
            )
        }

        fun JBPanel<JBPanel<*>>.renderTasks(result: Array<TodoistTask>) {
            // Convert list to a map for easier access by id
            val taskMap: MutableMap<String?, TodoistTask> = result.associateBy { it.id }.toMutableMap()

            // Function to recursively set the level
            fun setLevel(task: TodoistTask, level: Int) {
                task.level = level
                // Find children of this task and set their levels
                taskMap.values.filter { it.parentId == task.id }.forEach {
                    setLevel(it, level + 1)
                }
            }

            // Set the level for root tasks
            taskMap.values.filter { it.parentId == null }.forEach { setLevel(it, 0) }

            taskMap.forEach { task ->
                add(createTaskCheckbox(task.value))
            }
        }

        private fun JBPanel<JBPanel<*>>.createTaskCheckbox(
            task: TodoistTask,
        ): JBCheckBox {
            val checkBox = JBCheckBox(task.content)
            if (task.isCompleted!!) {
                checkBox.isSelected = true
            }

            if (!task.parentId.isNullOrEmpty()) {
                checkBox.border = JBUI.Borders.emptyLeft(20 * task.level) // Add left padding
            }

            thisLogger().info(checkBox.toString())

            checkBox.addActionListener {
                val callback = object : ApiCallback<String> {
                    override fun onSuccess(result: String) {
                        triggerSyncButtonAction(components.get(0) as JButton)
                    }

                    override fun onFailure(error: IOException) {
                        thisLogger().error("Can't close task", error)
                    }
                }

                if (checkBox.isSelected) {
                    service.closeTask(appSettingsState.todoistToken, task.id!!, callback)
                }
            }

            return checkBox
        }
    }
}
