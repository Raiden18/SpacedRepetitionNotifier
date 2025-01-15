package utils

import org.danceofvalkyries.app.domain.notion.NotionDataBase

data class NotionDataBaseFake(
    override val id: String,
    override val name: String
): NotionDataBase