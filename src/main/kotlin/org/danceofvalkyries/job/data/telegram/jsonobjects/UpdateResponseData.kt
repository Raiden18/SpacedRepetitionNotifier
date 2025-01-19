package org.danceofvalkyries.job.data.telegram.jsons

import com.google.gson.annotations.SerializedName
import org.danceofvalkyries.job.data.telegram.jsonobjects.CallbackQueryData
import org.danceofvalkyries.job.data.telegram.jsonobjects.MessageData

data class UpdateResponseData(
    @SerializedName("update_id")
    val id: Long,
    @SerializedName("callback_query")
    val callbackQueryData: CallbackQueryData?,
    @SerializedName("message")
    val messageData: MessageData?
)