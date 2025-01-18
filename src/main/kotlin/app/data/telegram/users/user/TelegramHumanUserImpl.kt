package org.danceofvalkyries.app.data.telegram.users.user

import org.danceofvalkyries.app.data.notion.databases.NotionDataBases
import org.danceofvalkyries.app.data.telegram.users.HumanUser
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.KnowLevels
import org.danceofvalkyries.notion.api.models.NotionId

class TelegramHumanUserImpl(
    private val localDbNotionDataBases: NotionDataBases,
    private val restfulNotionDataBases: NotionDataBases,
) : HumanUser {

    override suspend fun forget(flashCardId: String) {

    }

    override suspend fun recall(flashCardId: String) {

    }
}