package org.danceofvalkyries

import org.danceofvalkyries.app.AppFactory

suspend fun main(arguments: Array<String>) {
    AppFactory(arguments)
        .create()
        .run()
}
