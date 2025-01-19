package org.danceofvalkyries

import kotlinx.coroutines.runBlocking
import org.danceofvalkyries.job.JobFactory

fun main(arguments: Array<String>) = runBlocking {
    JobFactory(arguments)
        .create()
        .run()
}
