package org.danceofvalkyries.job

import kotlinx.coroutines.test.runTest

class JobRunInTestDecorator(
    private val job: Job
) : Job {
    override suspend fun run() {
        runTest {
            job.run()
        }
    }
}