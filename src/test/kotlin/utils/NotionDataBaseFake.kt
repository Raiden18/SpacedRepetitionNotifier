package utils

import app.domain.notion.databases.NotionDataBase

data class NotionDataBaseFake(
    override val id: String,
    override val name: String
): NotionDataBase