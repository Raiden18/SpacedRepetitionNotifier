package org.danceofvalkyries.telegram.chat.restful.json.objects

import com.google.gson.annotations.SerializedName

data class TelegramErrorJsonObject(
    @SerializedName("ok")
    val ok: Boolean?,
    @SerializedName("error_code")
    val errorCode: Int?,
    @SerializedName("description")
    val description: String?,
)