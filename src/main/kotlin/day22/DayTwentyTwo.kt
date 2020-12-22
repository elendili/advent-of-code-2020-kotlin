package day22

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import kotlin.collections.ArrayDeque


open class DayTwentyTwo(val string: String) {
    internal val deck1: ArrayDeque<Int>
    internal val deck2: ArrayDeque<Int>

    init {
        val l = string.split(Regex("Player.*"))
            .filter { it.isNotBlank() }
            .map { playerData ->
                playerData.split('\n').filter { it.isNotBlank() }.map { it.toInt() }.toCollection(ArrayDeque())
            }
        deck1 = l.first()
        deck2 = l.last()
    }

    fun game(deck1: ArrayDeque<Int>, deck2: ArrayDeque<Int>, gameNumber: Int): ArrayDeque<Int>? {
        var winner: ArrayDeque<Int>?;
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
        val winner: ArrayDeque<Int>?
        winner = game(deck1, deck2, 1)!!
        println("winner: $winner")
        return winner.asReversed().mapIndexed { index, e ->
            e.toLong() * (index + 1)
        }.sum()
    }

    fun partTwo(): Long {
        val winner: ArrayDeque<Int>?
        winner = Recursive(deck1, deck2).game()!!
        println("winner: $winner")
        return winner.asReversed().mapIndexed { index, e ->
            e.toLong() * (index + 1)
        }.sum()
    }
}

class Recursive(val list1: List<Int>, val list2: List<Int>) {
    private val snapshots: MutableSet<Int> = mutableSetOf()
    private val deck1 = ArrayDeque<Int>().apply { addAll(list1) }
    private val deck2 = ArrayDeque<Int>().apply { addAll(list2) }

    fun game(): ArrayDeque<Int>? {
        var winner: ArrayDeque<Int>?
        do {
            if (!snapshots.add(Objects.hash(deck1, deck2))) {
                return deck1
            }
            val c1 = deck1.removeFirst()
            val c2 = deck2.removeFirst()

            // recurse here
            winner = if (c1 <= deck1.size && c2 <= deck2.size) {
                Recursive(
                    deck1.take(c1),
                    deck2.take(c2)
                ).game()
            } else {
                if (c1 > c2) deck1 else deck2
            }

            if (winner == deck1) {
                deck1.addLast(c1)
                deck1.addLast(c2)
            } else {
                deck2.addLast(c2)
                deck2.addLast(c1)
            }

            winner = when {
                deck2.isEmpty() -> deck1
                deck1.isEmpty() -> deck2
                else -> null
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
        assertEquals(33266, executor.partTwo()) // incorrect
    }

}

/*
Player 1's deck: 9, 2, 6, 3, 1
Player 2's deck: 5, 8, 4, 7, 10
Player 1's deck:
Player 2's deck: 7, 5, 6, 2, 4, 1, 10, 8, 9, 3

 */