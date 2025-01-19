package org.danceofvalkyries.job

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.danceofvalkyries.utils.Dispatchers
import org.danceofvalkyries.utils.rest.clients.http.HttpClient

class JobResourcesLifeCycleDecorator(
    private val dispatchers: Dispatchers,
    private val httpClient: HttpClient,
    private val job: Job,
) : Job by job {

    private val coroutineScope = CoroutineScope(dispatchers.unconfined)

    override suspend fun run() {
        coroutineScope.launch {
            job.run()
            httpClient.releaseResources()
        }
    }
}