package notion.api.rest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.notion.data.repositories.api.rest.DatabaseUrl

class NotionUrlsTest : FunSpec() {

    init {
        test("Should return url for database") {
            DatabaseUrl()
                .dataBases("228")
                .toString() shouldBe "https://api.notion.com/v1/databases/228"
        }

        test("Should return url for query database") {
            DatabaseUrl()
                .databasesQuery("228")
                .toString() shouldBe "https://api.notion.com/v1/databases/228/query"
        }
    }
}