package org.danceofvalkyries.notion.api.models

data class NotionId(
    private val rawValue: String
) {

    companion object {
        val EMPTY = NotionId("")
    }

    fun get(modifier: Modifier): String {
        return modifier.modify(rawValue)
    }

    interface Modifier {
        companion object {
            val AS_IS = object : Modifier {
                override fun modify(value: String): String = value
            }
            val URL_FRIENDLY = object : Modifier {
                override fun modify(value: String): String = value.replace("-", "")
            }
        }

        fun modify(value: String): String
    }
}