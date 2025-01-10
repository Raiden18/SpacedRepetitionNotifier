package org.danceofvalkyries.notion.domain.models

data class ImageUrl(
    val url: String
) {
    companion object {
        val PROHIBITED_DOMAIN = listOf(
            "shutterstock.com"
        )

        val BLUE_SCREEN = ImageUrl(
            "https://neosmart.net/wiki/wp-content/uploads/sites/5/2013/08/unmountable-boot-volume.png"
        )
    }

    val isSupportedByTelegram: Boolean
        get() = PROHIBITED_DOMAIN.any { url.contains(it).not() }

}