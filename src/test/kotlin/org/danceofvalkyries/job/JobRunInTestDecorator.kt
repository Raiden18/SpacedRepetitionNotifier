package org.danceofvalkyries.job

import kotlinx.coroutines.test.runTest

class JobRunInTestDecorator(
    private val job: Job
) : Job by job {

    override suspend fun run() {
        runTest {
            job.run()
        }
    }
}