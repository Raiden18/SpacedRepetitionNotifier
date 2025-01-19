package job.data.notion.pages

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.notion.pages.NotionPageFlashCard
import org.danceofvalkyries.notion.pages.forget
import org.danceofvalkyries.notion.pages.recall

class NotionPageFlashCardKtTest : FunSpec() {

    init {
        test("Should disable all") {
            NotionPageFlashCard()
                .forget() shouldBe mapOf(
                1 to false,
                2 to false,
                3 to false,
                4 to false,
            )
        }

        test("Should move to next level") {
            NotionPageFlashCard()
                .recall() shouldBe mapOf(
                1 to true,
                2 to true,
                3 to true,
                4 to false,
            )
        }
    }

    private fun NotionPageFlashCard(): NotionPageFlashCard {
        return object : NotionPageFlashCard {
            override val id: String
                get() = TODO("Not yet implemented")
            override val coverUrl: String?
                get() = TODO("Not yet implemented")
            override val notionDbID: String
                get() = TODO("Not yet implemented")
            override val name: String
                get() = TODO("Not yet implemented")
            override val example: String?
                get() = TODO("Not yet implemented")
            override val explanation: String?
                get() = TODO("Not yet implemented")
            override val knowLevels: Map<Int, Boolean>
                get() = mapOf(
                    1 to true,
                    2 to true,
                    3 to false,
                    4 to false
                )

            override fun setKnowLevels(knowLevels: Map<Int, Boolean>) {
                TODO("Not yet implemented")
            }
        }
    }
}