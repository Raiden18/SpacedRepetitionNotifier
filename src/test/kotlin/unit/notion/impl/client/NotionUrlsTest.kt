package unit.notion.impl.client

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NotionUrlsTest : FunSpec() {

    init {
        test("Should return url for database") {
            NotionUrls()
                .dataBases("228")
                .toString() shouldBe "https://api.notion.com/v1/databases/228"
        }

        test("Should return url for query database") {
            NotionUrls()
                .databasesQuery("228")
                .toString() shouldBe "https://api.notion.com/v1/databases/228/query"
        }

        test("Should return url for page") {
            NotionUrls()
                .pages("228")
                .toString() shouldBe "https://api.notion.com/v1/pages/228"
        }
    }
}