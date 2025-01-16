package org.danceofvalkyries.app.data.dictionary

import okhttp3.HttpUrl.Companion.toHttpUrl

data class ConstantOnlineDictionary(
    private val url: String
) : OnlineDictionary {

    override fun getUrlFor(word: String): String {
        return url.toHttpUrl()
            .newBuilder()
            .addPathSegment(word)
            .build()
            .toString()
    }
}
