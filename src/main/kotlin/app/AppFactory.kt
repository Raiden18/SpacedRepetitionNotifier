package org.danceofvalkyries.app

import kotlinx.coroutines.Dispatchers
import org.danceofvalkyries.config.data.ConfigRepositoryProvider
import org.danceofvalkyries.environment.EnvironmentImpl
import org.danceofvalkyries.utils.DispatchersImpl

fun interface AppFactory {
    fun create(): App
}

fun AppFactory(): AppFactory {
    return AppFactory {
        val environment = EnvironmentImpl()
        val dispatchers = DispatchersImpl(Dispatchers.IO)
        val configRepository = ConfigRepositoryProvider()
        AnalyzeFlashCardsAndSendNotificationApp(environment, dispatchers, configRepository)
    }
}