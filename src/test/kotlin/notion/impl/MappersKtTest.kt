package notion.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.notion.impl.restapi.models.NotionPageData
import org.danceofvalkyries.notion.impl.restapi.models.PropertyData
import org.danceofvalkyries.notion.impl.restapi.models.response.CoverBody
import org.danceofvalkyries.notion.impl.restapi.models.response.CoverResponse
import org.danceofvalkyries.notion.impl.restapi.models.response.ParentResponse
import org.danceofvalkyries.notion.impl.restapi.models.response.properties.RichTextResponse
import org.danceofvalkyries.notion.impl.restapi.models.response.properties.TextContentResponse
import org.danceofvalkyries.notion.impl.restapi.models.response.properties.TextResponse
import org.danceofvalkyries.notion.impl.toDomain
import org.danceofvalkyries.notion.impl.toUpdateKnowLevels
import org.danceofvalkyries.notion.api.models.FlashCardNotionPage
import org.danceofvalkyries.notion.api.models.KnowLevels
import org.danceofvalkyries.notion.api.models.NotionId

class MappersKtTest : FunSpec() {

    init {
        test("Should create model to update know levels") {
            FlashCardNotionPage(
                name = "1",
                example = "2",
                explanation = "3",
                coverUrl = "4",
                id = NotionId("5"),
                notionDbID = NotionId("6"),
                knowLevels = KnowLevels(
                    mapOf(
                        1 to true,
                        2 to true,
                    )
                )
            ).toUpdateKnowLevels() shouldBe NotionPageData(
                id = "5",
                properties = mapOf(
                    "Know Level 1" to PropertyData(checkbox = true),
                    "Know Level 2" to PropertyData(checkbox = true),
                )

            )

        }

        test("Should map to Domain Page") {
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
                explanation,
                knowLevel1 = true
            ).toDomain() shouldBe FlashCardNotionPage(
                name = pageTitle,
                example = exampleSentence,
                explanation = explanation,
                coverUrl = coverUrl,
                id = NotionId(id),
                notionDbID = NotionId(parentDbId),
                knowLevels = KnowLevels(
                    mapOf(
                        1 to true,
                        2 to null,
                        3 to null,
                        4 to null,
                        5 to null,
                        6 to null,
                        7 to null,
                        8 to null,
                        9 to null,
                        10 to null,
                        11 to null,
                        12 to null,
                        13 to null
                    )
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
        knowLevel1: Boolean? = null
    ) = NotionPageData(
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
            "Name" to PropertyData(
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
            "Example" to PropertyData(
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
            "Explanation" to PropertyData(
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
            ),
            "Know Level 1" to PropertyData(
                checkbox = knowLevel1
            )
        ),
        url = null,
        publicUrl = null,
    )

}