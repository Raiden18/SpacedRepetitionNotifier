package config

import com.google.gson.Gson
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.config.data.ConfigData
import org.danceofvalkyries.config.data.NotionData
import org.danceofvalkyries.config.data.TelegramData
import org.danceofvalkyries.utils.rest.`object`

class ConfigKtTest : FunSpec() {

    init {
        test("Should parse config from json") {
            val jsonObject = `object` {
                "notion" to `object` {
                    "api_key" to "1"
                    "observed_databases" to arrayOf(
                        "2",
                        "3",
                        "4",
                        "5",
                    )
                    "delay_between_requests" to 500
                }
                "telegram" to `object` {
                    "api_key" to "6"
                    "chat_id" to "7"
                }
                "flash_cards_threshold" to 10
            }

            val jsonString = Gson().toJson(jsonObject)

            ConfigData(
                Gson(),
                jsonString,
            ) shouldBe ConfigData(
                notion = NotionData(
                    apiKey = "1",
                    observedDatabases = listOf("2", "3", "4", "5"),
                    delayBetweenRequests = 500
                ),
                telegram = TelegramData(
                    apiKey = "6",
                    chatId = "7"
                ),
                flashCardsThreshold = 10
            )
        }
    }
}