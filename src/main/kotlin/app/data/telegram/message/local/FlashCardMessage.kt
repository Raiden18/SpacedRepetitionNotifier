package org.danceofvalkyries.app.data.telegram.message.local

import org.danceofvalkyries.app.data.dictionary.OnlineDictionary
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.app.data.telegram.message.ConstantTelegramMessageButton
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.message.local.translator.TextTranslator
import org.danceofvalkyries.app.domain.message.ButtonAction
import org.danceofvalkyries.utils.resources.StringResources

class FlashCardMessage(
    private val flashCard: NotionPageFlashCard,
    private val translator: TextTranslator,
    private val stringResources: StringResources,
    private val onlineDictionaries: List<OnlineDictionary>
) : LocalTelegramMessage() {

    override val type: String
        get() = "FLASH_CARD"


    override val text: String
        get() {
            val memorizedInfo = translator.encode(flashCard.name)!!
            val example = translator.encode(flashCard.example)
            val answer = translator.encode(flashCard.explanation)

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
                .append(stringResources.choose())

            return body.toString()
        }

    override val nestedButtons: List<List<TelegramMessage.Button>>
        get() {
            val recallActions = listOf(
                ConstantTelegramMessageButton(
                    "${stringResources.forgot()}  ❌",
                    TelegramMessage.Button.Action.CallBackData(ButtonAction.Forgotten(flashCard.id).rawValue)
                ),
                ConstantTelegramMessageButton(
                    "${stringResources.recalled()}  ✅",
                    TelegramMessage.Button.Action.CallBackData(ButtonAction.Recalled(flashCard.id).rawValue)
                )
            )

            val dictionaryTelegramButtons = onlineDictionaries
                .map {
                    ConstantTelegramMessageButton(
                        text = stringResources.lookUp(),
                        action = TelegramMessage.Button.Action.Url(it.getUrlFor(flashCard.name)),
                    )
                }

            return listOf(
                recallActions,
                dictionaryTelegramButtons
            )
        }

    override val imageUrl: String?
        get() = flashCard.coverUrl
}