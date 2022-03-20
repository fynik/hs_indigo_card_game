package indigo

import kotlin.random.Random

class Deck {
    private val deckInPlay = mutableListOf<Card>()
    private val takenCards = mutableListOf<Card>()

    init {
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                deckInPlay.add(Card(rank, suit))
            }
        }
    }

    fun resetDeck() {
        deckInPlay.clear()
        takenCards.clear()
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                deckInPlay.add(Card(rank, suit))
            }
        }
        println("Card deck is reset.")
    }

    fun shuffleDeck() {
        for (i in 0..100) {
            val card1 = Random.nextInt(52)
            val card2 = Random.nextInt(52)

            val temp = deckInPlay[card1]
            deckInPlay[card1] = deckInPlay[card2]
            deckInPlay[card2] = temp
        }
    }

    fun getTopCard(): Card {
        val card = deckInPlay.last()
        takenCards.add(card)
        deckInPlay.removeLast()
        return card
    }

    fun isEmpty(): Boolean {
        return deckInPlay.isEmpty()
    }
}