package org.danceofvalkyries.job.data.telegram.message.local

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.utils.resources.EngStringResources

class DoneTelegramMessageTest : FunSpec() {
    init {
        test("Should return text for Done Message") {
            DoneTelegramMessage(
                EngStringResources()
            ).text shouldBe """Good Job! 😎 Everything is revised! ✅"""
        }
    }
}