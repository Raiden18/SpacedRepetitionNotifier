package org.danceofvalkyries.telegram.impl.client.models

import com.google.gson.annotations.SerializedName

data class CallbackQueryResult(
    @SerializedName("result")
    val updateResponseData: List<UpdateResponseData>
)