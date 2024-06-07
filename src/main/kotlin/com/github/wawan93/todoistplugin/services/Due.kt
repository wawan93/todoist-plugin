package com.github.wawan93.todoistplugin.services

import com.google.gson.annotations.SerializedName


data class Due (

  @SerializedName("date"         ) var date        : String?  = null,
  @SerializedName("is_recurring" ) var isRecurring : Boolean? = null,
  @SerializedName("datetime"     ) var datetime    : String?  = null,
  @SerializedName("string"       ) var string      : String?  = null,
  @SerializedName("timezone"     ) var timezone    : String?  = null

)