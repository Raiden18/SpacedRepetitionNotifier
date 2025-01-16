package org.danceofvalkyries.app.data.dictionary.constant

import okhttp3.HttpUrl.Companion.toHttpUrl
import org.danceofvalkyries.app.data.dictionary.OnlineDictionary

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
