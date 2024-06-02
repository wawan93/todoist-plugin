package com.github.wawan93.todoistplugin.services

import java.io.IOException

interface ApiCallback<T> {
    fun onSuccess(result: T)
    fun onFailure(error: IOException)
}