package com.github.wawan93.todoistplugin.services

import com.github.wawan93.todoistplugin.MyBundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {

    private val httpClient = OkHttpClient()

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    fun getRandomNumber() = (1..100).random()

    fun getTodoistProjects(token: String, callback: ApiCallback<Array<TodoistProject>>) {
        thisLogger().info("Getting Todoist projects")
        if (token == "") {
            throw RuntimeException("Token is empty")
        }

        val req = Request.Builder()
            .url("https://api.todoist.com/rest/v2/projects")
            .header("Authorization", "Bearer $token")
            .get()
            .build()

        httpClient.newCall(req).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback.onFailure(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.let { responseBody ->
                    val listType = object : TypeToken<Array<TodoistProject>>() {}.type
                    val responseData = responseBody.string()
                    val gson = Gson()
                    val resp: Array<TodoistProject> = gson.fromJson(responseData, listType)
                    callback.onSuccess(resp)
                } ?: run {
                    callback.onFailure(IOException("Response body is null"))
                }
            }
        })
    }

    fun getTasks(token: String, projectId: String, callback: ApiCallback<Array<TodoistTask>>) {
        thisLogger().info("Getting Todoist tasks")
        if (token == "") {
            throw RuntimeException("Token is empty")
        }

        if (projectId == "") {
            throw RuntimeException("Project ID is empty. Please select a project in Settings.")
        }

        val req = Request.Builder()
            .url("https://api.todoist.com/rest/v2/tasks?project_id=$projectId")
            .header("Authorization", "Bearer $token")
            .get()
            .build()

        httpClient.newCall(req).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback.onFailure(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.let { responseBody ->
                    val listType = object : TypeToken<Array<TodoistTask>>() {}.type
                    val responseData = responseBody.string()
                    val gson = Gson()
                    val resp: Array<TodoistTask> = gson.fromJson(responseData, listType)
                    callback.onSuccess(resp)
                } ?: run {
                    callback.onFailure(IOException("Response body is null"))
                }
            }
        })

    }

    fun closeTask(token: String, taskId: String, callback: ApiCallback<String>) {
        thisLogger().warn("Closing Todoist task")
        if (token == "") {
            throw RuntimeException("Token is empty")
        }

        if (taskId == "") {
            throw RuntimeException("Task ID is empty. Please select a task.")
        }

        val req = Request.Builder()
            .url("https://api.todoist.com/rest/v2/tasks/$taskId/close")
            .header("Authorization", "Bearer $token")
            .post("".toRequestBody(null))
            .build()

        httpClient.newCall(req).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback.onFailure(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.let { responseBody ->
                    val responseData = responseBody.string()
                    callback.onSuccess(responseData)
                } ?: run {
                    callback.onFailure(IOException("Response body is null"))
                }
            }
        })
    }
}
