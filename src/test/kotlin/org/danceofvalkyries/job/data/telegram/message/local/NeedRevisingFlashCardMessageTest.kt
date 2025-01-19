package org.danceofvalkyries.job.data.telegram.message.local

import integrations.testdata.english.vocabulary.Dota2EnglishVocabulary
import integrations.testdata.english.vocabulary.EnglishVocabularyDataBaseFake
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.job.data.telegram.message.ConstantTelegramMessageButton
import org.danceofvalkyries.job.data.telegram.message.TelegramMessage
import org.danceofvalkyries.utils.resources.EngStringResources

class NeedRevisingFlashCardMessageTest : FunSpec() {

    init {
        context("Need revising Notification message") {
            val needRevisingNotificationMessage = NeedRevisingNotificationMessage(
                stringResources = EngStringResources(),
                flashCards = listOf(Dota2EnglishVocabulary(EnglishVocabularyDataBaseFake.ID)),
                notionDataBases = listOf(EnglishVocabularyDataBaseFake())
            )

            test("Should return text") {
                needRevisingNotificationMessage.text shouldBe """You have 1 flashcards to revise 🧠"""
            }

            test("Should return nested buttons") {
                needRevisingNotificationMessage.nestedButtons shouldBe listOf(
                    listOf(
                        ConstantTelegramMessageButton(
                            text = "English Vocabulary: 1",
                            action = TelegramMessage.Button.Action.CallBackData("dbId=${EnglishVocabularyDataBaseFake.ID}"),
                        )
                    )
                )
            }
        }
    }
}