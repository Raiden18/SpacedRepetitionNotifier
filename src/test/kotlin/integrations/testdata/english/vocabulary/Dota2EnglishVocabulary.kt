package integrations.testdata.english.vocabulary

import org.danceofvalkyries.notion.pages.NotionPageFlashCard


class Dota2EnglishVocabulary(
    private val notionDbId: String,
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
        3 to true,
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

    override suspend fun getId(): String {
        return "dota_2_english_vocabulary_1"
    }


    override suspend fun getCoverUrl(): String? {
        return "https://cdn.akamai.steamstatic.com/apps/dota2/images/dota2_social.jpg"
    }

    override suspend fun getNotionDbId(): String {
        return notionDbId
    }

    override suspend fun getName(): String {
        return "Dota 2"
    }

    override suspend fun getExample(): String? {
        return "Dota 2 is the best game in the world"
    }

    override suspend fun getExplanation(): String {
        return "Mid or feed"
    }

    override suspend fun getKnowLevels(): Map<Int, Boolean> {
        return mapOf(
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
    }

    override suspend fun setKnowLevels(knowLevels: Map<Int, Boolean>) {
        updatedKnowLevels = knowLevels
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Dota2EnglishVocabulary

        return notionDbId == other.notionDbId
    }

    override fun hashCode(): Int {
        return notionDbId.hashCode()
    }
}