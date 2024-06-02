package com.github.wawan93.todoistplugin.services

import com.github.wawan93.todoistplugin.MyBundle
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

data class TodoistProject(
    @SerializedName("id"               ) var id             : String?  = null,
    @SerializedName("name"             ) var name           : String?  = null,
    @SerializedName("comment_count"    ) var commentCount   : Int?     = null,
    @SerializedName("order"            ) var order          : Int?     = null,
    @SerializedName("color"            ) var color          : String?  = null,
    @SerializedName("is_shared"        ) var isShared       : Boolean? = null,
    @SerializedName("is_favorite"      ) var isFavorite     : Boolean? = null,
    @SerializedName("parent_id"        ) var parentId       : String?  = null,
    @SerializedName("is_inbox_project" ) var isInboxProject : Boolean? = null,
    @SerializedName("is_team_inbox"    ) var isTeamInbox    : Boolean? = null,
    @SerializedName("view_style"       ) var viewStyle      : String?  = null,
    @SerializedName("url"              ) var url            : String?  = null
)

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
}
