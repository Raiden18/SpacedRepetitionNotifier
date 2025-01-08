package org.danceofvalkyries.notion.api.rest.request

import com.google.gson.Gson
import org.danceofvalkyries.json.JsonObject
import org.danceofvalkyries.json.`object`

fun SpacedRepetitionRequestBody(
    gson: Gson,
): String {
    return `object` {
        "filter" to `object` {
            "and" to arrayOf(
                `object` {
                    property("Know Level 1")
                    checkBox(true)
                },
                `object` {
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
    "checkbox" to `object` {
        "equals" to isChecked
    }
}
