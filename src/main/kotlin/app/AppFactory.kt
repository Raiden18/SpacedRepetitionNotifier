package org.danceofvalkyries.app

import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.danceofvalkyries.environment.EnvironmentImpl
import org.danceofvalkyries.utils.DispatchersImpl
import org.danceofvalkyries.utils.db.DataBaseImpl
import java.util.concurrent.TimeUnit

fun AppFactory(args: Array<String>): AppFactory {
    return AppFactoryImpl(args)
}

fun interface AppFactory {
    fun create(): App
}

private class AppFactoryImpl(
    private val args: Array<String>
) : AppFactory {

    override fun create(): App {
        val appKindArgument = args.firstOrNull() ?: error("App kind argument must be set!")

        val environment = EnvironmentImpl()
        val dispatchers = DispatchersImpl(Dispatchers.IO)
        val db = DataBaseImpl(environment)
        val httpClient = createHttpClient()

        return when (appKindArgument) {
            "notifier" -> NotifierApp(dispatchers, db, httpClient)
            "button_listener" -> TelegramButtonListenerApp(dispatchers, db, httpClient)
            "test" -> TestApp(dispatchers, db, httpClient)
            else -> error("Unknown App kind argument")
        }
    }

    private fun createHttpClient(): OkHttpClient {
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
}