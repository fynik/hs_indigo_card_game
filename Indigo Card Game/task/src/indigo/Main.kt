package indigo

const val PLAYER = 0
const val COMPUTER = 1
val deck = Deck()
val playerHand = mutableListOf<Card>()
val compHand = mutableListOf<Card>()
val cardsOnTable = mutableListOf<Card>()
val playerCardsWon = mutableListOf<Card>()
val compCardsWon = mutableListOf<Card>()
var playerPoints = 0
var compPoints = 0
var lastWon = 0
val pointRanks: List<Rank> = listOf(Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING, Rank.ACE)


fun main() {

    println("Indigo Card Game")

    //Shuffle deck
    deck.shuffleDeck()

    //Who's first to play
    val firstPlayer = chooseFirstPlayer()
    var currentPlayer = firstPlayer
    lastWon = firstPlayer //If no one won cards, they are going to first player

    //Initial 4 cards on table
    for (i in 1..4) cardsOnTable.add(deck.getTopCard())
    println("Initial cards on the table: ${cardsOnTable.joinToString(" ")}")

    //Game cycle
    while (true) {

        //Current cards on table
        println(if (cardsOnTable.isEmpty()) "No cards on the table" else
            "\n${cardsOnTable.size} cards on the table, and the top card is ${cardsOnTable.last()}")

        //Game over check and exit
        if (deck.isEmpty() && playerHand.isEmpty() && compHand.isEmpty()) {
            if (cardsOnTable.isNotEmpty()) takeCardsFromTable(lastWon) //remaining cards to last player who won the table

            //3 points to max cards won
            if (playerCardsWon.size > compCardsWon.size) {
                playerPoints += 3
            } else if (playerCardsWon.size < compCardsWon.size) {
                compPoints += 3
            } else {
                if (firstPlayer == PLAYER) playerPoints += 3 else compPoints += 3
            }

            printScore()
            println("Game Over")
            break
        }

        //Player turn logic
        if (currentPlayer == PLAYER) {
            var exit = false
            var cardIdx = 0

            //Check hand and give cards if empty
            if (playerHand.isEmpty()) {
                for (i in 1..6) playerHand.add(deck.getTopCard())
            }

            //Hand output
            print("Cards in hand: ")
            for (i in playerHand.indices) print("${i+1})${playerHand[i]} ")
            print("\n")

            //Player Input
            while (true) {
                println("Choose a card to play (1-${playerHand.size}):")
                val input = readln()
                if (input == "exit") {
                    exit = true
                    break
                }
                try {
                    cardIdx = input.toInt() - 1
                } catch (e: NumberFormatException) {
                    continue
                }

                if (cardIdx in 0..playerHand.lastIndex) break
            }

            if (exit) {
                println("Game Over")
                break
            }

            val playedCard = playerHand[cardIdx]
            playerHand.removeAt(cardIdx)
            cardsOnTable.add(playedCard)

            if (cardsOnTable.size > 1) checkForWinCardsOnTable(playedCard, currentPlayer)
        }

        //Comp turn logic
        if (currentPlayer == COMPUTER) {
            //Check hand and give cards if empty
            if (compHand.isEmpty()) {
                for (i in 1..6) compHand.add(deck.getTopCard())
            }


            //Print computer hand
            for (c in compHand) print("$c ")
            print("\n")

            val cardIdx = compMoveLogic()
            val playedCard = compHand[cardIdx]
            compHand.removeAt(cardIdx)
            cardsOnTable.add(playedCard)

            println("Computer plays $playedCard")

            if (cardsOnTable.size > 1) checkForWinCardsOnTable(playedCard, currentPlayer)
        }

        currentPlayer = if (currentPlayer == PLAYER) COMPUTER else PLAYER
    }
}

fun compMoveLogic(): Int {
    val isCardsOnTable = cardsOnTable.isNotEmpty()
    var candidateCardIdx = -1

    if (isCardsOnTable) {
        val tableCard = cardsOnTable.last()
        val candidateCardList = compHand.filter { it.rank == tableCard.rank || it.suit == tableCard.suit}.toList()

        candidateCardIdx = when {
            candidateCardList.isEmpty() -> -1
            candidateCardList.size == 1 -> compHand.indexOf(candidateCardList.first())
            else -> {
                when {
                    candidateCardList.count { it.suit == tableCard.suit } > 1 ->
                        compHand.indexOf(candidateCardList.first { it.suit == tableCard.suit })
                    candidateCardList.count { it.rank == tableCard.rank } > 1 ->
                        compHand.indexOf(candidateCardList.first { it.rank == tableCard.rank })
                    else -> compHand.indexOf(candidateCardList.first())
                }
            }
        }
    }


    if (!isCardsOnTable || candidateCardIdx == -1) {
        val moreThanOneSuits = compHand.groupingBy { it.suit }.eachCount().toMap().filter { it.value > 1 }.keys
        val moreThanOneRanks = compHand.groupingBy { it.rank }.eachCount().toMap().filter { it.value > 1 }.keys
        candidateCardIdx = when {
            moreThanOneSuits.isNotEmpty() -> compHand.indexOf(compHand.first { it.suit == moreThanOneSuits.first() })
            moreThanOneRanks.isNotEmpty() -> compHand.indexOf(compHand.first { it.rank == moreThanOneRanks.first() })
            else -> -1
        }
    }

    return when {
        compHand.size == 1 -> 0
        candidateCardIdx in 0..compHand.size -> candidateCardIdx
        else -> compHand.lastIndex

    }
}

fun checkForWinCardsOnTable(playedCard: Card, currentPlayer: Int) {
    val tableCard = cardsOnTable[cardsOnTable.lastIndex - 1]
    if (playedCard.rank == tableCard.rank || playedCard.suit == tableCard.suit) {
        val playerName = if (currentPlayer == PLAYER) "Player" else "Computer"
        takeCardsFromTable(currentPlayer)
        println("$playerName wins cards")
        printScore()
        println()
    }
}

fun chooseFirstPlayer(): Int {
    while (true) {
        println("Play first?")
        val answer = readln()
        return when (answer.lowercase()) {
            "yes" -> PLAYER
            "no" -> COMPUTER
            else -> continue
        }
    }
}

fun printScore() {
    println("Score: Player $playerPoints - Computer $compPoints")
    println("Cards: Player ${playerCardsWon.size} - Computer ${compCardsWon.size}")
}

fun takeCardsFromTable(currentPlayer: Int) {
    if (currentPlayer == PLAYER) {
        for (card in cardsOnTable) {
            playerCardsWon.add(card)
            if (card.rank in pointRanks) playerPoints++
        }
    } else {
        for (card in cardsOnTable) {
            compCardsWon.add(card)
            if (card.rank in pointRanks) compPoints++
        }
    }
    cardsOnTable.clear()
    lastWon = currentPlayer
}

