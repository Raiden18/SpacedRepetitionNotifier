package integrations.testdata.english.vocabulary

import org.danceofvalkyries.app.data.notion.databases.NotionDataBase
import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard
import utils.NotionDataBaseFake

class EnglishVocabularyDataBaseLocalDbFake : NotionDataBase {

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

    override val id: String = ID

    override val name: String = NAME

    override fun iterate(): Sequence<NotionPageFlashCard> {
        return defaultNotionDataBaseFake.iterate()
    }

    override fun add(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard {
        return defaultNotionDataBaseFake.add(notionPageFlashCard)
    }

    override fun getPageBy(pageId: String): NotionPageFlashCard {
        return iterate().first { it.id == pageId }
    }

    override fun clear() {
        defaultNotionDataBaseFake.clear()
    }

    override fun delete(pageId: String) {
        defaultNotionDataBaseFake.delete(pageId)
    }
}