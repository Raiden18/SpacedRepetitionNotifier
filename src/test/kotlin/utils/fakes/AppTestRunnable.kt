package utils.fakes

import io.kotest.engine.runBlocking
import org.danceofvalkyries.app.App

class AppTestRunnable(
    private val app: App
) : App {
    override suspend fun run() {
        runBlocking {
            app.run()
        }
    }
}