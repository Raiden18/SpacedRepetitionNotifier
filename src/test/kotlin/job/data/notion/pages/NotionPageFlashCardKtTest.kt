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
            override suspend fun getId(): String {
                TODO("Not yet implemented")
            }

            override suspend fun getCoverUrl(): String? {
                TODO("Not yet implemented")
            }

            override suspend fun getNotionDbId(): String {
                TODO("Not yet implemented")
            }

            override suspend fun getName(): String {
                TODO("Not yet implemented")
            }

            override suspend fun getExample(): String? {
                TODO("Not yet implemented")
            }

            override suspend fun getExplanation(): String? {
                TODO("Not yet implemented")
            }

            override suspend fun getKnowLevels(): Map<Int, Boolean> {
                return mapOf(
                    1 to true,
                    2 to true,
                    3 to false,
                    4 to false
                )
            }

            override suspend fun setKnowLevels(knowLevels: Map<Int, Boolean>) {

            }

        }
    }
}