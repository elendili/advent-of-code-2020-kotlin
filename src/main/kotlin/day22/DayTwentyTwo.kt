package day22

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File


open class DayTwentyTwo(val string: String) {
    internal val deck1: ArrayDeque<Byte>
    internal val deck2: ArrayDeque<Byte>

    init {
        val l = string.split(Regex("Player.*"))
            .filter { it.isNotBlank() }
            .map { playerData ->
                playerData.split('\n').filter { it.isNotBlank() }.map { it.toByte() }.toCollection(ArrayDeque<Byte>())
            }
        deck1 = l.first()
        deck2 = l.last()
    }

    fun game(deck1: ArrayDeque<Byte>, deck2: ArrayDeque<Byte>, gameNumber: Int): ArrayDeque<Byte>? {
        var winner: ArrayDeque<Byte>?;
        do {
            val c1 = deck1.removeFirst()!!
            val c2 = deck2.removeFirst()!!
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

    fun partOne(): Long {
        val winner: ArrayDeque<Byte>?
        winner = game(deck1, deck2, 1)!!
        println("winner: $winner")
        return winner.asReversed().mapIndexed { index, e ->
            e.toLong() * (index + 1)
        }.sum()
    }

    fun partTwo(): Long {
        val winner: ArrayDeque<Byte>?
        winner = Recursive(deck1, deck2).game()!!
        println("winner: $winner")
        return winner.asReversed().mapIndexed { index, e ->
            e.toLong() * (index + 1)
        }.sum()
    }
}

class Recursive(val deck1: ArrayDeque<Byte>, val deck2: ArrayDeque<Byte>) {
    private val snapshots: MutableSet<Long> = HashSet()
    private fun findStateInHistoryOrSave(deck1: ArrayDeque<Byte>, deck2: ArrayDeque<Byte>): Boolean {
        val state = (deck1.hashCode().toLong() shl 32) + deck2.hashCode().toLong()
        return if (snapshots.contains(state)) {
            true
        } else {
            snapshots.add(state)
            false
        }
    }

    fun game(): ArrayDeque<Byte>? {
        var winner: ArrayDeque<Byte>?;
        do {
            if (findStateInHistoryOrSave(deck1, deck2)) {
                return deck1
            }
            val c1 = deck1.removeFirst()!!
            val c2 = deck2.removeFirst()!!

            // recurse here
            if (c1 <= deck1.size && c2 <= deck2.size) {
                winner = Recursive(
                    deck1.take(c1.toInt()).toCollection(ArrayDeque()),
                    deck2.take(c2.toInt()).toCollection(ArrayDeque())
                ).game()
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
        assertEquals(306, DayTwentyTwo(sample).partOne())
    }

    @Test
    fun test2Check() {
        assertEquals(291, DayTwentyTwo(sample).partTwo())
    }

    @Test
    fun test2InfiniteCheck() {
        assertEquals(
            105, DayTwentyTwo(
                """Player 1:
43
19

Player 2:
2
29
14"""
            ).partTwo()
        )
    }

}

class ResultSake {
    @Test
    fun result1() {
        val executor = DayTwentyTwo(File("src/main/kotlin/day22/input.txt").readText())
        assertEquals(35299, executor.partOne())
    }

    @Test
    fun result2() {
        val executor = DayTwentyTwo(File("src/main/kotlin/day22/input.txt").readText())
        assertEquals(35069, executor.partTwo()) // incorrect
    }

}

/*
Player 1's deck: 9, 2, 6, 3, 1
Player 2's deck: 5, 8, 4, 7, 10
Player 1's deck:
Player 2's deck: 7, 5, 6, 2, 4, 1, 10, 8, 9, 3

 */