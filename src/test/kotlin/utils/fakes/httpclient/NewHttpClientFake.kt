package utils.fakes.httpclient

import org.danceofvalkyries.utils.HttpClient
import org.danceofvalkyries.utils.rest.Header

class NewHttpClientFake : HttpClient {

    private val mockedGetResponses = mutableListOf<HttpClient.Response>()
    private val mockedPostResponses = mutableListOf<RequestWithBody>()
    private val mockedPatchResponses = mutableListOf<RequestWithBody>()

    override fun get(url: String, headers: List<Header>): HttpClient.Response {
        val mockedResponse = mockedGetResponses.firstOrNull { it.requestUrl == url }
        if (mockedResponse == null) error("GET response for $url hasn't been mocked!")
        return mockedResponse
    }

    override fun post(url: String, body: String, headers: List<Header>): HttpClient.Response {
        val mockedResponse = mockedPostResponses.firstOrNull { it.requestBody == body && it.url == url }
        if (mockedResponse == null) error(
            """NO MOCKED RESPONSE FOR:
            POST response for url=$url 
            and body=$body
            has not been set!
        """.trimIndent()
        )
        return HttpClient.Response(
            requestUrl = mockedResponse.url,
            responseBody = mockedResponse.responseBody
        )
    }

    override fun patch(url: String, body: String, headers: List<Header>): HttpClient.Response {
        val mockedResponse = mockedPatchResponses.firstOrNull { it.url == url && it.requestBody == body }
        if (mockedResponse == null) error(
            """NO MOCKED RESPONSE FOR:
            PATCH response for url=$url 
            and body=$body
            has not been set!
        """.trimIndent()
        )
        return HttpClient.Response(
            requestUrl = mockedResponse.url,
            responseBody = mockedResponse.responseBody
        )
    }

    fun assertThat(): Matcher {
        return Matcher(
            mockedGetResponses,
            mockedPostResponses,
            mockedPatchResponses,
        )
    }

    fun mockNewResponse(): MockRequestBuilder {
        return MockRequestBuilder(
            mockedGetResponses,
            mockedPostResponses,
            mockedPatchResponses,
        )
    }

    inner class MockRequestBuilder(
        private val mockedGetResponses: MutableList<HttpClient.Response>,
        private val mockedPostResponses: MutableList<RequestWithBody>,
        private val mockedPatchResponses: MutableList<RequestWithBody>
    ) {

        private var response = HttpClient.Response("", "")

        private var requestBody = ""
        private var method = ""

        fun url(url: String): MockRequestBuilder {
            response = response.copy(requestUrl = url)
            return this
        }

        fun get(): MockRequestBuilder {
            method = "GET"
            return this
        }

        fun post(body: String): MockRequestBuilder {
            method = "POST"
            requestBody = body
            return this
        }

        fun patch(body: String): MockRequestBuilder {
            method = "PATCH"
            requestBody = body
            return this
        }

        fun shouldReturnBody(body: String): MockRequestBuilder {
            response = response.copy(responseBody = body)
            return this
        }

        fun build() {
            if (response.requestUrl.isEmpty()) error("URL should be set!")
            if (response.responseBody.isNullOrEmpty()) error("Response body should be set!")
            if (method.isEmpty()) error("Method should be set!")
            when (method) {
                "GET" -> mockedGetResponses.add(response)
                "POST" -> mockedPostResponses.add(
                    RequestWithBody(
                        url = response.requestUrl,
                        requestBody = requestBody,
                        responseBody = response.responseBody!!
                    )
                )

                "PATCH" -> mockedPatchResponses.add(
                    RequestWithBody(
                        url = response.requestUrl,
                        requestBody = requestBody,
                        responseBody = response.responseBody!!
                    )
                )
            }
        }
    }

    class RequestWithBody(
        val url: String,
        val requestBody: String,
        val responseBody: String,
    )

    class Matcher(
        private val mockedGetResponses: MutableList<HttpClient.Response>,
        private val mockedPostResponses: MutableList<RequestWithBody>,
        private val mockedPatchResponses: MutableList<RequestWithBody>,
    ) {

        private var method = ""
        private var url = ""
        private var body = ""

        fun post(
            url: String,
            body: String,
        ): Matcher {
            method = "POST"
            this.url = url
            this.body = body
            return this
        }

        fun wasSent() {
            when (method) {
                "POST" -> mockedPostResponses.firstOrNull {
                    it.url == url && it.requestBody == body
                } ?: error("""
                    METHOD: $method
                    URL: $url
                    BODY: $body
                    has not been sent!
                """.trimIndent())
            }
        }
    }
}