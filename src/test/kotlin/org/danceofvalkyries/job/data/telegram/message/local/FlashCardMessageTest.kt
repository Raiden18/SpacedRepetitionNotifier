package org.danceofvalkyries.job.data.telegram.message.local

import integrations.testdata.english.vocabulary.Dota2EnglishVocabulary
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.dictionary.constant.ConstantOnlineDictionary
import org.danceofvalkyries.telegram.message.ConstantTelegramMessageButton
import org.danceofvalkyries.telegram.message.TelegramMessage
import org.danceofvalkyries.telegram.message.local.FlashCardMessage
import org.danceofvalkyries.utils.resources.EngStringResources

class FlashCardMessageTest : FunSpec() {

    init {
        context("Dota 2 Flash with online dictionary") {
            val flashCardMessage = FlashCardMessage(
                flashCard = Dota2EnglishVocabulary("1"),
                stringResources = EngStringResources(),
                onlineDictionaries = listOf(ConstantOnlineDictionary("https://dictionary.cambridge.org/dictionary/english"))
            )

            test("Should return text") {
                flashCardMessage.text shouldBe """
                    *Dota 2*
                    
                    _Dota 2 is the best game in the world_
                    
                    ||Mid or feed||
                    
                    Choose:
                """.trimIndent()
            }

            test("Should return action buttons with dictionary button") {
                flashCardMessage.nestedButtons shouldBe listOf(
                    listOf(
                        ConstantTelegramMessageButton(
                            "Forgot  ❌",
                            TelegramMessage.Button.Action.CallBackData("forgottenFlashCardId=dota_2_english_vocabulary_1"),
                        ),
                        ConstantTelegramMessageButton(
                            "Recalled  ✅",
                            TelegramMessage.Button.Action.CallBackData("recalledFlashCardId=dota_2_english_vocabulary_1"),
                        )
                    ),
                    listOf(
                        ConstantTelegramMessageButton(
                            text = "Look it up",
                            action = TelegramMessage.Button.Action.Url("https://dictionary.cambridge.org/dictionary/english/Dota%202"),
                        )
                    )
                )
            }
        }
    }
}