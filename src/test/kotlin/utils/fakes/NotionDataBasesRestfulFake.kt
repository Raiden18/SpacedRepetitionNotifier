package utils.fakes

import org.danceofvalkyries.app.data.notion.databases.NotionDataBase
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases

class NotionDataBasesRestfulFake(
    private val dataBases: List<NotionDataBase>
) : NotionDataBases {

    override suspend fun iterate(): Sequence<NotionDataBase> {
        return dataBases.asSequence()
    }

    override fun getBy(id: String): NotionDataBase {
        return dataBases.first { it.id == id }
    }

    override suspend fun add(notionDataBase: NotionDataBase): NotionDataBase {
        error("Must not be supported!")
    }

    override suspend fun clear() {
        error("Must not be supported!")
    }
}