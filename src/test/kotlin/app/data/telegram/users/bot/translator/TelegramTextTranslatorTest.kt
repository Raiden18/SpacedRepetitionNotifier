package app.data.telegram.users.bot.translator

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.data.telegram.users.bot.translator.TelegramTextTranslator

class TelegramTextTranslatorTest : FunSpec() {

    init {
        test("Should translate text to acceptable by Telegram form") {
            val unacceptedByTelegramText = "{}|#<>`~[]*!()=._-+\\\\"
            TelegramTextTranslator().encode(unacceptedByTelegramText) shouldBe "\\{\\}\\|\\#\\<\\>\\`\\~\\[\\]\\*\\!\\(\\)\\=\\.\\_\\-\\+\\"
        }
    }
}