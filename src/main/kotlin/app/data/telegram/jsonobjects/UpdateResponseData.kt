package org.danceofvalkyries.app.data.telegram.jsons

import com.google.gson.annotations.SerializedName

data class UpdateResponseData(
    @SerializedName("update_id")
    val id: Long,
    @SerializedName("callback_query")
    val callbackQueryData: CallbackQueryData?,
    @SerializedName("message")
    val messageData: MessageData?
)