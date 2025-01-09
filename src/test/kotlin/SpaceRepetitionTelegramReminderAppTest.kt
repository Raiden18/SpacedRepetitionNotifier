import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.danceofvalkyries.app.AppImpl
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBase
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.notion.domain.repositories.SpacedRepetitionDataBaseRepository

class SpaceRepetitionTelegramReminderAppTest : FunSpec() {

    private val spacedRepetitionDataBaseRepository: SpacedRepetitionDataBaseRepository = mockk(relaxed = true)
    private val sendRevisingMessage: suspend (SpacedRepetitionDataBaseGroup) -> Unit = mockk(relaxed = true)
    private val sendGoodJobMessage: suspend () -> Unit = mockk(relaxed = true)
    private lateinit var appImpl: AppImpl

    init {
        beforeTest {
            clearAllMocks()
            appImpl = AppImpl(
                spacedRepetitionDataBaseRepository,
                flashCardsThreshold = 10,
                sendRevisingMessage = sendRevisingMessage,
                sendGoodJobMessage = sendGoodJobMessage,
            )
        }

        test("Should send good job notifications if there are less flash cards than threshold") {
            coEvery { spacedRepetitionDataBaseRepository.getAll() } returns SpacedRepetitionDataBaseGroup(
                listOf(
                    SpacedRepetitionDataBase(
                        id = "123",
                        name = "Untiteled",
                        flashCards = listOf()
                    )
                )
            )
            appImpl.run()

            coVerify(exactly = 1) { sendGoodJobMessage.invoke() }
        }

        test("Should send notifications if there are more flash cards than threshold") {
            val group = SpacedRepetitionDataBaseGroup(
                listOf(
                    SpacedRepetitionDataBase(
                        id = "123",
                        name = "Untiteled",
                        flashCards = listOf(
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard,
                            FlashCard
                        )
                    )
                )
            )
            coEvery { spacedRepetitionDataBaseRepository.getAll() } returns group

            appImpl.run()

            coVerify(exactly = 1) { sendRevisingMessage.invoke(group) }
        }
    }
}