package com.github.wawan93.todoistplugin.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.XCollection

@Service(Service.Level.PROJECT)
@State(
    name = "com.github.wawan93.todoistplugin.AppSettingsState",
)
class AppSettingsState : PersistentStateComponent<AppSettingsState> {
    var todoistToken: String = ""
    var selectedProjectId: String = ""

    @XCollection
    var projects: Map<String, String> = mapOf()

    override fun getState(): AppSettingsState {
        return this
    }

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}