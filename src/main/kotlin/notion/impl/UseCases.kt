package org.danceofvalkyries.notion.impl

import org.danceofvalkyries.notion.api.GetAllPagesFromNotionDataBase
import org.danceofvalkyries.notion.api.GetDataBaseFromNotion
import org.danceofvalkyries.notion.api.GetPageFromNotion
import org.danceofvalkyries.notion.api.UpdatePageInNotion
import org.danceofvalkyries.notion.impl.database.NotionDataBaseApi
import org.danceofvalkyries.notion.impl.flashcardpage.FlashCardNotionPageApi

fun GetDataBaseFromNotion(notionDataBaseApi: NotionDataBaseApi): GetDataBaseFromNotion {
    return GetDataBaseFromNotion {
        return@GetDataBaseFromNotion notionDataBaseApi.getFromNotion(it)
    }
}

fun GetPageFromNotion(flashCardNotionPageApi: FlashCardNotionPageApi): GetPageFromNotion {
    return GetPageFromNotion {
        return@GetPageFromNotion flashCardNotionPageApi.getFromNotion(it)
    }
}

fun GetAllPagesFromNotionDataBase(
    flashCardNotionPageApi: FlashCardNotionPageApi
): GetAllPagesFromNotionDataBase {
    return GetAllPagesFromNotionDataBase {
        return@GetAllPagesFromNotionDataBase flashCardNotionPageApi.getAllFromDb(it)
    }
}

fun UpdatePageInNotion(
    flashCardNotionPageApi: FlashCardNotionPageApi
): UpdatePageInNotion {
    return UpdatePageInNotion { flashCardNotionPageApi.updateInNotion(it) }
}