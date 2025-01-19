package org.danceofvalkyries.utils.rest.clients.http

import com.google.gson.Gson
import org.danceofvalkyries.utils.rest.Header

interface HttpClient {

    suspend fun get(url: String, headers: List<Header>): Response
    suspend fun post(url: String, body: String, headers: List<Header>): Response
    suspend fun patch(url: String, body: String, headers: List<Header>): Response

    fun releaseResources()

    data class Response(
        val requestUrl: String,
        val responseBody: String?,
        val responseCode: Int
    )
}

inline fun <reified T : Any> HttpClient.Response.parse(gson: Gson): T {
    return gson.fromJson(responseBody, T::class.java)
}