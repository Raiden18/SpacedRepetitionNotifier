package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.app.data.dictionary.OnlineDictionary
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.telegram.api.models.TelegramButton
import org.danceofvalkyries.telegram.api.models.TelegramImageUrl
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.telegram.api.models.TelegramText

data class FlashCardMessage(
    override val id: Long,
    private val flashCard: NotionPageFlashCard,
    private val onlineDictionaries: List<OnlineDictionary>,
) : Message {

    constructor(
        flashCard: NotionPageFlashCard,
        onlineDictionaries: List<OnlineDictionary>,
    ) : this(-1, flashCard, onlineDictionaries)

    override val type: String = "FLASH_CARD"

    override suspend fun asTelegramBody(): TelegramMessageBody {
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

        val recallActions = listOf(
            TelegramButton("Forgot  ❌", TelegramButton.Action.CallBackData(ButtonAction.Forgotten(flashCard.id).rawValue)),
            TelegramButton("Recalled  ✅", TelegramButton.Action.CallBackData(ButtonAction.Recalled(flashCard.id).rawValue))
        )

        val dictionaryTelegramButtons = onlineDictionaries
            .map {
                TelegramButton(
                    text = "Look it up",
                    action = TelegramButton.Action.Url(it.getUrlFor(memorizedInfo)),
                )
            }

        return TelegramMessageBody(
            text = TelegramText(body.toString()),
            nestedButtons = listOf(
                recallActions,
                dictionaryTelegramButtons
            ),
            imageUrl = flashCard.coverUrl?.let(::TelegramImageUrl),
        )
    }
}