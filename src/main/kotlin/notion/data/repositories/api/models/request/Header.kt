package org.danceofvalkyries.notion.data.repositories.api.models.request

import org.danceofvalkyries.json.Header

fun NotionApiVersionHeader(version: String): Header {
    return Header(
        name = "Notion-Version",
        value = version,
    )
}
