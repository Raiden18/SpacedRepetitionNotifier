package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import org.danceofvalkyries.app.domain.usecases.ReplaceAllCacheUseCase

class GetFlashCardsUseCaseKtTest : FunSpec() {

    private lateinit var getFlashCardsUseCase: ReplaceAllCacheUseCase

    init {
        beforeTest {
            clearAllMocks()
        }

        test("Should fetch flash cards from n")
    }
}