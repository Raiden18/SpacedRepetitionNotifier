import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.Message
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBase
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup

class MessageBuilderTest : FunSpec() {

    init {
        test("Should build message") {
            val englishVocabId = "1"
            val spacedRepetitionDataBaseGroup = SpacedRepetitionDataBaseGroup(
                listOf(
                    SpacedRepetitionDataBase(
                        id = "1",
                        name = "English vocabulary",
                        flashCards = listOf(FlashCard)
                    ),
                    SpacedRepetitionDataBase(
                        id = "2",
                        name = "Greek vocabulary",
                        flashCards = listOf(FlashCard)
                    ),
                    SpacedRepetitionDataBase(
                        id = "3",
                        name = "English grammar",
                        flashCards = listOf(FlashCard)
                    ),
                    SpacedRepetitionDataBase(
                        id = "4",
                        name = "Greek grammar",
                        flashCards = emptyList()
                    )
                )
            )
            Message(
                spacedRepetitionDataBaseGroup
            ).toString() shouldBe """
                    Total count: 3
                    
                    English vocabulary: 1
                    [Open](https://www.notion.so/databases/1)
                    
                    Greek vocabulary: 1
                    [Open](https://www.notion.so/databases/2)
                    
                    English grammar: 1
                    [Open](https://www.notion.so/databases/3)
                    
                    Greek grammar: 0
                    [Open](https://www.notion.so/databases/4)
                    
                    
                """.trimIndent()
        }
    }
}