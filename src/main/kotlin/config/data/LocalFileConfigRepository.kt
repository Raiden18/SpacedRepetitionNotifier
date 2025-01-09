package org.danceofvalkyries.config.data

import com.google.gson.Gson
import okio.Path.Companion.toPath
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.config.domain.ConfigRepository

class LocalFileConfigRepository(
    private val gson: Gson
) : ConfigRepository {

    private companion object {
        private const val SPACED_REPETITION_CONFIG_PATH = "./spaced_repetition.config"
    }

    override fun getConfig(): Config {
        return Config(
            gson,
            getConfigJson()
        )
    }


    private fun getConfigJson(): String {
        return SPACED_REPETITION_CONFIG_PATH
            .toPath()
            .toFile()
            .readText()
    }
}