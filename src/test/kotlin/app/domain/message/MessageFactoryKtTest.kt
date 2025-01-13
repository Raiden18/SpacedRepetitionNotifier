package app.domain.message

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.domain.message.MessageFactory
import org.danceofvalkyries.app.domain.message.MessageFactoryImpl
import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.app.domain.models.OnlineDictionary
import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.notion.domain.models.NotionId
import org.danceofvalkyries.telegram.domain.models.TelegramButton
import org.danceofvalkyries.telegram.domain.models.TelegramImageUrl
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody
import org.danceofvalkyries.telegram.domain.models.TelegramText

class MessageFactoryKtTest : FunSpec() {

    private lateinit var messageFactory: MessageFactory

    init {

        beforeTest {
            messageFactory = MessageFactoryImpl()
        }

        test("Should create Done message") {
            messageFactory.createDone() shouldBe TelegramMessageBody(
                text = """Good Job! 😎 Everything is revised! ✅""",
                telegramButtons = emptyList(),
                telegramImageUrl = null,
                type = TelegramMessageBody.Type.NOTIFICATION,
            )
        }

        test("Should create FlashCard message only with info") {
            val imageUrl = "https://www.shutterstock.com/image-vector/cute-baby-regurgitating-after-eating-260nw-2016458315.jpg"
            val flashCard = FlashCard.EMPTY.copy(
                memorizedInfo = "Expect",
                example = null,
                answer = null,
                telegramImageUrl = imageUrl,
                onlineDictionaries = listOf(
                    OnlineDictionary("https://dictionary.cambridge.org/dictionary/english/")
                ),
            )

            messageFactory.createFlashCardMessage(flashCard) shouldBe TelegramMessageBody(
                text = TelegramText(
                    """
                    *Expect*
                    
                    Choose:
                """.trimIndent()
                ),
                nestedButtons = buttonsWithDictionary("Expect"),
                telegramImageUrl = TelegramImageUrl(imageUrl),
                type = TelegramMessageBody.Type.FLASH_CARD,
            )
        }

        test("Should create flashcard without dictionary button") {
            val flashCard = FlashCard.EMPTY.copy(
                memorizedInfo = "Expect",
                example = null,
                answer = null,
                telegramImageUrl = "url",
                onlineDictionaries = emptyList(),
            )
            messageFactory
                .createFlashCardMessage(flashCard)
                .nestedButtons shouldBe buttonsWithoutDictionary()
        }

        test("Should create Full FlashCard message") {
            val flashCard = FlashCard.EMPTY.copy(
                memorizedInfo = "Expect",
                example = "I expected you to come",
                answer = "to wait to happen in the future",
                telegramImageUrl = "url",
                onlineDictionaries = listOf(
                    OnlineDictionary("https://dictionary.cambridge.org/dictionary/english/")
                )
            )
            messageFactory.createFlashCardMessage(flashCard) shouldBe TelegramMessageBody(
                text = TelegramText(
                    """
                    *Expect*
                    
                    _I expected you to come_
                    
                    ||to wait to happen in the future||
                    
                    Choose:""".trimIndent()
                ),
                nestedButtons = buttonsWithDictionary("Expect"),
                telegramImageUrl = TelegramImageUrl("url"),
                type = TelegramMessageBody.Type.FLASH_CARD,
            )
        }

        test("Should build revising needed message") {
            val emptyFLashCard = FlashCard.EMPTY

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
                emptyFLashCard.copy(
                    metaInfo = FlashCard.MetaInfo(
                        notionDbId = englishVocabularyDb.id.get(NotionId.Modifier.AS_IS),
                        id = ""
                    )
                ),
                emptyFLashCard.copy(
                    metaInfo = FlashCard.MetaInfo(
                        notionDbId = greekVocabularyDb.id.get(NotionId.Modifier.AS_IS),
                        id = ""
                    )
                ),
                emptyFLashCard.copy(
                    metaInfo = FlashCard.MetaInfo(
                        notionDbId = englishGrammarDb.id.get(NotionId.Modifier.AS_IS),
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
                text = TelegramText("""You have 3 flashcards to revise 🧠""".trimIndent()),
                nestedButtons = listOf(
                    listOf(
                        TelegramButton(
                            text = "English vocabulary: 1",
                            url = "https://www.notion.so/databases/1"
                        )
                    ),
                    listOf(
                        TelegramButton(
                            text = "Greek vocabulary: 1",
                            url = "https://www.notion.so/databases/2"
                        )
                    ),
                    listOf(
                        TelegramButton(
                            text = "English grammar: 1",
                            url = "https://www.notion.so/databases/3"
                        )
                    )
                ),
                telegramImageUrl = null,
                type = TelegramMessageBody.Type.NOTIFICATION,
            )
        }
    }

    private fun buttonsWithDictionary(memorizedInfo: String) = listOf(
        resultButtons(),
        listOf(
            TelegramButton(
                text = "Look it up",
                url = "https://dictionary.cambridge.org/dictionary/english/${memorizedInfo}"
            ),
        )
    )

    private fun buttonsWithoutDictionary() = listOf(
        resultButtons(),
        emptyList(),
    )

    private fun resultButtons(): List<TelegramButton> {
        return listOf(TelegramButton("Forgot  ❌", ""), TelegramButton("Recalled  ✅", ""))
    }
}