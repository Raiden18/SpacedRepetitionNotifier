package org.danceofvalkyries.job

interface Job {
    suspend fun run()
}
