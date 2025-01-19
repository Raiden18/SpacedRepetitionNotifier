package utils.fakes.httpclient

import org.danceofvalkyries.utils.rest.Header
import org.danceofvalkyries.utils.rest.clients.http.HttpClient

class HttpClientFake(
    private val postResponse: HttpClient.Response
) : HttpClient {

    var postRequest = mutableListOf<Request>()

    override fun get(url: String, headers: List<Header>): HttpClient.Response {
        TODO("Not yet implemented")
    }

    override fun post(url: String, body: String, headers: List<Header>): HttpClient.Response {
        postRequest.add(Request(url, body))
        return postResponse
    }

    override suspend fun patch(url: String, body: String, headers: List<Header>): HttpClient.Response {
        TODO("Not yet implemented")
    }

    fun assertThat(): Matcher {
        return Matcher()
    }

    data class Request(
        val url: String,
        val body: String
    )

    inner class Matcher {

        fun postWasRequested(request: Request) {
            if (postRequest.contains(request).not()) {
                val errorMessage = StringBuilder()
                    .appendLine("Was not requested!")
                    .appendLine("Request: $request")
                    .appendLine("List: $postRequest")
                    .toString()
                error(errorMessage)
            }
        }
    }
}