package org.danceofvalkyries.notion.databases

import org.danceofvalkyries.notion.pages.NotionPageFlashCard

interface NotionDataBase {
    fun getId(): String
    fun getName(): String
    fun iterate(): Sequence<NotionPageFlashCard>
    fun add(notionPageFlashCard: NotionPageFlashCard): NotionPageFlashCard
    fun getPageBy(pageId: String): NotionPageFlashCard
    fun clear()
    fun delete(pageId: String)
}