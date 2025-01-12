package app.domain.message

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.domain.message.MessageFactory
import org.danceofvalkyries.app.domain.message.MessageFactoryImpl
import org.danceofvalkyries.notion.domain.models.*
import org.danceofvalkyries.notion.domain.models.text.Text
import org.danceofvalkyries.telegram.data.api.TelegramFriendlyTextTextFormatter
import org.danceofvalkyries.telegram.domain.models.Button
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

class MessageFactoryKtTest : FunSpec() {

    private lateinit var messageFactory: MessageFactory

    init {

        beforeTest {
            messageFactory = MessageFactoryImpl(
                TelegramFriendlyTextTextFormatter()
            )
        }

        test("Should create Done message") {
            messageFactory.createDone() shouldBe TelegramMessageBody(
                text = """Good Job! 😎 Everything is revised! ✅""",
                buttons = emptyList(),
                imageUrl = null,
                type = TelegramMessageBody.Type.NOTIFICATION,
            )
        }

        test("Should create FlashCard message only with info and not supported url") {
            val flashCard = FlashCard.EMPTY.copy(
                memorizedInfo = Text("Expect"),
                example = null,
                answer = null,
                imageUrl = ImageUrl("https://www.shutterstock.com/image-vector/cute-baby-regurgitating-after-eating-260nw-2016458315.jpg"),
                onlineDictionaries = listOf(
                    OnlineDictionary("https://dictionary.cambridge.org/dictionary/english/")
                ),
            )

            messageFactory.createFlashCardMessage(flashCard) shouldBe TelegramMessageBody(
                text = """
                    *Expect*
                    
                    Choose:
                """.trimIndent(),
                nestedButtons = buttonsWithDictionary("Expect"),
                imageUrl = ImageUrl.BLUE_SCREEN,
                type = TelegramMessageBody.Type.FLASH_CARD,
            )
        }

        test("Should create flashcard without dictionary button") {
            val flashCard = FlashCard.EMPTY.copy(
                memorizedInfo = Text("Expect"),
                example = null,
                answer = null,
                imageUrl = ImageUrl("url"),
                onlineDictionaries = emptyList(),
            )
            messageFactory
                .createFlashCardMessage(flashCard)
                .nestedButtons shouldBe buttonsWithoutDictionary()
        }

        test("Should create Full FlashCard message") {
            val flashCard = FlashCard.EMPTY.copy(
                memorizedInfo = Text("Expect"),
                example = Text("I expected you to come"),
                answer = Text("to wait to happen in the future"),
                imageUrl = ImageUrl("url"),
                onlineDictionaries = listOf(
                    OnlineDictionary("https://dictionary.cambridge.org/dictionary/english/")
                )
            )
            messageFactory.createFlashCardMessage(flashCard) shouldBe TelegramMessageBody(
                text = """
                    *Expect*
                    
                    _I expected you to come_
                    
                    ||to wait to happen in the future||
                    
                    Choose:""".trimIndent(),
                nestedButtons = buttonsWithDictionary("Expect"),
                imageUrl = ImageUrl("url"),
                type = TelegramMessageBody.Type.FLASH_CARD,
            )
        }

        test("Should build revising needed message") {
            val emptyFLashCard = FlashCard.EMPTY

            val englishVocabularyDb = NotionDataBase.EMPTY.copy(
                id = NotionDbId("1"),
                name = "English vocabulary"
            )

            val greekVocabularyDb = NotionDataBase.EMPTY.copy(
                id = NotionDbId("2"),
                name = "Greek vocabulary"
            )

            val englishGrammarDb = NotionDataBase.EMPTY.copy(
                id = NotionDbId("3"),
                name = "English grammar"
            )

            val flashCards = listOf(
                emptyFLashCard.copy(
                    metaInfo = FlashCard.MetaInfo(
                        notionDbId = englishVocabularyDb.id,
                        id = ""
                    )
                ),
                emptyFLashCard.copy(
                    metaInfo = FlashCard.MetaInfo(
                        notionDbId = greekVocabularyDb.id,
                        id = ""
                    )
                ),
                emptyFLashCard.copy(
                    metaInfo = FlashCard.MetaInfo(
                        notionDbId = englishGrammarDb.id,
                        id = ""
                    )
                )
            )
            messageFactory.createNotification(
                flashCards = flashCards,
                notionDataBases = listOf(
                    englishVocabularyDb,
                    greekVocabularyDb,
                    englishGrammarDb,
                )

            ) shouldBe TelegramMessageBody(
                text = """You have 3 flashcards to revise 🧠""".trimIndent(),
                nestedButtons = listOf(
                    listOf(
                        Button(
                            text = "English vocabulary: 1",
                            url = "https://www.notion.so/databases/1"
                        )
                    ),
                    listOf(
                        Button(
                            text = "Greek vocabulary: 1",
                            url = "https://www.notion.so/databases/2"
                        )
                    ),
                    listOf(
                        Button(
                            text = "English grammar: 1",
                            url = "https://www.notion.so/databases/3"
                        )
                    )
                ),
                imageUrl = null,
                type = TelegramMessageBody.Type.NOTIFICATION,
            )
        }
    }

    private fun buttonsWithDictionary(memorizedInfo: String) = listOf(
        resultButtons(),
        listOf(
            Button(
                text = "Look it up",
                url = "https://dictionary.cambridge.org/dictionary/english/${memorizedInfo}"
            ),
        )
    )

    private fun buttonsWithoutDictionary() = listOf(
        resultButtons(),
        emptyList(),
    )

    private fun resultButtons(): List<Button> {
        return listOf(Button("Forgot  ❌", ""), Button("Recalled  ✅", ""))
    }
}