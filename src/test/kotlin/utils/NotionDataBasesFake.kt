package utils

import app.domain.notion.databases.NotionDataBase
import app.domain.notion.databases.NotionDataBases

data class NotionDataBasesFake(
    private val dataBases: List<NotionDataBase>
) : NotionDataBases {
    override suspend fun iterate(): Sequence<NotionDataBase> {
        return dataBases.asSequence()
    }

    override suspend fun add(notionDataBase: NotionDataBase): NotionDataBase {
        TODO("Not yet implemented")
    }

    override suspend fun add(id: String, name: String): NotionDataBase {
        TODO("Not yet implemented")
    }

    override suspend fun clear() {
        TODO("Not yet implemented")
    }
}