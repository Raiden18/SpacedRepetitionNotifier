package notion.domain.models.formatter

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.telegram.data.api.TelegramFriendlyTextFormatter


class TelegramFriendlyTextStringFormatterTest : FunSpec() {

    init {
        test("Should escape special characters") {

            val specialCharacters = "!()=._-\\\\"
            TelegramFriendlyTextFormatter()
                .format(specialCharacters) shouldBe "\\!\\(\\)\\=\\.\\_\\-\\"
        }
    }
}