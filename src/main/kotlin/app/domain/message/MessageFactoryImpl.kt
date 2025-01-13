package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.dictionary.api.OnlineDictionary
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.NotionDataBase
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
        flashCards: List<FlashCardNotionPage>,
        notionDataBases: List<NotionDataBase>,
    ): TelegramMessageBody {
        val telegramButtons = flashCards
            .groupBy { it.notionDbID }
            .map { (dbId, flashCards) ->
                val db = notionDataBases.first { it.id == dbId }
                TelegramButton(
                    text = "${db.name}: ${flashCards.count()}",
                    url = null,
                    callback = dbId.get(),
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
        flashCard: FlashCardNotionPage,
        onlineDictionaries: List<OnlineDictionary>,
    ): TelegramMessageBody {
        val memorizedInfo = flashCard.name
        val example = flashCard.example
        val answer = flashCard.explanation

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

        val recallActions = listOf(TelegramButton("Forgot  ❌", null, "123"), TelegramButton("Recalled  ✅", null, "123"))

        val dictionaryTelegramButtons = onlineDictionaries
            .map {
                TelegramButton(
                    text = "Look it up",
                    url = it.getWordUrl(memorizedInfo),
                    callback = null,
                )
            }

        return TelegramMessageBody(
            text = TelegramText(body.toString()),
            nestedButtons = listOf(
                recallActions,
                dictionaryTelegramButtons
            ),
            imageUrl = flashCard.coverUrl?.let(::TelegramImageUrl),
            type = TelegramMessageBody.Type.FLASH_CARD,
        )
    }
}