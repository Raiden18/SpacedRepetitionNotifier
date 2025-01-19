package org.danceofvalkyries.utils.rest

fun jsonObject(
    block: JsonObject.() -> Unit
): Map<String, Any> {
    val jsonObject = JsonObject()
    block.invoke(jsonObject)
    return jsonObject.fields
}

class JsonObject {

    val fields = mutableMapOf<String, Any>()

    infix fun String.to(that: Any) {
        val field = Pair(this, that)
        fields[field.first] = field.second
    }
}