package utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import org.danceofvalkyries.notion.databases.NotionDataBase
import org.danceofvalkyries.notion.databases.NotionDataBases

data class SqlLiteNotionDataBasesFake(
    private var dataBases: List<NotionDataBase> = emptyList()
) : NotionDataBases {
    override suspend fun iterate(): Flow<NotionDataBase> {
        return dataBases.asFlow()
    }

    override fun getBy(id: String): NotionDataBase {
        return dataBases.first { it.getId() == id }
    }

    override suspend fun add(notionDataBase: NotionDataBase): NotionDataBase {
        val dataBase = NotionDataBaseFake(
            id = notionDataBase.getId(),
            name = notionDataBase.getName(),
            pages = notionDataBase.iterate().toList()
        )
        dataBases = dataBases + listOf(dataBase)
        return dataBase
    }

    override suspend fun clear() {
        dataBases = emptyList()
    }
}