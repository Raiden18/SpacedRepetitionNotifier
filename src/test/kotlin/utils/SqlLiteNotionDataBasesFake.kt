package utils

import org.danceofvalkyries.app.data.notion.databases.NotionDataBase
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases

data class SqlLiteNotionDataBasesFake(
    private var dataBases: List<NotionDataBase> = emptyList()
) : NotionDataBases {
    override suspend fun iterate(): Sequence<NotionDataBase> {
        return dataBases.asSequence()
    }

    override fun getBy(id: String): NotionDataBase {
        return dataBases.first { it.id == id }
    }

    override suspend fun add(notionDataBase: NotionDataBase): NotionDataBase {
        val dataBase = NotionDataBaseFake(
            id = notionDataBase.id,
            name = notionDataBase.name,
            pages = notionDataBase.iterate().toMutableList()
        )
        dataBases = dataBases + listOf(dataBase)
        return dataBase
    }

    override suspend fun clear() {
        dataBases = emptyList()
    }
}