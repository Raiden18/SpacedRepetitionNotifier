package org.danceofvalkyries.utils.resources

class EngStringResources : StringResources {

    override fun getJob(): String {
        return "Good Job!"
    }

    override fun everythingIsRevised(): String {
        return "Everything is revised!"
    }

    override fun flashCardsToRevise(count: Int): String {
        return "You have $count flashcards to revise"
    }

    override fun choose(): String {
        return "Choose:"
    }

    override fun forgot(): String {
        return "Forgot"
    }

    override fun recalled(): String {
        return "Recalled"
    }

    override fun lookUp(): String {
        return "Look it up"
    }
}