package org.danceofvalkyries.environment

import okhttp3.OkHttpClient
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.utils.db.DataBase

interface Environment {
    val dataBase: DataBase
    val httpClient: OkHttpClient
    val config: Config
}

fun Environment(parameter: String): Environment {
    return when (parameter) {
        "prod" -> ProductionEnvironment()
        "test" -> TestEnvironment()
        else -> error("Unknown environment: $parameter")
    }
}
