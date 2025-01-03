package org.danceofvalkyries.notion.api.rest.request

import com.google.gson.Gson
import org.danceofvalkyries.json.JsonObject
import org.danceofvalkyries.json.`object`

fun SpacedRepetitionRequestBody(
    gson: Gson,
): String {
    val bodyMap = `object` {
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
    }
    return gson.toJson(bodyMap)
}

private fun JsonObject.property(name: String) {
    "property" to name
}

private fun JsonObject.checkBox(isChecked: Boolean) {
    "checkbox" to mapOf(Pair("equals", isChecked))
}
