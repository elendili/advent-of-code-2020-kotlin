package day22

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import kotlin.collections.HashSet

typealias Deck = LinkedList<Byte>

fun Deck.copy(count: Byte = this.size.toByte()): Deck {
    return Deck(this.subList(0, count.toInt()).toList())
}

open class DayTwentyTwoPartOne(val string: String) {
    internal val deck1: Deck
    internal val deck2: Deck

    init {
        val l = string.split(Regex("Player.*"))
            .filter { it.isNotBlank() }
            .map { playerData ->
                playerData.split('\n').filter { it.isNotBlank() }.map { it.toByte() }.toCollection(Deck())
            }
        deck1 = l.first()
        deck2 = l.last()
    }

    open fun game(deck1: Deck, deck2: Deck, gameNumber: Int): Deck? {
        var winner: Deck?;
        do {
            val c1 = deck1.poll()!!
            val c2 = deck2.poll()!!
            if (c1 > c2) {
                deck1 += c1
                deck1 += c2
            } else {
                deck2 += c2
                deck2 += c1
            }
            if (deck2.isEmpty()) {
                winner = deck1
            } else if (deck1.isEmpty()) {
                winner = deck2
            } else {
                winner = null
            }
        } while (winner == null);
        return winner
    }

    fun execute(): Long {
        val winner: Deck?
        val deck1 = deck1.copy()
        val deck2 = deck2.copy()
        winner = game(deck1, deck2, 1)!!
        println("winner: $winner")
        return winner.asReversed().mapIndexed { index, e ->
            e.toLong() * (index + 1)
        }.sum()
    }
}

class DayTwentyTwoPartTwo(string: String) : DayTwentyTwoPartOne(string) {
    private val snapshots: MutableSet<Long> = HashSet()
    private fun findStateInHistoryOrSave(deck1: Deck, deck2: Deck): Boolean {
        val state = (deck1.hashCode().toLong() shl 32) + deck2.hashCode().toLong()
        return if (snapshots.contains(state)) {
            println("returned because state existed. size of history: ${snapshots.size}")
            true
        } else {
            snapshots.add(state)
            false
        }
    }

    override fun game(deck1: Deck, deck2: Deck, gameNumber: Int): Deck? {
        if (gameNumber > 1) {
            println("game: $gameNumber")
        }
//            println(deck1)
//            println(deck2)
//            println("----")
        var winner: Deck?;
        do {

            if (findStateInHistoryOrSave(deck1, deck2)) {
                return deck1
            }
            val c1 = deck1.poll()!!
            val c2 = deck2.poll()!!

            // recurse here
            if (c1 <= deck1.size && c2 <= deck2.size) {
                winner = game(deck1.copy(c1), deck2.copy(c2), gameNumber + 1)
            } else {
                winner = if (c1 > c2) deck1 else deck2
            }

            if (winner == deck1) {
                deck1 += c1
                deck1 += c2
            } else {
                deck2 += c2
                deck2 += c1
            }

            if (deck2.isEmpty()) {
                winner = deck1
            } else if (deck1.isEmpty()) {
                winner = deck2
            } else {
                winner = null
            }
        } while (winner == null);
        return winner
    }

}

class TestSake {
    val sample = """Player 1:
9
2
6
3
1

Player 2:
5
8
4
7
10"""

    @Test
    fun test1() {
        assertEquals(306, DayTwentyTwoPartOne(sample).execute())
    }

    @Test
    fun test2Check() {
        assertEquals(291, DayTwentyTwoPartTwo(sample).execute())
    }

    @Test
    fun test2InfiniteCheck() {
        assertEquals(
            105, DayTwentyTwoPartTwo(
                """Player 1:
43
19

Player 2:
2
29
14"""
            ).execute()
        )
    }

}

class ResultSake {
    @Test
    fun result1() {
        val executor = DayTwentyTwoPartOne(File("src/main/kotlin/day22/input.txt").readText())
        assertEquals(35299, executor.execute())
    }

    @Test
    fun result2() {
        val executor = DayTwentyTwoPartTwo(File("src/main/kotlin/day22/input.txt").readText())
        assertEquals(34604, executor.execute())
    }

}

/*
Player 1's deck: 9, 2, 6, 3, 1
Player 2's deck: 5, 8, 4, 7, 10
Player 1's deck:
Player 2's deck: 7, 5, 6, 2, 4, 1, 10, 8, 9, 3

 */