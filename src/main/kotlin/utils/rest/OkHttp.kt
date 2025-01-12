package org.danceofvalkyries.utils.rest

import com.google.gson.Gson
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.danceofvalkyries.json.ContentType
import org.danceofvalkyries.json.ContentTypes
import org.danceofvalkyries.json.Header

inline fun <reified T> Response.parse(gson: Gson): T {
    return gson.fromJson(body?.string().orEmpty(), T::class.java)
}

fun Request.request(client: OkHttpClient) = client.newCall(this).execute()

fun Request.Builder.post(body: String): Request.Builder {
    val applicationJson = ContentType(ContentTypes.ApplicationJson)
    return post(body.toRequestBody(applicationJson.value.toMediaType()))
}

fun Request.Builder.patch(body: String): Request.Builder {
    val applicationJson = ContentType(ContentTypes.ApplicationJson)
    return patch(body.toRequestBody(applicationJson.value.toMediaType()))
}

fun Request.Builder.headers(headers: List<Header>): Request.Builder {
    val headersBuilder = Headers.Builder()
    headers.forEach { headersBuilder.add(it.name, it.value) }
    return headers(headersBuilder.build())
}