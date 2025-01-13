package org.danceofvalkyries.telegram.impl.client.models

import com.google.gson.annotations.SerializedName

data class CallbackQueryData(
    @SerializedName("id")
    val id: String,
    @SerializedName("message")
    val message: MessageData,
    @SerializedName("data")
    val data: String
)
