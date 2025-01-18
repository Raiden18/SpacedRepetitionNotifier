package org.danceofvalkyries.app.apps

import org.danceofvalkyries.app.App
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.utils.Dispatchers

class SandBoxApp(
    private val dispatchers: Dispatchers,
    private val environment: Environment,
) : App {
    override suspend fun run() = Unit
}