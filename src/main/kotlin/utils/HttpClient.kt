package org.danceofvalkyries.utils

import com.google.gson.Gson
import org.danceofvalkyries.utils.rest.Header

interface HttpClient {

    fun get(url: String, headers: List<Header>): Response
    fun post(url: String, body: String, headers: List<Header>): Response

    data class Response(
        val requestUrl: String,
        val responseBody: String?
    )
}

inline fun <reified T : Any> HttpClient.Response.parse(gson: Gson): T {
    return gson.fromJson(responseBody, T::class.java)
}