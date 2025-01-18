package integrations.testdata.greek

import org.danceofvalkyries.app.data.notion.pages.NotionPageFlashCard

class RestfulGreekLetter2FlashCard(
    notionDbId: String
) : NotionPageFlashCard {

    override val id: String
        get() = "greek_letter_id_2"

    override val coverUrl: String?
        get() = null

    override val notionDbID: String = notionDbId

    override val name: String
        get() = "greek_letter_id_2"

    override val example: String?
        get() = null

    override val explanation: String?
        get() = "v as in vet"

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

    override fun setKnowLevels(knowLevels: Map<Int, Boolean>) = Unit
}