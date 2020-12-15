package day15

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

data class DayFifteen(val string: String) {
    class NumbersHistory {
        private val track: MutableMap<Int, IntArray> = mutableMapOf()
        fun add(element: Int, index: Int) {
            track.compute(element) { _, v ->
                val array = v ?: IntArray(2)
                array[0] = array[1]
                array[1] = index
                array
            }
        }

        fun difference(last: Int): Int {
            val a = track[last]!!
            return if (a[0] == 0) {
                0
            } else {
                a[1] - a[0]
            }
        }

        fun size(): Int {
            return track.size
        }
    }

    private val numbers: List<Int> = string.split(',').map { it.toInt() }
    fun partTwo(finalTurn: Int = 30_000_000): Int {
        return partOne(finalTurn)
    }

    fun partOne(finalTurn: Int = 2020): Int {
        var turn = 1
        val history = NumbersHistory()
        // prepare
        numbers.forEach { e -> history.add(e, turn++) }
        var lastDiff = numbers.last() // last said word and difference with previous appearance at the same time
        while (turn <= finalTurn) {
            lastDiff = history.difference(lastDiff)
            history.add(lastDiff, turn)
            turn++
        }
        println(history.size())
        return lastDiff
    }

}

class TestSake {

    @Test
    fun testOnProvidedSamples1() {
        assertEquals(0, DayFifteen("0,3,6").partOne(4))
        assertEquals(3, DayFifteen("0,3,6").partOne(5))
        assertEquals(3, DayFifteen("0,3,6").partOne(6))
        assertEquals(1, DayFifteen("0,3,6").partOne(7))
        assertEquals(0, DayFifteen("0,3,6").partOne(8))
        assertEquals(4, DayFifteen("0,3,6").partOne(9))
        assertEquals(0, DayFifteen("0,3,6").partOne(10))
        assertEquals(436, DayFifteen("0,3,6").partOne())
    }

    @Test
    fun testOnProvidedSamples2() {
        assertEquals(1, DayFifteen("1,3,2").partOne())
        assertEquals(10, DayFifteen("2,1,3").partOne())
        assertEquals(27, DayFifteen("1,2,3").partOne())
        assertEquals(78, DayFifteen("2,3,1").partOne())
        assertEquals(438, DayFifteen("3,2,1").partOne())
        assertEquals(1836, DayFifteen("3,1,2").partOne())
    }

    @Test
    fun testOnProvidedSamplesPartTwo() {
        assertEquals(175594, DayFifteen("0,3,6").partTwo())
        assertEquals(2578, DayFifteen("1,3,2").partTwo())
        assertEquals(3544142, DayFifteen("2,1,3").partTwo())
        assertEquals(261214, DayFifteen("1,2,3").partTwo())
        assertEquals(6895259, DayFifteen("2,3,1").partTwo())
        assertEquals(18, DayFifteen("3,2,1").partTwo())
        assertEquals(362, DayFifteen("3,1,2").partTwo())
    }

}

class ResultSake {
    @Test
    fun result1() {
        val executor = DayFifteen(File("src/main/kotlin/day15/input.txt").readText())
        assertEquals(410, executor.partOne())
    }

    @Test
    fun result2() {
        val executor = DayFifteen(File("src/main/kotlin/day15/input.txt").readText())
        assertEquals(238, executor.partTwo())
    }
}