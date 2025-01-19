package org.danceofvalkyries.config.data

import com.google.gson.Gson
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.config.domain.ConfigRepository
import org.danceofvalkyries.utils.rest.jsonObject

class TestConfigRepository : ConfigRepository {

    private val gson = Gson()


    override fun getConfig(): Config {
        return jsonObject {

        }.let { gson.toJson(it) }.let { ConfigData(gson, it) }
    }
}