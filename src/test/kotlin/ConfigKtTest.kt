import com.google.gson.Gson
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.Config
import org.danceofvalkyries.Notion
import org.danceofvalkyries.Telegram
import org.danceofvalkyries.json.`object`

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

            Config(
                Gson(),
                jsonString,
            ) shouldBe Config(
                notion = Notion(
                    apiKey = "1",
                    observedDatabases = listOf("2", "3", "4", "5"),
                    delayBetweenRequests = 500
                ),
                telegram = Telegram(
                    apiKey = "6",
                    chatId = "7"
                ),
                flashCardsThreshold = 10
            )
        }
    }
}