package org.danceofvalkyries.config.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.config.domain.NotionConfig
import org.danceofvalkyries.config.domain.TelegramConfig

fun ConfigData(
    gson: Gson,
    json: String
): Config {
    return gson.fromJson(json, ConfigData::class.java)
}

data class ConfigData(
    @SerializedName("notion")
    override val notion: NotionData,
    @SerializedName("telegram")
    override val telegram: TelegramData,
    @SerializedName("flash_cards_threshold")
    override val flashCardsThreshold: Int,
) : Config

data class NotionData(
    @SerializedName("api_key")
    override val apiKey: String,
    @SerializedName("observed_databases")
    override val observedDatabases: List<String>,
    @SerializedName("delay_between_requests")
    override val delayBetweenRequests: Int,
) : NotionConfig

data class TelegramData(
    @SerializedName("api_key")
    override val apiKey: String,
    @SerializedName("chat_id")
    override val chatId: String,
) : TelegramConfig