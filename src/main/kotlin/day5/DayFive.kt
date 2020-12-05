package day5

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.pow

class DayFive {

    fun findMaxSeatId(strings: List<String>): Int {
        return strings.map { getSeatId(it) }.maxOf { it }
    }

    fun findMissingSeatId(strings: List<String>): Int {
        var list :List<Int> = strings.map { getSeatId(it) }.sorted()
        for (i in 1..strings.size){
            if(list[i]-list[i-1]==2){
                return list[i]-1
            }
        }
        return -1;
    }

    fun getSeatId(s: String): Int {
        val row = getNumber(s.substring(0, 7), 'F')
        val column = getNumber(s.substring(7), 'L')
        return row * 8 + column;
    }

    fun getNumber(s: String, leftChar: Char): Int {
        var left = 0
        var right: Int = 2.0.pow(s.length).toInt()
        var i = 0;
        while (left < right && i < s.length) {
            val mid = left + (right - left) / 2
            if (s[i++] == leftChar) {
                right = mid
            } else {
                left = mid
            }
        }
        return left;
    }


}


class TestSake1 {
    @Test
    fun testOnProvidedSamples() {
        assertEquals(44, DayFive().getNumber("FBFBBFF", 'F'))
        assertEquals(5, DayFive().getNumber("RLR", 'L'))
        assertEquals(357, DayFive().getSeatId("FBFBBFFRLR"))
        //  edges
        assertEquals(0, DayFive().getSeatId("FFFFFFFLLL"))
        assertEquals(128 * 8 - 1, DayFive().getSeatId("BBBBBBBRRR"))
        // samples
        assertEquals(567, DayFive().getSeatId("BFFFBBFRRR"))
        assertEquals(119, DayFive().getSeatId("FFFBBBFRRR"))
        assertEquals(820, DayFive().getSeatId("BBFFBBFRLL"))
    }
}

class ResultSake {
    @Test
    fun result() {
        val input = File("src/main/kotlin/day5/input.txt").readLines()
        assertEquals(987, DayFive().findMaxSeatId(input))
        assertEquals(603, DayFive().findMissingSeatId(input))
    }
}
