package notion.api.rest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.notion.api.rest.DatabaseUrl

class NotionUrlsTest : FunSpec() {

    init {
        test("Should return url for database") {
            DatabaseUrl("228")
                .dataBases()
                .toString() shouldBe "https://api.notion.com/v1/databases/228"
        }

        test("Should return url for query database") {
            DatabaseUrl("228")
                .databasesQuery()
                .toString() shouldBe "https://api.notion.com/v1/databases/228/query"
        }
    }
}