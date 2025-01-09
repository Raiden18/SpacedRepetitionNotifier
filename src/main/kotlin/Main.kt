package org.danceofvalkyries

import org.danceofvalkyries.app.AnalyzeFlashCardsAndSendNotificationApp
import org.danceofvalkyries.app.AppMeasurePerfomanceDecorator

suspend fun main() {
    AppMeasurePerfomanceDecorator(
        AnalyzeFlashCardsAndSendNotificationApp()
    ).run()
}
