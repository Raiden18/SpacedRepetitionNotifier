package org.danceofvalkyries.utils.scheduler

interface ActonScheduler {
    fun schedule(action: suspend () -> Unit)
    suspend fun awaitAll()
}