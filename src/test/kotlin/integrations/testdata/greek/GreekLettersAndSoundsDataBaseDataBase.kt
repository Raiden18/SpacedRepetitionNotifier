package integrations.testdata.greek

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.danceofvalkyries.notion.databases.NotionDataBase
import org.danceofvalkyries.notion.pages.NotionPageFlashCard
import utils.NotionDataBaseFake
import utils.fakes.telegram.TelegramMessageFake

class GreekLettersAndSoundsDataBaseDataBase : NotionDataBase {

    companion object {
        const val ID = "greek_letters_and_sounds_data_base"
        const val NAME = "Greek Letter And Sounds"
    }

    val restfulGreekLetter1FlashCard = RestfulGreekLetter1FlashCard(ID)
    val restfulGreekLetter2FlashCard = RestfulGreekLetter2FlashCard(ID)

    private val defaultNotionDataBaseFake = NotionDataBaseFake(
        id = ID,
        name = NAME,
        pages = listOf(restfulGreekLetter1FlashCard, restfulGreekLetter2FlashCard)
    )

    override fun getId(): String {
        return defaultNotionDataBaseFake.getId()
    }

    override fun getName(): String {
        return  defaultNotionDataBaseFake.getName()
    }

    fun createTelegramNotification(id: Long): TelegramMessageFake {
        return TelegramMessageFake.createTelegramNotification(
            messageId = id,
            numberToRevise = 2,
            tableName = NAME,
            dbId = ID
        )
    }

    override fun iterate(): Flow<NotionPageFlashCard> {
        return flowOf(restfulGreekLetter1FlashCard, restfulGreekLetter2FlashCard)
    }

    override suspend fun getPageBy(pageId: String): NotionPageFlashCard {
        return defaultNotionDataBaseFake.getPageBy(pageId)
    }

    override suspend fun add(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard {
        error("Must not be supported")
    }

    override fun clear() {
        error("Must not be supported")
    }

    override fun delete(pageId: String) {
        error("Must not be supported")
    }
}