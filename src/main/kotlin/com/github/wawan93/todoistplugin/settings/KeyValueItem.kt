package com.github.wawan93.todoistplugin.settings

import com.intellij.util.xmlb.annotations.Tag

data class KeyValueItem(
    @Tag var key: String,
    @Tag var value: String
) {
    override fun toString(): String {
        return value
    }
}