package app.data.repositories.flashcards

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.app.data.repositories.flashcards.toFlashCard
import org.danceofvalkyries.app.domain.models.FlashCard
import org.danceofvalkyries.app.domain.models.Id
import org.danceofvalkyries.app.domain.models.ImageUrl
import org.danceofvalkyries.notion.data.repositories.api.response.CoverBody
import org.danceofvalkyries.notion.data.repositories.api.response.CoverResponse
import org.danceofvalkyries.notion.data.repositories.api.response.NotionPageResponse
import org.danceofvalkyries.notion.data.repositories.api.response.ParentResponse
import org.danceofvalkyries.notion.data.repositories.api.response.properties.PropertyResponse
import org.danceofvalkyries.notion.data.repositories.api.response.properties.RichTextResponse
import org.danceofvalkyries.notion.data.repositories.api.response.properties.TextContentResponse
import org.danceofvalkyries.notion.data.repositories.api.response.properties.TextResponse

class NotionPageResponseExtKtTest : FunSpec() {

    init {
        test("Should map to FlashCard") {
            val pageTitle = "encounter"
            val exampleSentence = "I had an alarming encounter with a wild pig"
            val explanation = "a meeting, especially one that happens by chance"
            val coverUrl = "https://vovatia.wordpress.com/wp-content/uploads/2013/02/sidle.jpg"
            val id = "228"
            val parentDbId = "322"

            createNotionPageResponse(
                id,
                coverUrl,
                parentDbId,
                pageTitle,
                exampleSentence,
                explanation
            ).toFlashCard() shouldBe FlashCard(
                memorizedInfo = pageTitle,
                example = exampleSentence,
                answer = explanation,
                imageUrl = ImageUrl(coverUrl),
                onlineDictionaries = emptyList(),
                metaInfo = FlashCard.MetaInfo(
                    id = id,
                    notionDbId = Id(parentDbId),
                )
            )
        }

        test("Should null if fields are empty string") {
            createNotionPageResponse(
                "1",
                "",
                "2",
                "title",
                "",
                "",
            ).toFlashCard() shouldBe FlashCard(
                memorizedInfo = "title",
                example = null,
                answer = null,
                imageUrl = null,
                onlineDictionaries = emptyList(),
                metaInfo = FlashCard.MetaInfo(
                    id = "1",
                    notionDbId = Id("2"),
                )
            )
        }

        test("Should null if fields are blank") {
            createNotionPageResponse(
                "1",
                "  ",
                "2",
                "title",
                "  ",
                "  ",
            ).toFlashCard() shouldBe FlashCard(
                memorizedInfo = "title",
                example = null,
                answer = null,
                imageUrl = null,
                onlineDictionaries = emptyList(),
                metaInfo = FlashCard.MetaInfo(
                    id = "1",
                    notionDbId = Id("2"),
                )
            )
        }
    }

    private fun createNotionPageResponse(
        id: String? = null,
        coverUrl: String? = null,
        parentDbId: String? = null,
        pageTitle: String? = null,
        exampleSentence: String? = null,
        explanation: String? = null,
    ) = NotionPageResponse(
        objectType = null,
        id = id,
        createdTime = null,
        lastEditedTime = null,
        createdBy = null,
        lastEditedBy = null,
        cover = CoverResponse(
            external = CoverBody(
                url = coverUrl
            )
        ),
        icon = null,
        parent = ParentResponse(
            type = null,
            databaseId = parentDbId,
        ),
        archived = null,
        inTrash = null,
        properties = mapOf(
            "Name" to PropertyResponse(
                id = null,
                type = null,
                checkbox = null,
                richText = null,
                title = listOf(
                    TextResponse(
                        text = TextContentResponse(
                            content = pageTitle,
                            link = null
                        ),
                        plainText = null,
                    )
                ),
            ),
            "Example" to PropertyResponse(
                id = null,
                type = null,
                checkbox = null,
                richText = listOf(
                    RichTextResponse(
                        text = TextContentResponse(
                            content = exampleSentence,
                            link = null,
                        )
                    )
                ),
                title = null,
            ),
            "Explanation" to PropertyResponse(
                id = null,
                type = null,
                checkbox = null,
                richText = listOf(
                    RichTextResponse(
                        text = TextContentResponse(
                            content = explanation,
                            link = null,
                        )
                    )
                ),
                title = null,
            )
        ),
        url = null,
        publicUrl = null,
    )
}