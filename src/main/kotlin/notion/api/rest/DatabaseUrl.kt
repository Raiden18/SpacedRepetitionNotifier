package org.danceofvalkyries.notion.api.rest

import okhttp3.HttpUrl

data class DatabaseUrl(
    private val databaseId: String
) {

    private val notionUrl = HttpUrl.Builder()
        .scheme("https")
        .host("api.notion.com")
        .addPathSegment("v1")
        .build()

    fun dataBases(): HttpUrl {
        return notionUrl.newBuilder()
            .addPathSegment("databases")
            .addPathSegment(databaseId)
            .build()
    }

    fun databasesQuery(): HttpUrl {
        return dataBases().newBuilder()
            .addPathSegment("query")
            .build()
    }
}