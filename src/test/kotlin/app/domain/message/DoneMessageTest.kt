package app.domain.message

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.domain.message.notification.DoneMessage
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

class DoneMessageTest : FunSpec() {

    init {
        test("Should return Done message") {
            DoneMessage().asTelegramBody() shouldBe TelegramMessageBody(
                text = """Good Job! 😎 Everything is revised! ✅""",
                telegramButtons = emptyList(),
                telegramImageUrl = null,
            )
        }
    }
}