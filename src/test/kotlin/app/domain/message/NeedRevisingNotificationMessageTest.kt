package app.domain.message

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.domain.message.notification.NeedRevisingNotificationMessage
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.telegram.api.models.TelegramButton
import org.danceofvalkyries.telegram.api.models.TelegramText

class NeedRevisingNotificationMessageTest : BehaviorSpec() {

    init {
        Given("Dbs and Flash Cards form them") {
            val englishVocabularyDb = NotionDataBase.EMPTY.copy(
                id = NotionId("1"),
                name = "English vocabulary"
            )
            val greekVocabularyDb = NotionDataBase.EMPTY.copy(
                id = NotionId("2"),
                name = "Greek vocabulary"
            )

            val englishGrammarDb = NotionDataBase.EMPTY.copy(
                id = NotionId("3"),
                name = "English grammar"
            )
            val flashCards = listOf(
                FlashCardNotionPage.EMPTY.copy(
                    notionDbID = englishVocabularyDb.id
                ),
                FlashCardNotionPage.EMPTY.copy(
                    notionDbID = greekVocabularyDb.id
                ),
                FlashCardNotionPage.EMPTY.copy(
                    notionDbID = englishGrammarDb.id
                )
            )

            When("Creates Telegram Message") {
                val telegramMessage = NeedRevisingNotificationMessage(
                    flashCards = flashCards,
                    notionDataBases = listOf(
                        englishVocabularyDb,
                        greekVocabularyDb,
                        englishGrammarDb,
                    )
                ).telegramBody

                Then("Should Show text with total count of unrevised flashcards") {
                    telegramMessage.text shouldBe TelegramText("""You have 3 flashcards to revise 🧠""".trimIndent())
                }

                Then("English Grammar Button should be shown") {
                    telegramMessage.nestedButtons[0][0] shouldBe TelegramButton(
                        text = "English vocabulary: 1",
                        url = null,
                        callback = "123123"
                    )
                }

                Then("Greek Vocabulary Button should be shown") {
                    telegramMessage.nestedButtons[1][0] shouldBe TelegramButton(
                        text = "Greek vocabulary: 1",
                        url = null,
                        callback = "123123"
                    )
                }

                Then("English Vocabulary Button should be shown") {
                    telegramMessage.nestedButtons[2][0] shouldBe TelegramButton(
                        text = "English grammar: 1",
                        url = null,
                        callback = "123123"
                    )
                }
            }
        }
    }
}