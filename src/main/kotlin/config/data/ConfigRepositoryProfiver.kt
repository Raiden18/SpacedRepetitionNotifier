package org.danceofvalkyries.config.data

import com.google.gson.Gson
import org.danceofvalkyries.config.domain.ConfigRepository

fun ConfigRepositoryProvider(): ConfigRepository {
    return TestConfigRepository(Gson())
}