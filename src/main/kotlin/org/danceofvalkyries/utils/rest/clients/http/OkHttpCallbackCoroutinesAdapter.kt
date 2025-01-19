package org.danceofvalkyries.utils.rest.clients.http

import kotlinx.coroutines.CancellableContinuation
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.internal.closeQuietly
import java.io.IOException
import kotlin.coroutines.resumeWithException

class OkHttpCallbackCoroutinesAdapter(
    private val continuation: CancellableContinuation<Response>
) : Callback {

    override fun onFailure(call: Call, e: IOException) {
        continuation.resumeWithException(e)
    }

    override fun onResponse(call: Call, response: Response) {
        if (response.isSuccessful) {
            continuation.resume(response) { cause, _, _ ->
                response.closeQuietly()
            }
        } else {
            continuation.resumeWithException(IOException("HTTP error ${response.code}"))
        }
    }
}