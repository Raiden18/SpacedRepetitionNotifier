package utils.fakes.telegram

import org.danceofvalkyries.app.data.telegram.message.ConstantTelegramMessageButton
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage

data class TelegramMessageFake(
    override val id: Long,
    override val text: String,
    override val imageUrl: String?,
    override val nestedButtons: List<List<TelegramMessage.Button>>
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
    }
}