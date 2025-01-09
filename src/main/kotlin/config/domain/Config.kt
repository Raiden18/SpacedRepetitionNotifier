package org.danceofvalkyries.config.domain

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

fun Config(
    gson: Gson,
    json: String
): Config {
    return gson.fromJson(json, Config::class.java)
}

data class Config(
    @SerializedName("notion")
    val notion: Notion,
    @SerializedName("telegram")
    val telegram: Telegram,
    @SerializedName("flash_cards_threshold")
    val flashCardsThreshold: Int,
)

data class Notion(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("observed_databases")
    val observedDatabases: List<String>,
    @SerializedName("delay_between_requests")
    val delayBetweenRequests: Int,
)

data class Telegram(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("chat_id")
    val chatId: String,
)