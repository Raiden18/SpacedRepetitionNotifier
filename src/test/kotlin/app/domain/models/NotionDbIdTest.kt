package app.domain.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.domain.models.Id

class NotionDbIdTest : FunSpec() {

    init {
        test("Should remove '-' symbols from id") {
            Id("1-2-3-4")
                .valueId shouldBe "1234"
        }
    }
}