package org.danceofvalkyries.telegram.impl.client.request.bodies

import com.google.gson.annotations.SerializedName

data class ReplyMarkupResponse(
    @SerializedName("inline_keyboard")
    val inlineKeyboards: List<List<ButtonRequest>>
)