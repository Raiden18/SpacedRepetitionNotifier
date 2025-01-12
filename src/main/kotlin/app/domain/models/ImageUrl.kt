package org.danceofvalkyries.app.domain.models

// TODO: add access strategy so that telegram can modify it
data class ImageUrl(
    val url: String
) {
    companion object {
        val NOT_SUPPORTED_BY_TELEGRAM_TAGS = listOf(
            "shutterstock.com",
            "base64",
            "?",
        )

        val BLUE_SCREEN = ImageUrl(
            "https://neosmart.net/wiki/wp-content/uploads/sites/5/2013/08/unmountable-boot-volume.png"
        )
    }

    val isSupportedByTelegram: Boolean
        get() = NOT_SUPPORTED_BY_TELEGRAM_TAGS.any { url.contains(it) }.not()

}