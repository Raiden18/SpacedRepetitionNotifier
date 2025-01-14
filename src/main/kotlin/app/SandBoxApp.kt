package org.danceofvalkyries.app

import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.utils.Dispatchers

class SandBoxApp(
    private val dispatchers: Dispatchers,
    private val environment: Environment,
) : App {

    override suspend fun run() = Unit
}