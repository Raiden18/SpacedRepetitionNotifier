package org.danceofvalkyries.utils.rest.clients.sever

import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class KtorSeverClient : SeverClient {

    override fun getWebHook(): Flow<String> {
        return channelFlow {
            embeddedServer(Netty, port = 8080) {
                routing {
                    post("/webhook") {
                        send(call.receiveText())
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }.start(wait = true)
        }
    }
}