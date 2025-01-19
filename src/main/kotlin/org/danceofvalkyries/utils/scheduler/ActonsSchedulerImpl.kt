package org.danceofvalkyries.utils.scheduler

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.danceofvalkyries.utils.Dispatchers

class ActonsSchedulerImpl(
    private val dispatchers: Dispatchers
) : ActonScheduler {

    private val actions = mutableListOf<suspend () -> Unit>()

    override fun schedule(action: suspend () -> Unit) {
        actions.add(action)
    }

    override suspend fun awaitAll() {
        coroutineScope {
            actions.map { async(dispatchers.io) { it.invoke() } }.awaitAll()
            actions.clear()
        }
    }
}