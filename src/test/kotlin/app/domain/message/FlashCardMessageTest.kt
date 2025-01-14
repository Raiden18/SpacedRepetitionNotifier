package app.domain.message

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.domain.message.FlashCardMessage
import org.danceofvalkyries.dictionary.api.OnlineDictionary
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.telegram.api.models.TelegramButton
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.telegram.api.models.TelegramText

class FlashCardMessageTest : BehaviorSpec() {

    init {
        Given("FlashCard with full data") {
            val flashCardId = "228"
            val flashCard = FlashCardNotionPage.EMPTY.copy(
                name = "Expect",
                example = "I expected you to come",
                explanation = "to wait to happen in the future",
                coverUrl = "url",
                id = NotionId(flashCardId)
            )

            And("With Dictionary") {
                val dictionary = OnlineDictionary("https://dictionary.cambridge.org/dictionary/english/")

                When("Creates FlashCardMessage") {
                    lateinit var messageBody: TelegramMessageBody

                    beforeTest {
                        messageBody = FlashCardMessage(flashCard, listOf(dictionary)).telegramBody
                    }

                    Then("Should create formatted text") {
                        messageBody.text shouldBe TelegramText(
                            """
                                 *Expect*
                                 
                                 _I expected you to come_
                                 
                                 ||to wait to happen in the future||
                                 
                                 Choose:""".trimIndent(),
                        )
                    }

                    Then("Action Buttons Should be Shown") {
                        messageBody.nestedButtons.first() shouldBe actonButtons(flashCardId)
                    }

                    Then("Dictionary Buttons should be shown") {
                        messageBody.nestedButtons.last() shouldBe listOf(
                            DictionaryButton("Expect")
                        )
                    }
                }
            }

            And("Without Dictionary") {
                When("Creates FlashCardMessage") {
                    lateinit var messageBody: TelegramMessageBody

                    beforeTest {
                        messageBody = FlashCardMessage(flashCard, emptyList()).telegramBody
                    }

                    Then("Dictionary Buttons should be hidden") {
                        messageBody.nestedButtons.last() shouldBe listOf()
                    }
                }
            }

        }

        Given("FlashCard with minimum data") {
            val flashCardId = "228"
            val flashCard = FlashCardNotionPage.EMPTY.copy(
                name = "Expect",
                example = null,
                explanation = null,
                coverUrl = null,
                id = NotionId(flashCardId),
            )

            When("Creates FlashCardMessage") {
                lateinit var messageBody: TelegramMessageBody
                beforeTest {
                    messageBody = FlashCardMessage(flashCard, emptyList()).telegramBody
                }

                Then("Should Create Text") {
                    messageBody.text shouldBe TelegramText(
                        """
                        *Expect*
                        
                        Choose:
                        """.trimIndent()
                    )
                }

                Then("Action Buttons Should be Shown") {
                    messageBody.nestedButtons.first() shouldBe actonButtons(flashCardId)
                }
            }
        }
    }

    private fun actonButtons(
        flashCardIs: String
    ): List<TelegramButton> {
        return listOf(
            TelegramButton("Forgot  ❌", TelegramButton.Action.CallBackData("forgottenFlashCardId=$flashCardIs")),
            TelegramButton("Recalled  ✅", TelegramButton.Action.CallBackData("recalledFlashCardId=$flashCardIs")),
        )
    }

    private fun DictionaryButton(name: String): TelegramButton {
        return TelegramButton(
            text = "Look it up",
            action = TelegramButton.Action.Url("https://dictionary.cambridge.org/dictionary/english/${name}"),
        )
    }
}