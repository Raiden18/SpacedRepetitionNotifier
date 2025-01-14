package app.domain.srs

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.mockk
import org.danceofvalkyries.app.data.persistance.notion.page.flashcard.NotionPageFlashCardDataBaseTable
import org.danceofvalkyries.app.domain.srs.SpaceRepetitionSessionImpl
import org.danceofvalkyries.notion.api.models.NotionId
import org.danceofvalkyries.notion.impl.flashcardpage.FlashCardNotionPageApi

class SpaceRepetitionSessionImplTest : BehaviorSpec() {

    private val flashCardDatabase: NotionPageFlashCardDataBaseTable = mockk(relaxed = true)
    private val flashCardNotionPageApi: FlashCardNotionPageApi = mockk(relaxed = true)
    private lateinit var spaceRepetitionSessionImpl: SpaceRepetitionSessionImpl

    init {
        beforeTest {
            clearAllMocks()
        }

        Given("Space Repetition Session") {

            val notionDbId = NotionId("228")
            beforeTest {
                spaceRepetitionSessionImpl = SpaceRepetitionSessionImpl(
                    notionDbId,
                    flashCardDatabase,
                    flashCardNotionPageApi,
                )
            }

            When("When next flash card is required to show") {
                beforeTest {
                    spaceRepetitionSessionImpl.getNextFlashCard()
                }

                Then("Should fetch message from "){

                }
            }
        }
    }
}