package job.data.telegram.users.bot.translator

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.job.data.telegram.message.local.translator.TelegramTextTranslator

class TelegramTextTranslatorTest : FunSpec() {

    init {
        test("Should translate text to acceptable by Telegram form") {
            val unacceptedByTelegramText = "{}|#<>`~[]*!()=._-+\\\\"
            TelegramTextTranslator().encode(unacceptedByTelegramText) shouldBe "\\{\\}\\|\\#\\<\\>\\`\\~\\[\\]\\*\\!\\(\\)\\=\\.\\_\\-\\+\\"
        }
    }
}