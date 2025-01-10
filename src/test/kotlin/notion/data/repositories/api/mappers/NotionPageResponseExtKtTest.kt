package notion.data.repositories.api.mappers

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.danceofvalkyries.notion.data.repositories.api.mappers.toFlashCard
import org.danceofvalkyries.notion.data.repositories.api.rest.response.NotionPageResponse
import org.danceofvalkyries.notion.data.repositories.api.rest.response.properties.PropertyResponse
import org.danceofvalkyries.notion.data.repositories.api.rest.response.properties.RichTextResponse
import org.danceofvalkyries.notion.data.repositories.api.rest.response.properties.TextContentResponse
import org.danceofvalkyries.notion.data.repositories.api.rest.response.properties.TextResponse
import org.danceofvalkyries.notion.domain.models.FlashCard

class NotionPageResponseExtKtTest : FunSpec() {

    init {
        test("Should map to FlashCard") {
            val pageTitle = "encounter"
            val exampleSentence = "I had an alarming encounter with a wild pig"
            val explanation = "a meeting, especially one that happens by chance"
            NotionPageResponse(
                objectType = null,
                id = null,
                createdTime = null,
                lastEditedTime = null,
                createdBy = null,
                lastEditedBy = null,
                cover = null,
                icon = null,
                parent = null,
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
            ).toFlashCard() shouldBe FlashCard(
                memorizedInfo = pageTitle,
                example = exampleSentence,
                answer = explanation,
            )
        }

    }
}