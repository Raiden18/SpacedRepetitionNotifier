package org.danceofvalkyries.json

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

fun String.toRequestBody(): RequestBody {
    val applicationJson = ContentTypeApplicationJson()
    return toRequestBody(applicationJson.value.toMediaType())
}

inline fun <reified T> Response.parse(gson: Gson): T {
    return gson.fromJson(body?.string().orEmpty(), T::class.java)
}