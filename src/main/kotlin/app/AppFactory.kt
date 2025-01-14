package org.danceofvalkyries.app

import kotlinx.coroutines.Dispatchers
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.utils.DispatchersImpl

fun interface AppFactory {
    fun create(): App
}

fun AppFactory(args: Array<String>): AppFactory {
    return AppFactoryImpl(args)
}

private class AppFactoryImpl(
    private val args: Array<String>
) : AppFactory {

    override fun create(): App {
        val appKindArgument = args.getOrNull(0) ?: error("App kind argument must be set!")
        val environmentArgument = args.getOrNull(1) ?: error("Environment kind argument must be set!")

        val environment = Environment(environmentArgument)
        val dispatchers = DispatchersImpl(Dispatchers.IO)

        return when (appKindArgument) {
            "notifier" -> NotifierApp(dispatchers, environment)
            "button_listener" -> TelegramButtonListenerApp(dispatchers, environment)
            "sand_box" -> SandBoxApp(dispatchers, environment)
            else -> error("Unknown App kind argument")
        }
    }
}