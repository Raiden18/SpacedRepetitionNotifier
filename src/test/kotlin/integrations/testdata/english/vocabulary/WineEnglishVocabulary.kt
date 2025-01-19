package integrations.testdata.english.vocabulary

import org.danceofvalkyries.job.data.notion.pages.NotionPageFlashCard

class WineEnglishVocabulary(
    private val notionDbId: String
) : NotionPageFlashCard {

    var updatedKnowLevels: Map<Int, Boolean> = mapOf<Int, Boolean>()

    val forgottenLevelKnowLevels = mapOf(
        1 to false,
        2 to false,
        3 to false,
        4 to false,
        5 to false,
        6 to false,
        7 to false,
        8 to false,
        9 to false,
        10 to false,
        11 to false,
        12 to false,
        13 to false,
    )

    val nextLevelKnowLevels = mapOf(
        1 to true,
        2 to true,
        3 to false,
        4 to false,
        5 to false,
        6 to false,
        7 to false,
        8 to false,
        9 to false,
        10 to false,
        11 to false,
        12 to false,
        13 to false,
    )

    override val id: String
        get() = "wine_english_vocabulary_1"

    override val coverUrl: String?
        get() = null

    override val notionDbID: String
        get() = notionDbId

    override val name: String
        get() = "Wine"

    override val example: String?
        get() = "I do not like wine"

    override val explanation: String?
        get() = "alcoholic beverage made of grapes"

    override val knowLevels: Map<Int, Boolean>
        get() = mapOf(
            1 to true,
            2 to false,
            3 to false,
            4 to false,
            5 to false,
            6 to false,
            7 to false,
            8 to false,
            9 to false,
            10 to false,
            11 to false,
            12 to false,
            13 to false,
        )

    override fun setKnowLevels(knowLevels: Map<Int, Boolean>) {
        updatedKnowLevels = knowLevels
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WineEnglishVocabulary

        if (notionDbId != other.notionDbId) return false
        if (id != other.id) return false
        if (coverUrl != other.coverUrl) return false
        if (notionDbID != other.notionDbID) return false
        if (name != other.name) return false
        if (example != other.example) return false
        if (explanation != other.explanation) return false
        if (knowLevels != other.knowLevels) return false

        return true
    }

    override fun hashCode(): Int {
        var result = notionDbId.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + (coverUrl?.hashCode() ?: 0)
        result = 31 * result + notionDbID.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (example?.hashCode() ?: 0)
        result = 31 * result + (explanation?.hashCode() ?: 0)
        result = 31 * result + knowLevels.hashCode()
        return result
    }


}