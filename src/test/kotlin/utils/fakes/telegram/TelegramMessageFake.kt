package utils.fakes.telegram

import org.danceofvalkyries.telegram.message.ConstantTelegramMessageButton
import org.danceofvalkyries.telegram.message.TelegramMessage

data class TelegramMessageFake(
    private val id: Long,
    private val text: String,
    private val imageUrl: String?,
    private val nestedButtons: List<List<TelegramMessage.Button>>
) : TelegramMessage {

    companion object {
        fun createTelegramNotification(
            messageId: Long,
            numberToRevise: Int,
            tableName: String,
            dbId: String
        ): TelegramMessageFake {
            return TelegramMessageFake(
                id = messageId,
                text = """You have $numberToRevise flashcards to revise 🧠""",
                imageUrl = null,
                nestedButtons = listOf(
                    listOf(
                        ConstantTelegramMessageButton(
                            text = "$tableName: $numberToRevise",
                            action = TelegramMessage.Button.Action.CallBackData("dbId=$dbId")
                        )
                    ),
                )
            )
        }

        fun createAllDone(messageId: Long): TelegramMessageFake {
            return TelegramMessageFake(
                id = messageId,
                text = """Good Job! 😎 Everything is revised! ✅""",
                imageUrl = null,
                nestedButtons = emptyList(),
            )
        }

        fun createEnglishVocabularyFlashCard(
            messageId: Long,
            text: String,
            example: String,
            answer: String,
            flashCardId: String,
            dictionaryUrl: String,
            imageUrl: String?,
        ): TelegramMessageFake {
            return TelegramMessageFake(
                id = messageId,
                text = """
                    *${text}*
                    
                    _${example}_
                    
                    ||${answer}||
                    
                    Choose:
                """.trimIndent(),
                imageUrl = imageUrl,
                nestedButtons = listOf(
                    listOf(
                        ConstantTelegramMessageButton(
                            "Forgot  ❌",
                            TelegramMessage.Button.Action.CallBackData("forgottenFlashCardId=${flashCardId}")
                        ),
                        ConstantTelegramMessageButton(
                            "Recalled  ✅",
                            TelegramMessage.Button.Action.CallBackData("recalledFlashCardId=${flashCardId}")
                        )
                    ),
                    listOf(
                        ConstantTelegramMessageButton(
                            text = "Look it up",
                            action = TelegramMessage.Button.Action.Url(dictionaryUrl),
                        )
                    ),
                ),
            )
        }
    }

    override fun getId(): Long {
        return id
    }

    override fun getText(): String {
        return text
    }

    override fun getImageUrl(): String? {
        return imageUrl
    }

    override fun getNestedButtons(): List<List<TelegramMessage.Button>> {
        return nestedButtons
    }
}