package org.danceofvalkyries.telegram.impl.models

import com.google.gson.annotations.SerializedName

data class UpdateResponseData(
    @SerializedName("update_id")
    val id: Long,
    @SerializedName("callback_query")
    val callbackQueryData: CallbackQueryData
)