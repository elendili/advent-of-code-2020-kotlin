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
    val cups: MutableMap<Int, CircleNode> = mutableMapOf()
    val head: CircleNode
    val tail: CircleNode

    init {
        val cupValues = string.toCharArray().map {
            it.toString().toInt()
        }.toList()

        val headValue = cupValues.first()
        head = CircleNode(headValue)
        cups[headValue] = head
        var localMaxCup: Int = headValue
        var localMinCup: Int = headValue
        var localTail = head

        cupValues.drop(1).forEach {
            val circleNode = CircleNode(it)
            circleNode.prev = localTail
            localTail.next = circleNode

            localMaxCup = Math.max(localMaxCup, it)
            localMinCup = Math.min(localMinCup, it)

            cups[it] = circleNode

            localTail = circleNode
        }

        // add more elements for part two
        for (i in localMaxCup + 1..countTowards) {
            val circleNode = CircleNode(i)
            circleNode.prev = localTail
            localTail.next = circleNode
            localTail = circleNode
            localMaxCup = i
            cups[i] = circleNode
        }
        localTail.next = head
        head.prev = localTail
        minCup = localMinCup
        maxCup = localMaxCup
        tail = localTail
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
            val destCup = cups.get(destCupValue)!!

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

    fun partOne(rounds: Int): String {
        game(rounds)
        // gather Output for round 1
        var current = cups.get(1)!!.next!!
        var out = ""
        while (current.value != 1) {
            out += current.value
            current = current.next!!
        }
        return out
    }

    fun partTwo(rounds: Int): Long {
        game(rounds)
        // gather Output for round 1
        val firstAfterOne = cups.get(1)!!.next!!
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
        assertEquals(
            149245887792,
            DayTwentyThree("389125467", 1_000_000).partTwo(10_000_000)
        )
    }

}

class ResultSake {
    @Test
    fun result1() {
        val executor = DayTwentyThree("318946572")
        assertEquals("52864379", executor.partOne(100))
    }

    @Test
    fun result2() {
        assertEquals(
            11591415792,
            DayTwentyThree("318946572", 1_000_000).partTwo(10_000_000)
        )
    }

}
