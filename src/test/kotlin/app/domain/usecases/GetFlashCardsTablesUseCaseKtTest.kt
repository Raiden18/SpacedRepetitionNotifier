package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.danceofvalkyries.app.domain.usecases.GetFlashCardsTablesUseCase
import org.danceofvalkyries.notion.domain.models.FlashCard
import org.danceofvalkyries.notion.domain.models.FlashCardTable
import org.danceofvalkyries.notion.domain.models.FlashCardsTablesGroup
import org.danceofvalkyries.notion.domain.repositories.FlashCardsTablesRepository

class GetFlashCardsTablesUseCaseKtTest : FunSpec() {

    private val flashCardsTablesRepository: FlashCardsTablesRepository = mockk(relaxed = true)
    private lateinit var getFlashCardsTablesUseCase: GetFlashCardsTablesUseCase

    init {
        beforeTest {
            clearAllMocks()
            getFlashCardsTablesUseCase = GetFlashCardsTablesUseCase(
                flashCardsTablesRepository
            )
        }
        test("Should clear table, save flash cards to db, end return them") {
            val englishGrammarFlashCards = listOf(FlashCard.EMPTY)
            val notionTable1 = FlashCardTable(
                id = "1",
                name = "EnglishGrammar",
                flashCards = englishGrammarFlashCards
            )
            val flashCardTables = FlashCardsTablesGroup(
                listOf(notionTable1)
            )
            coEvery { flashCardsTablesRepository.getFromNotion() } returns flashCardTables

            getFlashCardsTablesUseCase.execute() shouldBe flashCardTables

            coVerifyOrder {
                flashCardsTablesRepository.clear()
                flashCardsTablesRepository.saveToDb(englishGrammarFlashCards)
            }
        }
    }
}