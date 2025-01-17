package utils

import org.danceofvalkyries.utils.HttpClient
import org.danceofvalkyries.utils.rest.Header

class HttpClientFake : HttpClient {

    val getUrlRequests = mutableListOf<String>()
    val postRequests = mutableListOf<PostRequest>()

    val getUrlAndResponses = mutableMapOf<String, String>()
    val postUrlAndResponse = mutableMapOf<PostRequest, String>()

    fun mockGetResponse(url: String, response: String) {
        getUrlAndResponses[url] = response
    }

    fun mockPostResponse(
        url: String,
        body: String,
        response: String,
    ) {
        val postRequest = PostRequest(
            url = url,
            body = body,
        )
        postUrlAndResponse[postRequest] = response
    }

    override fun get(url: String, headers: List<Header>): HttpClient.Response {
        getUrlRequests.add(url)
        return HttpClient.Response(
            requestUrl = url,
            responseBody = getUrlAndResponses[url]
        )
    }

    override fun post(url: String, body: String, headers: List<Header>): HttpClient.Response {
        val postRequest = PostRequest(
            url = url,
            body = body,
        )
        postRequests.add(postRequest)
        return HttpClient.Response(
            requestUrl = url,
            responseBody = postUrlAndResponse[postRequest]
        )
    }

    data class PostRequest(
        val url: String,
        val body: String,
    )
}