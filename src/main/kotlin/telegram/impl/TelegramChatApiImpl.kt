package org.danceofvalkyries.telegram.impl

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import org.danceofvalkyries.telegram.api.TelegramChatApi
import org.danceofvalkyries.telegram.api.models.TelegramButton
import org.danceofvalkyries.telegram.api.models.TelegramUpdate
import org.danceofvalkyries.telegram.api.models.TelegramUpdateCallbackQuery
import org.danceofvalkyries.telegram.impl.models.UpdateResponseData

class TelegramChatApiImpl(
    private val gson: Gson,
) : TelegramChatApi {

    override suspend fun getUpdates(): Flow<TelegramUpdate> {
        return channelFlow {
            embeddedServer(Netty, port = 8080) {
                routing {
                    post("/webhook") {
                        send(call.receiveText())
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }.start(wait = true)
        }.map { gson.fromJson(it, UpdateResponseData::class.java) }
            .map {
                TelegramUpdate(
                    id = it.id,
                    telegramUpdateCallbackQuery = TelegramUpdateCallbackQuery(
                        id = it.callbackQueryData.id,
                        callback = TelegramButton.Action.CallBackData(it.callbackQueryData.data!!),
                    ),
                )
            }
    }
}