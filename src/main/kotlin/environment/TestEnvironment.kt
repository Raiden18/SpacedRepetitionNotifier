package org.danceofvalkyries.environment

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.danceofvalkyries.config.data.TestConfigRepository
import org.danceofvalkyries.config.domain.Config
import org.danceofvalkyries.utils.db.DataBase
import org.danceofvalkyries.utils.db.DataBaseImpl
import java.util.concurrent.TimeUnit

class TestEnvironment : Environment {

    override val dataBase: DataBase
        get() {
            val path = "${System.getProperty("user.home")}/spaced_repetition_test.db"
            return DataBaseImpl(path)
        }

    override val httpClient: OkHttpClient
        get() {
            val logger = HttpLoggingInterceptor.Logger { message -> println(message) }
            val interceptor = HttpLoggingInterceptor(logger).apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val timeOut = 60_000L
            return OkHttpClient.Builder()
                .callTimeout(timeOut, TimeUnit.MILLISECONDS)
                .readTimeout(timeOut, TimeUnit.MILLISECONDS)
                .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
                .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor)
                .build()
        }

    override val config: Config
        get() = TestConfigRepository().getConfig()
}