package utils

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import org.danceofvalkyries.app.data.telegram.chat.restful.KtorWebServer

class KtorWebServerFake(
    private val gson: Gson
): KtorWebServer {

    private val channel = MutableStateFlow<String?>(null)

    fun send(json: Map<String, Any>) {
        channel.value = gson.toJson(json)
    }

    override fun getWebHook(): Flow<String> {
        return channel.mapNotNull { it }
    }
}