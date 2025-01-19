package org.danceofvalkyries.environment

import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.utils.db.DataBase
import org.danceofvalkyries.utils.rest.clients.http.HttpClient

interface Environment {
    val dataBase: DataBase
    val httpClient: HttpClient
    val config: Config
}

fun Environment(parameter: String): Environment {
    return when (parameter) {
        "prod" -> ProductionEnvironment()
        "test" -> TestEnvironment()
        else -> error("Unknown environment: $parameter")
    }
}
