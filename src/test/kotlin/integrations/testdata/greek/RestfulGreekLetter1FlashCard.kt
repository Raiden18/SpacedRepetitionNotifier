package integrations.testdata.greek

import org.danceofvalkyries.notion.pages.NotionPageFlashCard

class RestfulGreekLetter1FlashCard(
    private val notionDbId: String
) : NotionPageFlashCard {

    override suspend fun getId(): String {
        return "greek_letter_id_1"
    }

    override suspend fun getCoverUrl(): String? {
        return null
    }

    override suspend fun getNotionDbId(): String {
        return notionDbId
    }

    override suspend fun getName(): String {
        return "Αα"
    }

    override suspend fun getExample(): String? {
        return null
    }

    override suspend fun getExplanation(): String? {
        return "a as in raft"
    }

    override suspend fun getKnowLevels(): Map<Int, Boolean> {
        return mapOf(
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
    }

    override suspend fun setKnowLevels(knowLevels: Map<Int, Boolean>) = Unit
}