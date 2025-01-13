package notion.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.notion.api.models.NotionId

class NotionIdTest : FunSpec() {

    init {
        test("Should return value without scope") {
            NotionId("1-2-3-4-5")
                .get(NotionId.Modifier.URL_FRIENDLY) shouldBe "12345"
        }
    }
}