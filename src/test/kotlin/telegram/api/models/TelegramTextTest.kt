package telegram.api.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.telegram.api.models.TelegramText

class TelegramTextTest : FunSpec() {

    init {
        test("Should escape special characters") {
            val specialCharacters = "!()=._-\\\\"
            TelegramText(specialCharacters)
                .get() shouldBe "\\!\\(\\)\\=\\.\\_\\-\\"
        }
    }
}