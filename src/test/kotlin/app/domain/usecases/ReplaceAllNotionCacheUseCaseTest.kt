package app.domain.usecases

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.ReplaceAllNotionCacheUseCase
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.ReplaceFlashCardsInCacheUseCase
import org.danceofvalkyries.app.apps.notifier.domain.usecaes.ReplaceNotionDbsInCacheUseCase
import org.danceofvalkyries.utils.DispatchersImpl

class ReplaceAllNotionCacheUseCaseTest : FunSpec() {


    private val replaceFlashCardsInCacheUseCase: ReplaceFlashCardsInCacheUseCase = mockk(relaxed = true)
    private val replaceNotionDbsInCacheUseCase: ReplaceNotionDbsInCacheUseCase = mockk(relaxed = true)

    private lateinit var replaceAllNotionCacheUseCase: ReplaceAllNotionCacheUseCase

    init {
        beforeTest {
            clearAllMocks()
            replaceAllNotionCacheUseCase = ReplaceAllNotionCacheUseCase(
                replaceFlashCardsInCacheUseCase,
                replaceNotionDbsInCacheUseCase,
                DispatchersImpl(Dispatchers.Unconfined)
            )
        }

        test("Should replace all cache") {
            replaceAllNotionCacheUseCase.execute()

            coVerify(exactly = 1) { replaceFlashCardsInCacheUseCase.execute() }
            coVerify(exactly = 1) { replaceNotionDbsInCacheUseCase.execute() }
        }
    }
}