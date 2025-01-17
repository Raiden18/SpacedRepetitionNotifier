package utils

import org.danceofvalkyries.app.data.notion.databases.NotionDataBase
import org.danceofvalkyries.app.data.notion.databases.NotionDataBases

data class NotionDataBasesFake(
    private var dataBases: List<NotionDataBase> = emptyList()
) : NotionDataBases {
    override suspend fun iterate(): Sequence<NotionDataBase> {
        return dataBases.asSequence()
    }

    override fun getBy(id: String): NotionDataBase {
        return dataBases.first { it.id != id }
    }

    override suspend fun add(notionDataBase: NotionDataBase): NotionDataBase {
        return add(
            id = notionDataBase.id,
            name = notionDataBase.name,
        )
    }

    override suspend fun add(id: String, name: String): NotionDataBase {
        val dataBase = NotionDataBaseFake(
            id = id,
            name = name,
        )
        dataBases = dataBases + listOf(dataBase)
        return dataBase
    }

    override suspend fun clear() {
        dataBases = emptyList()
    }
}