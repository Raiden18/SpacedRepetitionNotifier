package org.danceofvalkyries.telegram.message.local

import org.danceofvalkyries.dictionary.OnlineDictionary
import org.danceofvalkyries.job.telegram_listener.ButtonAction
import org.danceofvalkyries.telegram.message.ConstantTelegramMessageButton
import org.danceofvalkyries.telegram.message.TelegramMessage
import org.danceofvalkyries.telegram.message.local.translator.TelegramTextTranslator
import org.danceofvalkyries.utils.resources.StringResources

class FlashCardMessage(
    private val notionFlashCardId: String,
    private val name: String,
    private val example: String?,
    private val answer: String?,
    private val coverUrl: String?,
    private val stringResources: StringResources,
    private val onlineDictionaries: List<OnlineDictionary>,
) : LocalTelegramMessage() {

    override val type: String
        get() = "FLASH_CARD"


    override fun getText(): String {
        val translator = TelegramTextTranslator()
        val memorizedInfo = translator.encode(name)!!
        val example = translator.encode(example)
        val answer = translator.encode(answer)

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

    override suspend fun getNestedButtons(): List<List<TelegramMessage.Button>> {
        val recallActions = listOf(
            ConstantTelegramMessageButton(
                "${stringResources.forgot()}  ❌",
                TelegramMessage.Button.Action.CallBackData(ButtonAction.Forgotten(notionFlashCardId).rawValue)
            ),
            ConstantTelegramMessageButton(
                "${stringResources.recalled()}  ✅",
                TelegramMessage.Button.Action.CallBackData(ButtonAction.Recalled(notionFlashCardId).rawValue)
            )
        )

        val dictionaryTelegramButtons = onlineDictionaries
            .map {
                ConstantTelegramMessageButton(
                    text = stringResources.lookUp(),
                    action = TelegramMessage.Button.Action.Url(it.getUrlFor(name)),
                )
            }

        return listOf(
            recallActions,
            dictionaryTelegramButtons
        )
    }

    override fun getImageUrl(): String? {
        return coverUrl
    }
}