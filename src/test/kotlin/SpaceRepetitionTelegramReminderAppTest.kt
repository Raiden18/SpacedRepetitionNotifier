import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.danceofvalkyries.SpaceRepetitionTelegramReminderApp
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBase
import org.danceofvalkyries.notion.domain.models.SpacedRepetitionDataBaseGroup
import org.danceofvalkyries.notion.domain.repositories.SpacedRepetitionDataBaseRepository

class SpaceRepetitionTelegramReminderAppTest : FunSpec() {

    private val spacedRepetitionDataBaseRepository: SpacedRepetitionDataBaseRepository = mockk(relaxed = true)
    private val buildMessage: (SpacedRepetitionDataBaseGroup) -> String = { "Message" }
    private val sendMessage: suspend (String) -> Unit = mockk(relaxed = true)
    private lateinit var spaceRepetitionTelegramReminderApp: SpaceRepetitionTelegramReminderApp

    init {
        beforeTest {
            clearAllMocks()
            spaceRepetitionTelegramReminderApp = SpaceRepetitionTelegramReminderApp(
                spacedRepetitionDataBaseRepository,
                flashCardsThreshold = 10,
                buildMessage = buildMessage,
                sendMessage = sendMessage
            )
        }

        test("Should not send notifications if there are less flash cards than threshold") {
            coEvery { spacedRepetitionDataBaseRepository.getAll() } returns SpacedRepetitionDataBaseGroup(
                listOf(
                    SpacedRepetitionDataBase(
                        id = "123",
                        name = "Untiteled",
                        flashCards = listOf()
                    )
                )
            )
            spaceRepetitionTelegramReminderApp.run()

            coVerify(exactly = 0) { sendMessage.invoke(any()) }
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

            spaceRepetitionTelegramReminderApp.run()

            coVerify(exactly = 1) { sendMessage.invoke(buildMessage.invoke(group)) }
        }
    }
}