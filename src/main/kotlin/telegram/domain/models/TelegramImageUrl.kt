package org.danceofvalkyries.telegram.domain.models

data class TelegramImageUrl(
    private val url: String
) {
    companion object {
        val NOT_SUPPORTED_BY_TELEGRAM_TAGS = listOf(
            "shutterstock.com",
            "base64",
            "?",
        )

        const val BLUE_SCREEN = "https://neosmart.net/wiki/wp-content/uploads/sites/5/2013/08/unmountable-boot-volume.png"
    }

    fun get(): String {
        val isSupportedByTelegram = NOT_SUPPORTED_BY_TELEGRAM_TAGS.any { url.contains(it) }.not()
        return if (isSupportedByTelegram) {
            url
        } else {
            BLUE_SCREEN
        }
    }
}