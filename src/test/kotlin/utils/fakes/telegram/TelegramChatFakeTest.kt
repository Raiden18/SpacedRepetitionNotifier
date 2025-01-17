package utils.fakes.telegram

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class TelegramChatFakeTest : FunSpec() {

    private lateinit var telegramChatFake: TelegramChatFake

    init {
        beforeTest {
            telegramChatFake = TelegramChatFake()
        }

        test("Should create message with increased notification id starting from 1") {
            telegramChatFake.sendTextMessage(
                text = "message 1",
                nestedButtons = emptyList(),
            ) shouldBe TelegramMessageFake(
                id = 1,
                text = "message 1",
                imageUrl = null,
                nestedButtons = emptyList()
            )

            telegramChatFake.sendTextMessage(
                text = "message 2",
                nestedButtons = emptyList(),
            ) shouldBe TelegramMessageFake(
                id = 2,
                text = "message 2",
                imageUrl = null,
                nestedButtons = emptyList()
            )
        }

        test("Should remove sent messages by id") {
            val message = telegramChatFake.sendTextMessage(
                text = "message 1",
                nestedButtons = emptyList(),
            )

            telegramChatFake.delete(message.getId())

            telegramChatFake.assertThat()
                .wasDeleted(message)
        }

        test("Should increase ID even though previous message was deleted") {
            val message1 = telegramChatFake.sendTextMessage(
                text = "message 1",
                nestedButtons = emptyList(),
            )
            telegramChatFake.delete(message1.getId())

            telegramChatFake.sendTextMessage(
                text = "message 2",
                nestedButtons = emptyList(),
            ) shouldBe TelegramMessageFake(
                id = 2,
                text = "message 2",
                imageUrl = null,
                nestedButtons = emptyList()
            )
        }

        test("Should replace old message with new one") {
            val message1 = telegramChatFake.sendTextMessage(
                text = "message 1",
                nestedButtons = emptyList(),
            )

            val editedMessage = telegramChatFake.edit(
                messageId = message1.getId(),
                newText = "228",
                newNestedButtons = emptyList()
            )

            message1.getId() shouldBe editedMessage.getId()
            message1.getText() shouldNotBe editedMessage.getText()
        }

        test("Should send photo message") {
            telegramChatFake.sendPhotoMessage(
                caption = "caption 1",
                imageUrl = "image url",
                nestedButtons = emptyList()
            ) shouldBe TelegramMessageFake(
                id = 1,
                text = "caption 1",
                imageUrl = "image url",
                nestedButtons = emptyList()
            )
        }
    }
}