package org.danceofvalkyries.job

interface Job {
    val type: String
    suspend fun run()
}
