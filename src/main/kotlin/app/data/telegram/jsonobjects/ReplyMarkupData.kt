package org.danceofvalkyries.app.data.telegram.jsons

import com.google.gson.annotations.SerializedName

data class ReplyMarkupData(
    @SerializedName("inline_keyboard")
    val inlineKeyboards: List<List<ButtonData>>
)