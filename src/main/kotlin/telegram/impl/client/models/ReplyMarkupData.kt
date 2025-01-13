package org.danceofvalkyries.telegram.impl.client.models

import com.google.gson.annotations.SerializedName

data class ReplyMarkupData(
    @SerializedName("inline_keyboard")
    val inlineKeyboards: List<List<ButtonData>>
)