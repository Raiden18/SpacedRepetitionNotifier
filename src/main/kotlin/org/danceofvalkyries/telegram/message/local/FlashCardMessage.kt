package org.danceofvalkyries.telegram.message.local

import org.danceofvalkyries.dictionary.OnlineDictionary
import org.danceofvalkyries.job.telegram_listener.ButtonAction
import org.danceofvalkyries.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.telegram.message.ConstantTelegramMessageButton
import org.danceofvalkyries.telegram.message.TelegramMessage
import org.danceofvalkyries.telegram.message.local.translator.TelegramTextTranslator
import org.danceofvalkyries.utils.resources.StringResources

class FlashCardMessage(
    private val flashCard: NotionPageFlashCard,
    private val stringResources: StringResources,
    private val onlineDictionaries: List<OnlineDictionary>
) : LocalTelegramMessage() {

    override val type: String
        get() = "FLASH_CARD"


    override fun getText(): String {
        val translator = TelegramTextTranslator()
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

    override fun getNestedButtons(): List<List<TelegramMessage.Button>> {
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

    override fun getImageUrl(): String? {
        return flashCard.coverUrl
    }
}