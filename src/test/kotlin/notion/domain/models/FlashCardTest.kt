package notion.domain.models

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.NotionDbId

class FlashCardTest : FunSpec() {

    init {
        test("All values should be with escaped special characters") {
            val specialCharacters = "!()=._\\\\"
            val escaped = "\\!\\(\\)\\=\\.\\_\\"
            val flashCard = FlashCard(
                memorizedInfo = specialCharacters,
                example = specialCharacters,
                answer = specialCharacters,
                imageUrl = null,
                metaInfo = FlashCard.MetaInfo("", NotionDbId.EMPTY)
            )

            flashCard.answerValue shouldBe escaped
            flashCard.exampleValue shouldBe escaped
            flashCard.memorizedInfoValue shouldBe escaped
        }
    }
}