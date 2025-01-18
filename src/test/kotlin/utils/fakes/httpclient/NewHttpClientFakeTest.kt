package utils.fakes.httpclient

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.utils.HttpClient
import org.junit.jupiter.api.assertThrows

class NewHttpClientFakeTest : FunSpec() {

    private lateinit var newHttpClientFake: NewHttpClientFake

    init {
        beforeTest {
            newHttpClientFake = NewHttpClientFake()
        }

        test("Should mock GET request") {
            newHttpClientFake.mockNewResponse()
                .url("https://google.com")
                .get()
                .shouldReturnBody("""{}""")
                .build()

            newHttpClientFake.mockNewResponse()
                .url("https://ya.ru")
                .get()
                .shouldReturnBody("""{"result":true}""")
                .build()

            newHttpClientFake.get(
                "https://google.com",
                headers = emptyList()
            ) shouldBe HttpClient.Response(
                requestUrl = "https://google.com",
                responseBody = """{}"""
            )
            newHttpClientFake.get(
                "https://ya.ru",
                headers = emptyList()
            ) shouldBe HttpClient.Response(
                requestUrl = "https://ya.ru",
                responseBody = """{"result":true}"""
            )
        }

        test("Should mock POST request") {
            newHttpClientFake.mockNewResponse()
                .url("https://youtube.com")
                .post("""{"request":something}""")
                .shouldReturnBody("{}")
                .build()

            newHttpClientFake.post(
                url = "https://youtube.com",
                body = """{"request":something}""",
                headers = emptyList()
            ) shouldBe HttpClient.Response(
                requestUrl = "https://youtube.com",
                responseBody = "{}"
            )
        }

        test("Should mock PATCH request") {
            newHttpClientFake.mockNewResponse()
                .url("https://reddit.com")
                .patch("{}")
                .shouldReturnBody("""{"result":true}""")
                .build()

            newHttpClientFake.patch(
                url = "https://reddit.com",
                body = "{}",
                headers = emptyList()
            ) shouldBe HttpClient.Response(
                requestUrl = "https://reddit.com",
                responseBody = """{"result":true}"""
            )
        }

        test("Should throw exception if POST request was not called") {
            assertThrows<Throwable> {
                newHttpClientFake.assertThat()
                    .post(url = "ya.ru", body = "{}")
                    .wasSent()
            }
        }

        test("Should not throw exception if POST request was called") {
            newHttpClientFake.mockNewResponse()
                .url("https://youtube.com")
                .post("{}")
                .shouldReturnBody("{}")
                .build()

            newHttpClientFake.post(
                url = "https://youtube.com",
                body = "{}",
                headers = emptyList()
            ) shouldBe HttpClient.Response(
                requestUrl = "https://youtube.com",
                responseBody = "{}"
            )

            newHttpClientFake.assertThat()
                .post(url = "https://youtube.com", body = "{}").wasSent()
        }
    }
}