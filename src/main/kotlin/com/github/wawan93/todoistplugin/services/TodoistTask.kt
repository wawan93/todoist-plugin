package com.github.wawan93.todoistplugin.services

import com.google.gson.annotations.SerializedName


data class TodoistTask (

  @SerializedName("creator_id"    ) var creatorId    : String?           = null,
  @SerializedName("created_at"    ) var createdAt    : String?           = null,
  @SerializedName("assignee_id"   ) var assigneeId   : String?           = null,
  @SerializedName("assigner_id"   ) var assignerId   : String?           = null,
  @SerializedName("comment_count" ) var commentCount : Int?              = null,
  @SerializedName("is_completed"  ) var isCompleted  : Boolean?          = null,
  @SerializedName("content"       ) var content      : String?           = null,
  @SerializedName("description"   ) var description  : String?           = null,
  @SerializedName("due"           ) var due          : Due?              = Due(),
  @SerializedName("duration"      ) var duration     : String?           = null,
  @SerializedName("id"            ) var id           : String?           = null,
  @SerializedName("labels"        ) var labels       : ArrayList<String> = arrayListOf(),
  @SerializedName("order"         ) var order        : Int?              = null,
  @SerializedName("priority"      ) var priority     : Int?              = null,
  @SerializedName("project_id"    ) var projectId    : String?           = null,
  @SerializedName("section_id"    ) var sectionId    : String?           = null,
  @SerializedName("parent_id"     ) var parentId     : String?           = null,
  @SerializedName("url"           ) var url          : String?           = null

)