package day23

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

fun List<CircleNode>.toString() {
    this.map { it.value }.joinToString("")
}

data class CircleNode(val value: Int, var next: CircleNode? = null, var prev: CircleNode? = null) {
    override fun toString(): String {
        return prev?.value.toString().orEmpty() + "<-" + value + "->" + next?.value.toString().orEmpty()
    }
}

class DayTwentyThree(val string: String, countTowards: Int = 0) {
    private val minCup: Int
    private val maxCup: Int
    val cups: MutableList<CircleNode>
    val head: CircleNode

    init {
        cups = string.toCharArray().map {
            CircleNode(it.toString().toInt())
        }.toList().toMutableList()

        var localMaxCup: Int = -1
        for (i in cups.indices) {
            if (i + 1 < cups.size) {
                cups[i].next = cups[i + 1]
            }
            if (i > 0) {
                cups[i].prev = cups[i - 1]
            }
            localMaxCup = Math.max(localMaxCup, cups[i].value)
        }
        head = cups[0]
        minCup = cups.map { it.value }.minOrNull()!!

        for (i in localMaxCup + 1..countTowards) {
            val newNode = CircleNode(i)
            newNode.prev = cups.last()
            cups.last().next = newNode
            cups.add(newNode)
            localMaxCup = i
        }
        cups.last().next = cups.first()
        cups.first().prev = cups.last()
        maxCup = localMaxCup
    }

    fun game(number: Int) {
        var currentCup: CircleNode = head
        // moves
        repeat(number) { counter ->
            // cut by miracle
            var currentInSlice = currentCup.next!!
            currentInSlice.prev = null
            val slice: MutableList<CircleNode> = mutableListOf()
            repeat(3) {
                slice.add(currentInSlice)
                currentInSlice = currentInSlice.next!!
            }
            // merge current with the end of gap
            currentCup.next = currentInSlice

            val sliceValues = slice.map { it.value }

            // find destination
            var destCupValue = currentCup.value - 1
            while (destCupValue !in minCup..maxCup || destCupValue in sliceValues) {
                if (destCupValue < minCup) {
                    destCupValue = maxCup
                } else {
                    destCupValue -= 1
                }
            }
            val destCup = cups.firstOrNull { e ->
                e.value == destCupValue
            }!!

            // insert what was cut
            val targetOfSlice = destCup.next!!
            destCup.next = slice.first()
            slice.first().prev = destCup.next

            slice.last().next = targetOfSlice
            targetOfSlice.prev = slice.last()

            // move pointer
            currentCup = currentCup.next!!
        }
    }

    fun partOne(number: Int): String {
        game(number)
        // gather Output for round 1
        var current = cups.first { it.value == 1 }.next!!
        var out = ""
        while (current.value != 1) {
            out += current.value
            current = current.next!!
        }
        return out
    }

    fun partTwo(number: Int): Long {
        game(number)
        // gather Output for round 1
        val firstAfterOne = cups.first { it.value == 1 }.next!!
        val multiplied = firstAfterOne.value.toLong() * firstAfterOne.next!!.value.toLong()
        return multiplied
    }

}

class TestSake1 {

    @Test
    fun test1() {
        assertEquals("92658374", DayTwentyThree("389125467").partOne(10))
        assertEquals("67384529", DayTwentyThree("389125467").partOne(100))
    }

}
class TestSake2 {

    @Test
    fun testFor2() {
        assertEquals("67384529", DayTwentyThree("38125467", 9).partTwo(100))
    }

}

class ResultSake {
    @Test
    fun result1() {
        val executor = DayTwentyThree("318946572")
        assertEquals("52864379", executor.partOne(100))
    }

}
