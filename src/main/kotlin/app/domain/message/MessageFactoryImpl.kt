package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.telegram.api.models.TelegramButton
import org.danceofvalkyries.telegram.api.models.TelegramImageUrl
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.telegram.api.models.TelegramText

class MessageFactoryImpl : MessageFactory {

    override fun createDone(): TelegramMessageBody {
        return TelegramMessageBody(
            text = """Good Job! 😎 Everything is revised! ✅""",
            telegramButtons = emptyList(),
            telegramImageUrl = null,
            type = TelegramMessageBody.Type.NOTIFICATION,
        )
    }

    override fun createNotification(
        flashCards: List<FlashCard>,
        notionDataBases: List<NotionDataBase>,
    ): TelegramMessageBody {
        val telegramButtons = flashCards
            .groupBy { it.metaInfo.notionDbId }
            .map { (dbId, flashCards) ->
                val db = notionDataBases.first { it.id.get(NotionId.Modifier.URL_FRIENDLY) == dbId }
                TelegramButton(
                    text = "${db.name}: ${flashCards.count()}",
                    url = "https://www.notion.so/databases/${db.id.get(NotionId.Modifier.URL_FRIENDLY)}"
                )
            }

        return TelegramMessageBody(
            text = """You have ${flashCards.count()} flashcards to revise 🧠""".trimIndent(),
            telegramButtons = telegramButtons,
            telegramImageUrl = null,
            type = TelegramMessageBody.Type.NOTIFICATION,
        )
    }

    override fun createFlashCardMessage(
        flashCard: FlashCard,
    ): TelegramMessageBody {
        val memorizedInfo = flashCard.memorizedInfo
        val example = flashCard.example
        val answer = flashCard.answer

        val body = StringBuilder()
            .appendLine("*${memorizedInfo}*")
        if (example != null) {
            body.appendLine()
                .appendLine("_${example}_")
        }
        if (answer != null) {
            body.appendLine()
                .appendLine("||${answer}||")
        }
        body.appendLine()
            .append("Choose:")

        val imageUrl = flashCard.telegramImageUrl

        val recallActions = listOf(TelegramButton("Forgot  ❌", ""), TelegramButton("Recalled  ✅", ""))
        val dictionaryTelegramButtons = flashCard.onlineDictionaries
            .map {
                TelegramButton(
                    text = "Look it up",
                    url = it.getWordUrl(memorizedInfo)
                )
            }

        return TelegramMessageBody(
            text = TelegramText(body.toString()),
            nestedButtons = listOf(
                recallActions,
                dictionaryTelegramButtons
            ),
            imageUrl = imageUrl?.let(::TelegramImageUrl),
            type = TelegramMessageBody.Type.FLASH_CARD,
        )
    }
}