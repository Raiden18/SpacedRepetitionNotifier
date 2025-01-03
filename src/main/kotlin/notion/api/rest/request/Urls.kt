package org.danceofvalkyries.notion.api.rest.request

import okhttp3.HttpUrl

fun DbUrl(databaseId: String): HttpUrl {
    return HttpUrl.Builder()
        .scheme("https")
        .host("api.notion.com")
        .addPathSegment("v1")
        .addPathSegment("databases")
        .addPathSegment(databaseId)
        .build()
}

fun Query(
    httpUrl: HttpUrl
): HttpUrl {
    return httpUrl.newBuilder()
        .addPathSegment("query")
        .build()
}