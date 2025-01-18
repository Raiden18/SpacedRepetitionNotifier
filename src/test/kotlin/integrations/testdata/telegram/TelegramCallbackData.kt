package integrations.testdata.telegram

import org.danceofvalkyries.app.data.telegram.message.TelegramMessage

data class TelegramCallbackData(
    override val id: String,
    override val messageId: Long,
    override val action: TelegramMessage.Button.Action,
): TelegramMessage.Button.Callback {

    override suspend fun answer() = Unit
}