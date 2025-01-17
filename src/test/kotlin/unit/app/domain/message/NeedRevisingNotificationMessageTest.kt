package unit.app.domain.message

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage
import org.danceofvalkyries.telegram.api.models.TelegramButton
import org.danceofvalkyries.telegram.api.models.TelegramText
import utils.NotionDataBaseFake
import utils.NotionDataBasesFake
import utils.NotionPageFlashCardFake

class NeedRevisingNotificationMessageTest : BehaviorSpec() {

    init {
        Given("Dbs and Flash Cards form them") {
            val englishVocabularyDb = NotionDataBaseFake(
                id = "1",
                name = "English vocabulary",
                pages = listOf(
                    NotionPageFlashCardFake(
                        notionDbID = "1"
                    )
                )
            )
            val greekVocabularyDb = NotionDataBaseFake(
                id = "2",
                name = "Greek vocabulary",
                pages = listOf(
                    NotionPageFlashCardFake(
                        notionDbID = "2"
                    )
                )
            )

            val englishGrammarDb = NotionDataBaseFake(
                id = "3",
                name = "English grammar",
                pages = listOf(
                    NotionPageFlashCardFake(
                        notionDbID = "3"
                    )
                )
            )
            When("Creates Telegram Message") {
                val telegramMessage = NeedRevisingNotificationMessage(
                    NotionDataBasesFake(listOf(englishVocabularyDb, greekVocabularyDb, englishGrammarDb))
                ).asTelegramBody()

                Then("Should Show text with total count of unrevised flashcards") {
                    telegramMessage.text shouldBe TelegramText("""You have 3 flashcards to revise 🧠""".trimIndent())
                }

                Then("English Grammar Button should be shown") {
                    telegramMessage.nestedButtons[0][0] shouldBe TelegramButton(
                        text = "English vocabulary: 1",
                        action = TelegramButton.Action.CallBackData("dbId=1")
                    )
                }

                Then("Greek Vocabulary Button should be shown") {
                    telegramMessage.nestedButtons[1][0] shouldBe TelegramButton(
                        text = "Greek vocabulary: 1",
                        action = TelegramButton.Action.CallBackData("dbId=2")
                    )
                }

                Then("English Vocabulary Button should be shown") {
                    telegramMessage.nestedButtons[2][0] shouldBe TelegramButton(
                        text = "English grammar: 1",
                        action = TelegramButton.Action.CallBackData("dbId=3")
                    )
                }
            }
        }
    }
}