package org.danceofvalkyries.job.data.telegram.jsonobjects

import com.google.gson.annotations.SerializedName
import org.danceofvalkyries.job.data.telegram.jsons.UpdateResponseData

data class CallbackQueryResult(
    @SerializedName("result")
    val updateResponseData: List<UpdateResponseData>
)