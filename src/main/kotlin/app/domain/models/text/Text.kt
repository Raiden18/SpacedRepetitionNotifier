package org.danceofvalkyries.app.domain.models.text

data class Text(
    private val value: String?
) {

    companion object {
        val EMPTY = Text("")
    }

    fun getValue(textFormatter: TextModifier): String? {
        return textFormatter.modify(value)
    }

    interface TextModifier {
        companion object {
            val AS_IS = object : TextModifier {
                override fun modify(value: String?): String? = value
            }
        }

        fun modify(value: String?): String?
    }
}