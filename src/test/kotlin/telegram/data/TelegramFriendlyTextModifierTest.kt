package telegram.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.telegram.data.api.TelegramFriendlyTextModifier


class TelegramFriendlyTextModifierTest : FunSpec() {

    init {
        test("Should escape special characters") {
            val specialCharacters = "!()=._-\\\\"
            TelegramFriendlyTextModifier()
                .modify(specialCharacters) shouldBe "\\!\\(\\)\\=\\.\\_\\-\\"
        }
    }
}