package integrations

import kotlinx.coroutines.test.runTest
import org.danceofvalkyries.job.Job

class JobRunInTestDecorator(
    private val job: Job
) : Job {
    override suspend fun run() {
        runTest {
            job.run()
        }
    }
}