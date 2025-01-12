package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.ImageUrl.Companion.BLUE_SCREEN
import org.danceofvalkyries.notion.domain.models.NotionDataBase
import org.danceofvalkyries.notion.domain.models.text.TextFormatter
import org.danceofvalkyries.telegram.domain.models.Button
import org.danceofvalkyries.telegram.domain.models.TelegramMessageBody

class MessageFactoryImpl(
    private val formatter: TextFormatter
) : MessageFactory {

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
            type = TelegramMessageBody.Type.NOTIFICATION,
        )
    }

    override fun createFlashCardMessage(
        flashCard: FlashCard,
    ): TelegramMessageBody {
        val memorizedInfo = flashCard
            .memorizedInfo
            .getValue(formatter)
        val example = flashCard
            .example
            ?.getValue(formatter)
        val answer = flashCard
            .answer
            ?.getValue(formatter)

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

        val imageUrl = if (flashCard.imageUrl?.isSupportedByTelegram == false) {
            BLUE_SCREEN
        } else {
            flashCard.imageUrl
        }

        val recallActions = listOf(Button("Forgot  ❌", ""), Button("Recalled  ✅", ""))
        val dictionaryButtons = flashCard.onlineDictionaries
            .map {
                Button(
                    text = "Look it up",
                    url = it.getWordUrl(memorizedInfo)
                )
            }

        return TelegramMessageBody(
            text = body.toString(),
            nestedButtons = listOf(
                recallActions,
                dictionaryButtons
            ),
            imageUrl = imageUrl,
            type = TelegramMessageBody.Type.FLASH_CARD,
        )
    }
}