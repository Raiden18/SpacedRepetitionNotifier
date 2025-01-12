package org.danceofvalkyries.notion.data.repositories.api.rest.request

import com.google.gson.Gson
import org.danceofvalkyries.utils.rest.JsonObject
import org.danceofvalkyries.utils.rest.jsonObject

fun SpacedRepetitionRequestBody(
    gson: Gson,
): String {
    return jsonObject {
        "filter" to jsonObject {
            "and" to arrayOf(
                jsonObject {
                    property("Know Level 1")
                    checkBox(true)
                },
                jsonObject {
                    property("Show")
                    checkBox(true)
                }
            )
        }
    }.let(gson::toJson)
}

private fun JsonObject.property(name: String) {
    "property" to name
}

private fun JsonObject.checkBox(isChecked: Boolean) {
    "checkbox" to jsonObject {
        "equals" to isChecked
    }
}
