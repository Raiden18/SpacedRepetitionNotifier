package org.danceofvalkyries.utils.rest.clients.http

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OkHttpCallbackCoroutinesAdapter(
    private val continuation: Continuation<Response>
) : Callback {

    override fun onFailure(call: Call, e: IOException) {
        continuation.resumeWithException(e)
    }

    override fun onResponse(call: Call, response: Response) {
        continuation.resume(response)
    }
}