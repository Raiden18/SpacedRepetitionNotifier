package org.danceofvalkyries.app.data.telegram.users.user

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
import okhttp3.OkHttpClient
import org.danceofvalkyries.app.data.telegram.message.TelegramMessage
import org.danceofvalkyries.app.data.telegram.message.restful.RestfulTelegramUpdateMessageButtonCallback
import org.danceofvalkyries.app.data.telegram.users.HumanUser
import org.danceofvalkyries.app.data.telegram.jsonobjects.TelegramChatUrls
import org.danceofvalkyries.app.data.telegram.jsons.UpdateResponseData

class TelegramHumanUserImpl(
    private val gson: Gson,
    private val httpClient: OkHttpClient,
    private val apiKey: String
) : HumanUser {

    private val telegramChatUrls = TelegramChatUrls(apiKey)

    override fun getActions(): Flow<TelegramMessage.Button.Callback> {
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
                RestfulTelegramUpdateMessageButtonCallback(
                    id = it.callbackQueryData.id,
                    action = TelegramMessage.Button.Action.CallBackData(it.callbackQueryData.data!!),
                    gson = gson,
                    client = httpClient,
                    telegramChatUrls = telegramChatUrls,
                )
            }

    }
}