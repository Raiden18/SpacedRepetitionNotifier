package job.data.telegram.chat.restful

import com.google.gson.Gson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecWhenContainerScope
import org.danceofvalkyries.telegram.chat.restful.TelegramChatHttpClientDecorator
import org.danceofvalkyries.telegram.chat.restful.TelegramChatHttpClientDecorator.Companion.BLUE_SCREEN
import org.danceofvalkyries.telegram.jsonobjects.MessageData
import org.danceofvalkyries.utils.rest.clients.http.HttpClient
import utils.fakes.httpclient.HttpClientFake

class TelegramChatHttpClientDecoratorTest : BehaviorSpec() {

    private val gson = Gson()
    private val sendImageUrl = "https://api.telegram.org/botAPI_KEY/sendPhoto"
    private lateinit var fakeHttpClient: HttpClientFake

    private lateinit var telegramChatHttpClientDecorator: TelegramChatHttpClientDecorator

    init {

        Given("Bad Request: wrong remote file identifier specified: Wrong padding length") {
            beforeTest {
                telegramChatHttpClientDecorator = createClientDecorator("/jsons/telegram/send_image/bad_request/response/wrong_padding_length_response.json")
            }

            When("Makes post request"){
                beforeTest {
                    makePostRequest("/jsons/telegram/send_image/bad_request/requests/wrong_padding_length_request.json")
                }
                thenShouldResentImageWithBlueScreen()
            }
        }

        Given("Bad Request: wrong remote file identifier specified: Wrong character in the string") {
            beforeTest {
                telegramChatHttpClientDecorator = createClientDecorator("/jsons/telegram/send_image/bad_request/response/wrong_character_in_the_string_response.json")
            }

            When("Makes post Request") {
                beforeTest {
                    makePostRequest("/jsons/telegram/send_image/bad_request/requests/wrong_character_in_the_string_request.json")
                }
                thenShouldResentImageWithBlueScreen()
            }
        }

        Given("Bad Request: failed to get HTTP URL content") {
            beforeTest {
                telegramChatHttpClientDecorator = createClientDecorator("/jsons/telegram/send_image/bad_request/response/failed_to_get_http_url_content_response.json")
            }

            When("Makes Post Request") {
                beforeTest {
                    makePostRequest("/jsons/telegram/send_image/bad_request/requests/failed_to_get_url_content_request.json")
                }
                thenShouldResentImageWithBlueScreen()
            }
        }

        Given("Wrong file identifier/HTTP URL specified") {
            beforeTest {
                telegramChatHttpClientDecorator = createClientDecorator("/jsons/telegram/send_image/bad_request/response/wrong_identifier_response.json")
            }

            When("Makes post request") {
                beforeTest {
                    makePostRequest("/jsons/telegram/send_image/bad_request/requests/inaccessible_photo_url_request.json")
                }
                thenShouldResentImageWithBlueScreen()
            }
        }
    }


    private suspend fun makePostRequest(jsonName: String) {
        telegramChatHttpClientDecorator.post(
            url = sendImageUrl,
            body = javaClass.getResource(jsonName)!!.readText(),
            headers = emptyList()
        )
    }


    private suspend fun BehaviorSpecWhenContainerScope.thenShouldResentImageWithBlueScreen() {
        Then("Should send new request with fixed image") {
            fakeHttpClient.postRequest
                .map { it.body }
                .map { gson.fromJson(it, MessageData::class.java) }
                .firstOrNull { it.photo == BLUE_SCREEN } ?: error("No Post request with fixed image")
        }
    }

    private fun createClientDecorator(jsonName: String): TelegramChatHttpClientDecorator {
        fakeHttpClient = HttpClientFake(
            postResponse = HttpClient.Response(
                requestUrl = sendImageUrl,
                responseBody = javaClass.getResource(jsonName)!!.readText(),
                responseCode = 400,
            ),
        )
        return TelegramChatHttpClientDecorator(
            fakeHttpClient,
            Gson()
        )
    }
}