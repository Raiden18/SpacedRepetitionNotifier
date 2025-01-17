package utils

import org.danceofvalkyries.utils.HttpClient

class HttpClientFake : HttpClient {

    val urls = mutableListOf<String>()

    override fun get(url: String): HttpClient.Response {
        urls.add(url)
        return HttpClient.Response(
            requestUrl = url
        )
    }
}