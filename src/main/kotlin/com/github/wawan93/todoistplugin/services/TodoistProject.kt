package com.github.wawan93.todoistplugin.services

import com.google.gson.annotations.SerializedName

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
