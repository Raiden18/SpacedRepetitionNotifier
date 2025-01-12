package notion.domain.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.notion.domain.models.NotionId

class NotionIdTest : FunSpec() {

    init {
        test("Should return value without scope") {
            NotionId("1-2-3-4-5")
                .withoutScore shouldBe "12345"
        }
    }
}