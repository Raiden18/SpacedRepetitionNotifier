package org.danceofvalkyries.app.domain.models

data class Id(
    private val value: String
) {

    companion object {
        val EMPTY = Id("")
    }

    fun getValue(idEditor: IdEditor): String {
        return idEditor.get(valueId)
    }

    val valueId: String
        get() = value
            .replace("-", "")

    fun interface IdEditor {
        companion object {
            val AS_IS = IdEditor { it }
        }

        fun get(value: String): String
    }
}