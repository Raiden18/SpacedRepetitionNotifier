package integrations.testdata

import org.danceofvalkyries.app.data.notion.databases.NotionDataBase
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.app.data.telegram.message.ConstantTelegramMessageButton
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import utils.NotionDataBaseFake
import utils.fakes.telegram.TelegramMessageFake

class GreekLettersAndSoundsDataBaseRestfulFake : NotionDataBase {

    companion object {
        const val ID = "greek_letters_and_sounds_data_base"
        const val NAME = "Greek Letter And Sounds"
    }

    private val defaultNotionDataBaseFake = NotionDataBaseFake(
        id = ID,
        name = NAME,
    )

    val restfulGreekLetter1FlashCard = RestfulGreekLetter1FlashCard(ID)
    val restfulGreekLetter2FlashCard = RestfulGreekLetter2FlashCard(ID)

    override val id: String
        get() = defaultNotionDataBaseFake.id

    override val name: String
        get() = defaultNotionDataBaseFake.name

    fun createTelegramNotification(id: Long): TelegramMessageFake {
        return TelegramMessageFake.createTelegramNotification(
            messageId = id,
            numberToRevise = 2,
            tableName = NAME,
            dbId = ID
        )
    }

    override fun iterate(): Sequence<NotionPageFlashCard> {
        return sequenceOf(restfulGreekLetter1FlashCard, restfulGreekLetter2FlashCard)
    }

    override fun getPageBy(pageId: String): NotionPageFlashCard {
        return defaultNotionDataBaseFake.getPageBy(pageId)
    }

    override fun add(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard {
        error("Must not be supported")
    }

    override fun clear() {
        error("Must not be supported")
    }

    override fun delete(pageId: String) {
        error("Must not be supported")
    }
}