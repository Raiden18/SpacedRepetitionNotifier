package org.danceofvalkyries.job.data.telegram.message.local

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.telegram.message.local.DoneTelegramMessage
import org.danceofvalkyries.utils.resources.EngStringResources

class DoneTelegramMessageTest : FunSpec() {
    init {
        test("Should return text for Done Message") {
            DoneTelegramMessage(
                EngStringResources()
            ).getText() shouldBe "Good Job\\! 😎 Everything is revised\\! ✅"
        }
    }
}