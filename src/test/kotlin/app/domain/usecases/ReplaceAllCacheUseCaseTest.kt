package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.danceofvalkyries.app.domain.usecases.ReplaceAllCacheUseCase
import org.danceofvalkyries.app.domain.usecases.ReplaceFlashCardsInCacheUseCase
import org.danceofvalkyries.app.domain.usecases.ReplaceNotionDbsInCacheUseCase
import org.danceofvalkyries.utils.DispatchersImpl

class ReplaceAllCacheUseCaseTest : FunSpec() {


    private val replaceFlashCardsInCacheUseCase: ReplaceFlashCardsInCacheUseCase = mockk(relaxed = true)
    private val replaceNotionDbsInCacheUseCase: ReplaceNotionDbsInCacheUseCase = mockk(relaxed = true)

    private lateinit var replaceAllCacheUseCase: ReplaceAllCacheUseCase

    init {
        beforeTest {
            clearAllMocks()
            replaceAllCacheUseCase = ReplaceAllCacheUseCase(
                replaceFlashCardsInCacheUseCase,
                replaceNotionDbsInCacheUseCase,
                DispatchersImpl(Dispatchers.Unconfined)
            )
        }

        test("Should replace all cache") {
            replaceAllCacheUseCase.execute()

            coVerify(exactly = 1) { replaceFlashCardsInCacheUseCase.execute() }
            coVerify(exactly = 1) { replaceNotionDbsInCacheUseCase.execute() }
        }
    }
}