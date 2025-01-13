package org.danceofvalkyries.notion.impl.restapi

import okhttp3.HttpUrl

class NotionUrls {

    private val notionUrl = HttpUrl.Builder()
        .scheme("https")
        .host("api.notion.com")
        .addPathSegment("v1")
        .build()

    fun dataBases(databaseId: String): HttpUrl {
        return notionUrl.newBuilder()
            .addPathSegment("databases")
            .addPathSegment(databaseId)
            .build()
    }

    fun databasesQuery(databaseId: String): HttpUrl {
        return dataBases(databaseId).newBuilder()
            .addPathSegment("query")
            .build()
    }

    fun pages(pageId: String): HttpUrl {
        return notionUrl.newBuilder()
            .addPathSegment("pages")
            .addPathSegment(pageId)
            .build()
    }
}