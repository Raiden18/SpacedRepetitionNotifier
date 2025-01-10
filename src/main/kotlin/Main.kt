package org.danceofvalkyries

import org.danceofvalkyries.app.AppFactory

suspend fun main() {
    AppFactory()
        .create()
        .run()
}
