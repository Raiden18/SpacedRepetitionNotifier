package notion.impl.client.models

import com.google.gson.annotations.SerializedName
import notion.impl.client.models.response.CoverResponse
import notion.impl.client.models.response.ParentResponse
import notion.impl.client.models.response.UserResponse

data class NotionPageData(
    @SerializedName("object") val objectType: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("created_time") val createdTime: String? = null,
    @SerializedName("last_edited_time") val lastEditedTime: String? = null,
    @SerializedName("created_by") val createdBy: UserResponse? = null,
    @SerializedName("last_edited_by") val lastEditedBy: UserResponse? = null,
    @SerializedName("cover") val cover: CoverResponse? = null,
    @SerializedName("icon") val icon: Any? = null,
    @SerializedName("parent") val parent: ParentResponse? = null,
    @SerializedName("archived") val archived: Boolean? = null,
    @SerializedName("in_trash") val inTrash: Boolean? = null,
    @SerializedName("properties") val properties: Map<String, PropertyData>? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("public_url") val publicUrl: Any? = null,
) {

    val name: String
        get() = properties
            ?.get("Name")
            ?.title
            ?.firstOrNull()
            ?.text
            ?.content
            .orEmpty()

    val example: String
        get() = properties
            ?.get("Example")
            ?.richText
            ?.firstOrNull()
            ?.text
            ?.content
            .orEmpty()

    val explanation: String
        get() = properties
            ?.get("Explanation")
            ?.richText
            ?.firstOrNull()
            ?.text
            ?.content
            .orEmpty()

    val urlCover: String?
        get() = cover?.external?.url


    fun getKnowLevel(level: Int): Pair<Int, Boolean?> {
        val property = properties?.get("Know Level $level")
        return level to property?.checkbox
    }
}