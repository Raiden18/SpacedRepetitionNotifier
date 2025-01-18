package integrations

import kotlinx.coroutines.test.runTest
import org.danceofvalkyries.app.App

class AppRunInTestDecorator(
    private val app: App
) : App {
    override suspend fun run() {
        runTest {
            app.run()
        }
    }
}