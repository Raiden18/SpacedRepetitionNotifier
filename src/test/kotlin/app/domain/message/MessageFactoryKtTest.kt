package app.domain.message

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.danceofvalkyries.app.domain.message.MessageFactory
import org.danceofvalkyries.app.domain.message.MessageFactoryImpl
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBase
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.telegram.domain.models.Button
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

class MessageFactoryKtTest : FunSpec() {

    private lateinit var messageFactory: MessageFactory

    init {

        beforeTest {
            messageFactory = MessageFactoryImpl()
        }

        test("Should create Done message") {
            messageFactory.createDone() shouldBe TelegramMessageBody(
                text = """Good Job! 😎 Everything is revised! ✅""",
                buttons = emptyList(),
            )
        }

        test("Should create FlashCard message") {
            val flashCard = FlashCard(
                memorizedInfo = "Expect",
                example = "I expected you to come",
                answer = "to wait to happen in the future",
            )
            messageFactory.createFlashCardMessage(flashCard) shouldBe TelegramMessageBody(
                text = """
                    *Expect*
                    
                    _I expected you to come_
                    
                    ||to wait to happen in the future||
                    
                    Choose:""".trimIndent(),
                nestedButtons = listOf(
                    listOf(Button("Forgot  ❌", ""), Button("Recalled  ✅", "")),
                    listOf(
                        Button(
                            text = "Look it up",
                            url = "https://dictionary.cambridge.org/dictionary/english/${flashCard.memorizedInfo}"
                        ),
                    )
                )
            )
        }

        test("Should build revising needed message") {
            val spacedRepetitionDataBaseGroup = SpacedRepetitionDataBaseGroup(
                listOf(
                    SpacedRepetitionDataBase(
                        id = "1",
                        name = "English vocabulary",
                        flashCards = listOf(mockk())
                    ),
                    SpacedRepetitionDataBase(
                        id = "2",
                        name = "Greek vocabulary",
                        flashCards = listOf(mockk())
                    ),
                    SpacedRepetitionDataBase(
                        id = "3",
                        name = "English grammar",
                        flashCards = listOf(mockk())
                    ),
                    SpacedRepetitionDataBase(
                        id = "4",
                        name = "Greek grammar",
                        flashCards = emptyList()
                    )
                )
            )
            messageFactory.createNotification(spacedRepetitionDataBaseGroup) shouldBe TelegramMessageBody(
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
                )
            )
        }
    }
}