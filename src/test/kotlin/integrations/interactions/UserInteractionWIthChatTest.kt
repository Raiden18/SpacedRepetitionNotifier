package integrations.interactions

import com.google.gson.Gson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import org.danceofvalkyries.app.apps.buttonslistener.TelegramButtonListenerApp
import org.danceofvalkyries.app.apps.buttonslistener.presentation.controller.SpaceRepetitionSession
import org.danceofvalkyries.app.data.telegram.chat.restful.RestfulTelegramChat
import org.danceofvalkyries.app.data.telegram.users.bot.TelegramBotUserImpl
import org.danceofvalkyries.app.data.telegram.users.user.TelegramHumanUserImpl
import org.danceofvalkyries.utils.DispatchersImpl
import org.danceofvalkyries.utils.rest.jsonObject
import utils.*

class UserInteractionWIthChatTest : BehaviorSpec() {

    private lateinit var spaceRepetitionSession: SpaceRepetitionSession
    private lateinit var telegramButtonListenerApp: TelegramButtonListenerApp
    private lateinit var ktorWebServer: KtorWebServerFake
    private lateinit var httpClientFake: HttpClientFake
    private val CHAT_ID = "123"
    private val API_KEY = "API_KEY"

    init {

        beforeTest {
            val gson = Gson()
            ktorWebServer = KtorWebServerFake(gson)
            httpClientFake = HttpClientFake()
            val telegramChat = RestfulTelegramChat(
                apiKey = API_KEY,
                OkHttpClient(),
                Gson(),
                CHAT_ID,
                ktorWebServer,
                httpClientFake
            )
            val restfulNotionDataBases = NotionDataBasesFake()
            val localDbNotionDataBases = NotionDataBasesFake()
            val telegramMessagesType = TelegramMessagesTypeFake()
            val humanUser = TelegramHumanUserImpl(
                localDbNotionDataBases,
                restfulNotionDataBases,
            )
            val onlineDictionariesFake = OnlineDictionariesFake(
                emptyList()
            )
            val botUser = TelegramBotUserImpl(
                telegramChat,
                localDbNotionDataBases,
                telegramMessagesType,
                onlineDictionariesFake
            )
            val spaceRepetitionSession = SpaceRepetitionSession(humanUser, botUser)
            telegramButtonListenerApp = TelegramButtonListenerApp(
                DispatchersImpl(Dispatchers.Unconfined),
                spaceRepetitionSession,
                telegramChat
            )
        }

        Given("Listener App") {

            val messageId = 2868

            When("User types a message in chat") {
                beforeTest {
                    val responseFromTelegram = jsonObject {
                        "update_id" to 2
                        "message" to jsonObject {
                            "message_id" to messageId
                            "from" to jsonObject {
                                "id" to 3
                                "is_bot" to false
                                "first_name" to "Paul"
                                "username" to "DanceOfValkyries"
                                "language_code" to "en"
                            }
                            "chat" to jsonObject {
                                "id" to 4
                                "first_name" to "Paul"
                                "username" to "DanceOfValkyries"
                                "type" to "private"
                            }
                            "date" to 1737094191
                            "text" to "Q"
                        }
                    }

                    ktorWebServer.send(responseFromTelegram)
                }

                Then("Should delete that message on Telegram").config() {
                    telegramButtonListenerApp.run()
                    httpClientFake.urls shouldContain "https://api.telegram.org/bot$API_KEY/deleteMessage?chat_id=$CHAT_ID&message_id=$messageId"
                }
            }
        }
    }
}