package org.danceofvalkyries.app.data.telegram.jsons

import app.data.telegram.jsonobjects.MessageData
import com.google.gson.annotations.SerializedName

data class UpdateResponseData(
    @SerializedName("update_id")
    val id: Long,
    @SerializedName("callback_query")
    val callbackQueryData: CallbackQueryData?,
    @SerializedName("message")
    val messageData: MessageData?
)