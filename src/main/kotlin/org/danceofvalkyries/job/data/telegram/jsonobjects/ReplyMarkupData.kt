package org.danceofvalkyries.job.data.telegram.jsons

import com.google.gson.annotations.SerializedName
import org.danceofvalkyries.job.data.telegram.jsonobjects.ButtonData

data class ReplyMarkupData(
    @SerializedName("inline_keyboard")
    val inlineKeyboards: List<List<ButtonData>>
)