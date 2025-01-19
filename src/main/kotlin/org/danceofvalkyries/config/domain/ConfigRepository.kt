package org.danceofvalkyries.config.domain

interface ConfigRepository {
    fun getConfig(): Config
}