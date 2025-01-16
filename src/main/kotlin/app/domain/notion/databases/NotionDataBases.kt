package app.domain.notion.databases

interface NotionDataBases {
    suspend fun iterate(): Sequence<NotionDataBase>
    suspend fun add(
        id: String,
        name: String = "",
    ): NotionDataBase

    suspend fun clear()
}