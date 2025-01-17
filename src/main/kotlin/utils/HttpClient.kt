package org.danceofvalkyries.utils

interface HttpClient {
    fun get(url: String): Response

    data class Response(
        val requestUrl: String,
    )
}