package org.danceofvalkyries.environment

import okhttp3.OkHttpClient
import org.danceofvalkyries.config.data.LocalFileConfigRepository
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.utils.db.DataBase
import org.danceofvalkyries.utils.db.DataBaseImpl
import org.danceofvalkyries.utils.rest.clients.http.HttpClient
import org.danceofvalkyries.utils.rest.clients.http.HttpClientImpl
import java.util.concurrent.TimeUnit

class ProductionEnvironment : Environment {

    override val dataBase: DataBase
        get() {
            val path = "${System.getProperty("user.home")}/spaced_repetition.db"
            return DataBaseImpl(path)
        }

    override val httpClient: HttpClient by lazy {
        val timeOut = 60_000L
        HttpClientImpl(
            OkHttpClient.Builder()
                .callTimeout(timeOut, TimeUnit.MILLISECONDS)
                .readTimeout(timeOut, TimeUnit.MILLISECONDS)
                .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
                .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
                .build()
        )
    }
    override val config: Config
        get() = LocalFileConfigRepository().getConfig()
}