package telegram.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBase
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.telegram.domain.models.Button
import org.danceofvalkyries.telegram.domain.models.DoneMessage
import org.danceofvalkyries.telegram.domain.models.RevisingIsNeededMessage
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

class TelegramMessageBodyTest : FunSpec() {

    init {
        test("Should build good job message") {
            DoneMessage() shouldBe TelegramMessageBody(
                text = """Good Job! 😎 Everything is revised! ✅""",
                buttons = emptyList(),
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
            RevisingIsNeededMessage(
                spacedRepetitionDataBaseGroup
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
                )
            )
        }
    }

}