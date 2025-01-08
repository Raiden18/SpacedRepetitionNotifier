package telegram.api.rest.request.bodies

import com.google.gson.GsonBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.telegram.data.api.rest.request.bodies.SendMessageBody

class MessageBodyKtTest : FunSpec() {

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