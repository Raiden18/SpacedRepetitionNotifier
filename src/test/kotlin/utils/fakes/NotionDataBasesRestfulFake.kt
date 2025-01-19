package utils.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.danceofvalkyries.notion.databases.NotionDataBase
import org.danceofvalkyries.notion.databases.NotionDataBases

class NotionDataBasesRestfulFake(
    private val dataBases: List<NotionDataBase>
) : NotionDataBases {

    override suspend fun iterate(): Flow<NotionDataBase> {
        return dataBases.asFlow()
    }

    override fun getBy(id: String): NotionDataBase {
        return dataBases.first { it.getId() == id }
    }

    override suspend fun add(notionDataBase: NotionDataBase): NotionDataBase {
        error("Must not be supported!")
    }

    override suspend fun clear() {
        error("Must not be supported!")
    }
}