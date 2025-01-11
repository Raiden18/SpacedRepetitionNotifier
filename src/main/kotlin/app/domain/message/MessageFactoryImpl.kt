package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.app.domain.message.MessageFactory.Notification
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.ImageUrl.Companion.BLUE_SCREEN
import org.danceofvalkyries.notion.domain.models.FlashCardsTablesGroup
import org.danceofvalkyries.telegram.domain.models.Button
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

class MessageFactoryImpl : MessageFactory {

    override fun createDone(): TelegramMessageBody {
        return TelegramMessageBody(
            text = """Good Job! 😎 Everything is revised! ✅""",
            buttons = emptyList(),
            imageUrl = null,
        )
    }

    override fun createNotification(
        notification: Notification
    ): TelegramMessageBody {
        return TelegramMessageBody(
            text = """You have ${notification.totalCount} flashcards to revise 🧠""".trimIndent(),
            buttons = notification
                .dataBases
                .map {
                    Button(
                        text = "${it.name}: ${it.flashCardsNeedRevising}",
                        url = "https://www.notion.so/databases/${it.id}"
                    )
                },
            imageUrl = null,
        )
    }

    override fun createFlashCardMessage(
        flashCard: FlashCard,
    ): TelegramMessageBody {
        val body = StringBuilder()
            .appendLine("*${flashCard.memorizedInfo}*")
        if (flashCard.example != null) {
            body.appendLine()
                .appendLine("_${flashCard.example}_")
        }
        if (flashCard.answer != null) {
            body.appendLine()
                .appendLine("||${flashCard.answer}||")
        }
        body.appendLine()
            .append("Choose:")

        val imageUrl = if (flashCard.imageUrl?.isSupportedByTelegram == false) {
            BLUE_SCREEN
        } else {
            flashCard.imageUrl
        }

        return TelegramMessageBody(
            text = body.toString(),
            nestedButtons = listOf(
                listOf(Button("Forgot  ❌", ""), Button("Recalled  ✅", "")),
                listOf(
                    Button(
                        text = "Look it up",
                        url = "https://dictionary.cambridge.org/dictionary/english/${flashCard.memorizedInfo}"
                    ),
                )
            ),
            imageUrl = imageUrl,
        )
    }
}