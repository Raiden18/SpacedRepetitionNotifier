package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.ImageUrl.Companion.BLUE_SCREEN
import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.telegram.domain.models.Button
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

class MessageFactoryImpl : MessageFactory {

    override fun createDone(): TelegramMessageBody {
        return TelegramMessageBody(
            text = """Good Job! 😎 Everything is revised! ✅""",
            buttons = emptyList(),
            imageUrl = null,
            type = TelegramMessageBody.Type.NOTIFICATION,
        )
    }

    override fun createNotification(
        flashCards: List<FlashCard>,
        notionDataBases: List<NotionDataBase>,
    ): TelegramMessageBody {
        val buttons = flashCards
            .groupBy { it.metaInfo.notionDbId }
            .map { (dbId, flashCards) ->
                val db = notionDataBases.first { it.id == dbId }
                Button(
                    text = "${db.name}: ${flashCards.count()}",
                    url = "https://www.notion.so/databases/${db.id.valueId}"
                )
            }

        return TelegramMessageBody(
            text = """You have ${flashCards.count()} flashcards to revise 🧠""".trimIndent(),
            buttons = buttons,
            imageUrl = null,
            type = TelegramMessageBody.Type.FLASH_CARD,
        )
    }

    override fun createFlashCardMessage(
        flashCard: FlashCard,
    ): TelegramMessageBody {
        val body = StringBuilder()
            .appendLine("*${flashCard.memorizedInfoValue}*")
        if (flashCard.exampleValue != null) {
            body.appendLine()
                .appendLine("_${flashCard.exampleValue}_")
        }
        if (flashCard.answerValue != null) {
            body.appendLine()
                .appendLine("||${flashCard.answerValue}||")
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
                        url = "https://dictionary.cambridge.org/dictionary/english/${flashCard.memorizedInfoValue}"
                    ),
                )
            ),
            imageUrl = imageUrl,
            type = TelegramMessageBody.Type.FLASH_CARD,
        )
    }
}