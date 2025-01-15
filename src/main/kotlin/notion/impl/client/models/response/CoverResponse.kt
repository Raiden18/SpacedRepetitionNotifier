package notion.impl.client.models.response

import com.google.gson.annotations.SerializedName

data class CoverResponse(
    @SerializedName("external")
    val external: CoverBody?
)

data class CoverBody(
    @SerializedName("url")
    val url: String?
)