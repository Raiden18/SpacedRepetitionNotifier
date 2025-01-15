package org.danceofvalkyries.dictionary.api

import okhttp3.HttpUrl.Companion.toHttpUrl

data class OnlineDictionary(
    val url: String
) {
    fun getWordUrl(word: String?): String {
        if (word == null) return ""
        return url.toHttpUrl()
            .newBuilder()
            .addPathSegment(word)
            .build()
            .toString()
    }
}