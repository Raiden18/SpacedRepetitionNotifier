package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.telegram.domain.models.Button
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

class MessageFactoryImpl : MessageFactory {

    override fun createDone(): TelegramMessageBody {
        return TelegramMessageBody(
            text = """Good Job! 😎 Everything is revised! ✅""",
            buttons = emptyList()
        )
    }

    override fun createNotification(
        group: SpacedRepetitionDataBaseGroup
    ): TelegramMessageBody {
        return TelegramMessageBody(
            text = """You have ${group.totalFlashCardsNeedRevising} flashcards to revise 🧠""".trimIndent(),
            buttons = group.group
                .filter { it.flashCards.isNotEmpty() }
                .map {
                    Button(
                        text = "${it.name}: ${it.flashCards.count()}",
                        url = "https://www.notion.so/databases/${it.id}"
                    )
                }
        )
    }

    override fun createFlashCardMessage(flashCard: FlashCard): TelegramMessageBody {
        val body = StringBuilder()
            .appendLine("*${flashCard.memorizedInfo}*")
            .appendLine()
            .appendLine("_${flashCard.example}_")
            .appendLine()
            .appendLine("||${flashCard.answer}||")
            .appendLine()
            .append("Choose:")
            .toString()

        return TelegramMessageBody(
            text = body,
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
}