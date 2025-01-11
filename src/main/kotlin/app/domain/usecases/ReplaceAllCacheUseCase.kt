package org.danceofvalkyries.app.domain.usecases

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.utils.Dispatchers

fun interface ReplaceAllCacheUseCase {
    suspend fun execute()
}

fun ReplaceAllCacheUseCase(
    replaceFlashCardsInCacheUseCase: ReplaceFlashCardsInCacheUseCase,
    replaceNotionDbsInCacheUseCase: ReplaceNotionDbsInCacheUseCase,
    dispatchers: Dispatchers,
): ReplaceAllCacheUseCase {
    return ReplaceAllCacheUseCase {
        coroutineScope {
            val replaceAsync = listOf(
                replaceFlashCardsInCacheUseCase::execute,
                replaceNotionDbsInCacheUseCase::execute,
            ).map { async(dispatchers.io) { it.invoke() } }

            replaceAsync.awaitAll()
        }
    }
}