package org.danceofvalkyries.job.data.telegram.jsonobjects

import com.google.gson.annotations.SerializedName
import org.danceofvalkyries.telegram.jsonobjects.MessageData

data class CallbackQueryData(
    @SerializedName("id")
    val id: String,
    @SerializedName("message")
    val message: MessageData? = null,
    @SerializedName("data")
    val data: String? = null,
)
