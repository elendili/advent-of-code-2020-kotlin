package day22

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import kotlin.collections.ArrayDeque

// Part deux - recursively

class RecursiveCombatGame(playerACardList: List<Int>, playerBCardList: List<Int>) {

    enum class Winner {
        PlayerA,
        PlayerB
    }

    private val playerACards = ArrayDeque<Int>().apply { addAll(playerACardList) }
    private val playerBCards = ArrayDeque<Int>().apply { addAll(playerBCardList) }
    private val previousStates: MutableSet<Int> = mutableSetOf()

    private val winner: Winner?
        get() = when {
            Objects.hash(playerACards, playerBCards) in previousStates -> Winner.PlayerA
            playerACards.isEmpty() -> Winner.PlayerB
            playerBCards.isEmpty() -> Winner.PlayerA
            else -> null
        }

    private val winningDeck: List<Int> get() = if (winner == Winner.PlayerA) playerACards else playerBCards
    val winnerScore: Int get() = winningDeck.asReversed().mapIndexed { i, c -> (i + 1) * c }.sum()

    fun playGame() {
        while (winner == null) {
            playRound()
        }
    }

    fun playRound() {
        previousStates.add(Objects.hash(playerACards, playerBCards))
        val playerACard = playerACards.removeFirst()
        val playerBCard = playerBCards.removeFirst()

        val roundWinner = when {
            playerACards.size >= playerACard && playerBCards.size >= playerBCard ->
                // Play a subgame
                RecursiveCombatGame(
                    playerACards.take(playerACard),
                    playerBCards.take(playerBCard)
                ).run {
                    playGame()
                    winner!!
                }
            playerACard > playerBCard -> Winner.PlayerA
            else -> Winner.PlayerB
        }

        if (roundWinner == Winner.PlayerA) playerACards.run {
            addLast(playerACard)
            addLast(playerBCard)
        } else playerBCards.run {
            addLast(playerBCard)
            addLast(playerACard)
        }
    }
}

fun decks2() {
    val decks = File("src/main/kotlin/day22/input.txt").readText()
        .split("\n\n").map { deck ->
            deck.split("\n").drop(1).map { line -> line.toInt() }
        }

    val game = RecursiveCombatGame(decks[0], decks[1])
    game.playGame()

    println(game.winnerScore)
}

class ForeignSake {

    @Test
    fun result2() {
        decks2()
    }

}