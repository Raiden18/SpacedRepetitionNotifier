package org.danceofvalkyries.telegram.impl.models

import com.google.gson.annotations.SerializedName

data class CallbackQueryResult(
    @SerializedName("result")
    val updateResponseData: List<UpdateResponseData>
)