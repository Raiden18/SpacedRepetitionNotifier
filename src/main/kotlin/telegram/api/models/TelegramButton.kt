package org.danceofvalkyries.telegram.api.models

// TODO: add sealed class instead of url and callback?
data class TelegramButton(
    val text: String,
    val url: String?,
    val callback: String? = null
)
