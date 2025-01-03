package telegram.api.rest.request.bodies

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.telegram.api.rest.request.bodies.SendMessageBody

class SendMessageBodyKtTest : FunSpec() {

    init {
        test("Should create body for message") {
            val chatId = "228"
            val text = "3228"
            val gson = GsonBuilder()
                .setPrettyPrinting()
                .create()
            SendMessageBody(
                gson,
                chatId,
                text,
            ) shouldBe """
            {
              "chat_id": "$chatId",
              "text": "$text",
              "parse_mode": "Markdown"
            }
            """.trimIndent()
        }
    }
}