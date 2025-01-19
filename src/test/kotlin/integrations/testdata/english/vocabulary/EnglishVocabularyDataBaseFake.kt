package integrations.testdata.english.vocabulary

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.danceofvalkyries.notion.databases.NotionDataBase
import org.danceofvalkyries.notion.pages.NotionPageFlashCard
import utils.NotionDataBaseFake

class EnglishVocabularyDataBaseFake : NotionDataBase {

    companion object {
        val ID = "english_vocabulary_db_1"
        val NAME = "English Vocabulary"
    }

    val wine = WineEnglishVocabulary(ID)
    val dota2 = Dota2EnglishVocabulary(ID)

    private val defaultNotionDataBaseFake = NotionDataBaseFake(
        id = ID,
        name = NAME,
        pages = listOf(wine, dota2)
    )


    override fun getId(): String {
        return ID
    }

    override suspend fun getName(): String {
        return NAME
    }

    override suspend fun iterate(): Flow<NotionPageFlashCard> {
        return defaultNotionDataBaseFake.iterate()
    }

    override suspend fun add(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard {
        return defaultNotionDataBaseFake.add(notionPageFlashCard)
    }

    override suspend fun getPageBy(pageId: String): NotionPageFlashCard {
        return iterate().first { it.id == pageId }
    }

    override suspend fun clear() {
        defaultNotionDataBaseFake.clear()
    }

    override suspend fun delete(pageId: String) {
        defaultNotionDataBaseFake.delete(pageId)
    }
}