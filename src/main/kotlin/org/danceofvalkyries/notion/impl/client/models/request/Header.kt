package org.danceofvalkyries.notion.impl.client.models.request

import org.danceofvalkyries.utils.rest.Header

fun NotionApiVersionHeader(version: String): Header {
    return Header(
        name = "Notion-Version",
        value = version,
    )
}
