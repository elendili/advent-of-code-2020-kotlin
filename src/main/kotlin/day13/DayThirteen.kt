package day13

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class DayThirteen(private val earliestTime: Int, private val buses: List<Int>) {
    constructor(strings: String) : this(strings.split('\n'))
    constructor(strings: List<String>) :
            this(strings.first().toIntOrNull() ?: -1,
                strings.last().split(',').map { it.toIntOrNull() }.map { it ?: -1 })

    fun partOne(): Int {
        var earliestBusAndTimeToDeparture = buses
            .filter { it > 0 }
            .map { it to ((earliestTime / it) * it + it - earliestTime) } //  59*(939/59) + 59 - 939 = 5 minute
            .minByOrNull { p -> p.second }!!
        return earliestBusAndTimeToDeparture.first * earliestBusAndTimeToDeparture.second
    }

    fun partTwo(): Long {
        val busNumberAndIndexList = buses
            .map { it.toLong() }
            .mapIndexed { i, b -> b to i }
            .filter { it.first > -1 }
        var increment = 1L
        var time = 1L
        busNumberAndIndexList.forEach { (curBusValue, curBusShift) ->
            while ((time + curBusShift).rem(curBusValue) != 0L) {
                time += increment
            }
            // as soon as we reached time which satisfies current bus number
            // we can multiply the incrementer by current bus value
            // and further skip gaps of size increment*busValue
            increment *= curBusValue
        }
        return time
    }

}

class TestSake {
    private val input1 = """939
7,13,x,x,59,x,31,19""".split('\n')

    @Test
    fun testOnProvidedSamplesBase() {
        assertEquals(295, DayThirteen(input1).partOne())
    }

    @Test
    fun testOnProvidedSamplesBasePArtTwo1() {
        assertEquals(1068781, DayThirteen(input1).partTwo())
    }

    @Test
    fun testOnProvidedSamplesBasePArtTwo2() {
        assertEquals(17, DayThirteen("17").partTwo())
    }

    @Test
    fun testOnProvidedSamplesBasePArtTwo4() {
        assertEquals(54, DayThirteen("3,5,7").partTwo())
        assertEquals(51, DayThirteen("17,13").partTwo())
        assertEquals(34, DayThirteen("17,5").partTwo())
        assertEquals(36, DayThirteen("3,x,19").partTwo())
    }

    @Test
    fun testOnProvidedSamplesBasePArtTwo3() {
        assertEquals(3417, DayThirteen("17,x,13,19").partTwo())
        assertEquals(754018, DayThirteen("67,7,59,61").partTwo())
        assertEquals(779210, DayThirteen("67,x,7,59,61").partTwo())
        assertEquals(1261476, DayThirteen("67,7,x,59,61").partTwo())
        assertEquals(1202161486, DayThirteen("1789,37,47,1889").partTwo())
    }

}

class ResultSake {
    @Test
    fun result1() {
        val executor = DayThirteen(File("src/main/kotlin/day13/input.txt").readLines())
        assertEquals(3215, executor.partOne())
    }

    @Test
    fun result2() {
        val executor = DayThirteen(File("src/main/kotlin/day13/input.txt").readLines())
        assertEquals(1001569619313439, executor.partTwo())
    }
}