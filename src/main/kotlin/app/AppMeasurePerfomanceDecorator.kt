package org.danceofvalkyries.app

import org.danceofvalkyries.utils.printMeasure

class AppMeasurePerfomanceDecorator(
    private val app: App
) : App {

    override suspend fun run() {
        printMeasure(
            "app time spent",
        ) {
            app.run()
        }
    }
}