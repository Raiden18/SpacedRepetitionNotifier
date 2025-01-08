package telegram.api.rest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.telegram.data.api.rest.TelegramChatUrls

class TelegramUrlsTest : FunSpec() {
    private val apiKey = "228"
    private val chatId = "2"

    private val telegramChatUrl = TelegramChatUrls(
        apiKey = apiKey,
        chatId = chatId,
    )

    init {
        test("Should create url for send message") {
            telegramChatUrl
                .sendMessage()
                .toString() shouldBe "https://api.telegram.org/bot$apiKey/sendMessage"
        }

        test("Should create url for getUpdates message") {
            telegramChatUrl
                .getUpdates(1)
                .toString() shouldBe "https://api.telegram.org/bot$apiKey/getUpdates?limit=1"
        }

        test("Should return url for deleteMessages") {
            val messageId = 1L
            telegramChatUrl
                .deleteMessage(messageId)
                .toString() shouldBe "https://api.telegram.org/bot$apiKey/deleteMessage?chat_id=$chatId&message_id=$messageId"
        }

        test("Should create editMessageText") {
            telegramChatUrl.editMessageText()
                .toString() shouldBe "https://api.telegram.org/bot$apiKey/editMessageText"
        }
    }
}