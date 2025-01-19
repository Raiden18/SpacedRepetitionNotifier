package utils

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import org.danceofvalkyries.utils.rest.clients.sever.SeverClient

class SeverClientFake(
    private val gson: Gson
): SeverClient {

    private val channel = MutableStateFlow<String?>(null)

    fun send(json: Map<String, Any>) {
        channel.value = gson.toJson(json)
    }

    override fun getWebHook(): Flow<String> {
        return channel.mapNotNull { it }
    }
}