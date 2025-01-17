package org.danceofvalkyries.app.data.notion.databases

import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard

interface NotionDataBase {
    val id: String
    val name: String

    fun iterate(): Sequence<NotionPageFlashCard>

    fun add(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard
    fun getPageBy(pageId: String): NotionPageFlashCard

    fun clear()
    fun delete(pageId: String)
}