package com.github.wawan93.todoistplugin.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.github.wawan93.todoistplugin.MyBundle
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

data class TodoistProject(
    @SerializedName("can_assign_tasks" ) var canAssignTasks : Boolean? = null,
    @SerializedName("child_order"      ) var childOrder     : Int?     = null,
    @SerializedName("collapsed"        ) var collapsed      : Boolean? = null,
    @SerializedName("color"            ) var color          : String?  = null,
    @SerializedName("created_at"       ) var createdAt      : String?  = null,
    @SerializedName("id"               ) var id             : String?  = null,
    @SerializedName("is_archived"      ) var isArchived     : Boolean? = null,
    @SerializedName("is_deleted"       ) var isDeleted      : Boolean? = null,
    @SerializedName("is_favorite"      ) var isFavorite     : Boolean? = null,
    @SerializedName("name"             ) var name           : String?  = null,
    @SerializedName("parent_id"        ) var parentId       : String?  = null,
    @SerializedName("shared"           ) var shared         : Boolean? = null,
    @SerializedName("sync_id"          ) var syncId         : String?  = null,
    @SerializedName("updated_at"       ) var updatedAt      : String?  = null,
    @SerializedName("v2_id"            ) var v2Id           : String?  = null,
    @SerializedName("v2_parent_id"     ) var v2ParentId     : String?  = null,
    @SerializedName("view_style"       ) var viewStyle      : String?  = null
)

data class TodoistSyncResponse(
    @SerializedName("full_sync"       ) var fullSync      : Boolean?            = null,
    @SerializedName("projects"        ) var projects      : ArrayList<TodoistProject> = arrayListOf(),
    @SerializedName("sync_token"      ) var syncToken     : String?             = null,
)

@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {

    private val httpClient = OkHttpClient()

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    fun getRandomNumber() = (1..100).random()

    fun getTodoistProjects(token: String, callback: ApiCallback<List<TodoistProject>>) {
        thisLogger().info("Getting Todoist projects")
        if (token == "") {
            throw RuntimeException("Token is empty")
        }

        val req = Request.Builder()
            .url("https://api.todoist.com/sync/v9/sync")
            .header("Authorization", "Bearer $token")
            .post("{\"resource_types\": [\"projects\"]}".toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        httpClient.newCall(req).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback.onFailure(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.let { responseBody ->
                    val responseData = responseBody.string()
                    val gson = Gson()
                    val resp = gson.fromJson(responseData, TodoistSyncResponse::class.java)
                    callback.onSuccess(resp.projects)
                } ?: run {
                    callback.onFailure(IOException("Response body is null"))
                }
            }
        })
    }
}
