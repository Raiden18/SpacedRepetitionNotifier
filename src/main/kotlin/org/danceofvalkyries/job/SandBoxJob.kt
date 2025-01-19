package org.danceofvalkyries.job

import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.utils.Dispatchers

class SandBoxJob(
    private val dispatchers: Dispatchers,
    private val environment: Environment,
) : Job {

    override val type: String = "sand_box"

    override suspend fun run() = Unit
}