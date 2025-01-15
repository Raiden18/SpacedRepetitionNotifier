package notion.impl.client.models.response.properties

import com.google.gson.annotations.SerializedName
import notion.impl.client.models.response.properties.TextContentResponse

data class RichTextResponse(
    @SerializedName("text") val text: TextContentResponse
)