package org.danceofvalkyries.config.data

import com.google.gson.Gson
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.config.domain.ConfigRepository
import org.danceofvalkyries.utils.rest.jsonObject

class TestConfigRepository : ConfigRepository {

    private val gson = Gson()

    override fun getConfig(): Config {
        return jsonObject {
            "notion" to jsonObject {
                "api_key" to "1"
                "observed_databases" to arrayOf(
                    jsonObject {
                        "id" to "2" // Test DB 1
                        "dictionaries" to listOf<String>()
                    },
                    jsonObject {
                        "id" to "3" // Test DB 2
                        "dictionaries" to emptyList<String>()
                    },
                    jsonObject {
                        "id" to "4" // English Vocabulary
                        "dictionaries" to listOf("https://dictionary.cambridge.org/dictionary/english/")
                    },
                    jsonObject {
                        "id" to "5" // English Grammar
                        "dictionaries" to emptyList<String>()
                    },
                    jsonObject {
                        "id" to "6" // Greek Letters And Sounds
                        "dictionaries" to emptyList<String>()
                    },
                    jsonObject {
                        "id" to "7" // Greek Vocabulary
                        "dictionaries" to emptyList<String>()
                    },
                )
                "delay_between_requests" to 500
            }
            "telegram" to jsonObject {
                "api_key" to "8198451039:8"
                "chat_id" to "9"
            }
            "flash_cards_threshold" to 0
        }.let { gson.toJson(it) }.let { ConfigData(gson, it) }
    }
}