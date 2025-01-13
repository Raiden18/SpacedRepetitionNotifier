package testutils

import org.danceofvalkyries.app.domain.message.MessageFactory
import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.notion.api.models.NotionDataBase
import org.danceofvalkyries.telegram.api.models.TelegramMessageBody

class MessageFactoryFake(
    var notificationBody: TelegramMessageBody = TelegramMessageBody.EMPTY,
    var flashCardBody: TelegramMessageBody = TelegramMessageBody.EMPTY,
) : MessageFactory {

    override fun createDone(): TelegramMessageBody {
        TODO()
    }

    override fun createNotification(
        flashCards: List<FlashCard>,
        notionDataBases: List<NotionDataBase>
    ): TelegramMessageBody = notificationBody

    override fun createFlashCardMessage(flashCard: FlashCard): TelegramMessageBody {
        return flashCardBody
    }
}