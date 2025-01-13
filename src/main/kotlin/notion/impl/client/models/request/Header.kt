package org.danceofvalkyries.notion.impl.restapi.models.request

import org.danceofvalkyries.json.Header

fun NotionApiVersionHeader(version: String): Header {
    return Header(
        name = "Notion-Version",
        value = version,
    )
}
