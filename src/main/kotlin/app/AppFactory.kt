package org.danceofvalkyries.app

import kotlinx.coroutines.Dispatchers
import org.danceofvalkyries.environment.EnvironmentImpl
import org.danceofvalkyries.utils.DispatchersImpl
import org.danceofvalkyries.utils.db.DataBaseImpl

fun interface AppFactory {
    fun create(): App
}

fun AppFactory(): AppFactory {
    return AppFactory {
        val environment = EnvironmentImpl()
        val dispatchers = DispatchersImpl(Dispatchers.IO)
        val db = DataBaseImpl(environment)
        NotifierApp(dispatchers, db)
        TestApp(db, dispatchers)
    }
}