package org.danceofvalkyries

import org.danceofvalkyries.job.JobFactory

suspend fun main(arguments: Array<String>) {
    JobFactory(arguments)
        .create()
        .run()
}
