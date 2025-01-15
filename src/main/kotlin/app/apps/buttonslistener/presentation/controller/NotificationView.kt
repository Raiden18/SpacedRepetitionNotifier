package org.danceofvalkyries.app.apps.buttonslistener.presentation.controller

interface NotificationView {
    suspend fun update()
    suspend fun hide()
}