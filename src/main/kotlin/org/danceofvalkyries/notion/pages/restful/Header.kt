package org.danceofvalkyries.notion.pages.restful

import org.danceofvalkyries.utils.rest.Header

fun NotionApiVersionHeader(version: String): Header {
    return Header(
        name = "Notion-Version",
        value = version,
    )
}
