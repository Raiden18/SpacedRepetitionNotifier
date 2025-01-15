package org.danceofvalkyries.app.domain.message

import org.danceofvalkyries.dictionary.api.OnlineDictionary
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.telegram.api.models.TelegramButton
import org.danceofvalkyries.telegram.api.models.TelegramImageUrl
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody
import org.danceofvalkyries.telegram.api.models.TelegramText

data class FlashCardMessage(
    override val id: Long,
    private val flashCard: FlashCardNotionPage,
    private val onlineDictionaries: List<OnlineDictionary>,
) : Message {

    constructor(
        flashCard: FlashCardNotionPage,
        onlineDictionaries: List<OnlineDictionary>,
    ) : this(-1, flashCard, onlineDictionaries)

    override val type: String = "FLASH_CARD"

    override val telegramBody: TelegramMessageBody
        get() {
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
                        action = TelegramButton.Action.Url(it.getWordUrl(memorizedInfo)),
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