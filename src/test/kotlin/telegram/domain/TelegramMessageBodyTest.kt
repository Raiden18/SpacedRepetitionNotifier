package telegram.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBase
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.telegram.domain.Button
import org.danceofvalkyries.telegram.domain.TelegramMessageBody

class TelegramMessageBodyTest : FunSpec() {

    init {
        test("Should build good job message") {
            TelegramMessageBody.done() shouldBe TelegramMessageBody(
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
                        flashCards = listOf(FlashCard)
                    ),
                    SpacedRepetitionDataBase(
                        id = "2",
                        name = "Greek vocabulary",
                        flashCards = listOf(FlashCard)
                    ),
                    SpacedRepetitionDataBase(
                        id = "3",
                        name = "English grammar",
                        flashCards = listOf(FlashCard)
                    ),
                    SpacedRepetitionDataBase(
                        id = "4",
                        name = "Greek grammar",
                        flashCards = emptyList()
                    )
                )
            )
            TelegramMessageBody.revisingIsNeeded(
                spacedRepetitionDataBaseGroup
            ) shouldBe TelegramMessageBody(
                text = """You have 3 flashcards to revise 🧠""".trimIndent(),
                buttons = listOf(
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