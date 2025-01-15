package org.danceofvalkyries.app.apps.notifier.domain.usecaes

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.utils.Dispatchers

fun interface ReplaceAllNotionCacheUseCase {
    suspend fun execute()
}

fun ReplaceAllNotionCacheUseCase(
    replaceFlashCardsInCacheUseCase: ReplaceFlashCardsInCacheUseCase,
    replaceNotionDbsInCacheUseCase: ReplaceNotionDbsInCacheUseCase,
    dispatchers: Dispatchers,
): ReplaceAllNotionCacheUseCase {
    return ReplaceAllNotionCacheUseCase {
        coroutineScope {
            val replaceAsync = listOf(
                replaceFlashCardsInCacheUseCase::execute,
                replaceNotionDbsInCacheUseCase::execute,
            ).map { async(dispatchers.io) { it.invoke() } }

            replaceAsync.awaitAll()
        }
    }
}