package org.danceofvalkyries.notion.domain.models

data class ImageUrl(
    val url: String
) {
    companion object {
        val NOT_SUPPORTED_BY_TG_DOMAINS = listOf(
            "shutterstock.com"
        )

        val BLUE_SCREEN = ImageUrl(
            "https://neosmart.net/wiki/wp-content/uploads/sites/5/2013/08/unmountable-boot-volume.png"
        )
    }

    val isSupportedByTelegram: Boolean
        get() = NOT_SUPPORTED_BY_TG_DOMAINS.any { url.contains(it).not() }

}