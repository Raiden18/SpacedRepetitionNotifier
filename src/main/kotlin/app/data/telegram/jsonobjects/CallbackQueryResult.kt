package org.danceofvalkyries.app.data.telegram.jsons

import com.google.gson.annotations.SerializedName

data class CallbackQueryResult(
    @SerializedName("result")
    val updateResponseData: List<UpdateResponseData>
)