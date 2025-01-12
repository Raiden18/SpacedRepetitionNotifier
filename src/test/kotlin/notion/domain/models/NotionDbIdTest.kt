package notion.domain.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.notion.domain.models.NotionDbId

class NotionDbIdTest : FunSpec() {

    init {
        test("Should remove '-' symbols from id") {
            NotionDbId("1-2-3-4")
                .valueId shouldBe "1234"
        }
    }
}