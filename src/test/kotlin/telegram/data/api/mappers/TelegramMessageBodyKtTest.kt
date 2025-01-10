package telegram.data.api.mappers

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.telegram.data.api.mappers.textWithEscapedCharacters
import org.danceofvalkyries.telegram.domain.TelegramMessageBody

class TelegramMessageBodyKtTest : FunSpec() {

    init {
        test("Should escape special symbols"){
            TelegramMessageBody(
                text = "!()=.",
                nestedButtons = emptyList()
            ).textWithEscapedCharacters() shouldBe "\\!\\(\\)\\="
        }
    }
}