package day2

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

/*
how: iterate over list of strings and add to hashmap int values
 */

private const val goal = 2020

abstract class DayTwo {
    fun process(strings: List<String>): Int {
        var out = 0;
        strings.forEach {
            val a = it.split(" ")
                .flatMap { it2 ->
                    it2.split(Regex("\\W"), 0).filter { it3 ->
                        it3.isNotEmpty()
                    }
                }
            val firstNumber = Integer.valueOf(a[0])
            val secondNumber = Integer.valueOf(a[1])
            val letter = a[2].toCharArray()[0]
            val password = a[3]
            out += (if (isValid(firstNumber, secondNumber, letter, password)) 1 else 0)
        }
        return out
    }

    fun process(string: String): Int {
        return process(string.split("\n"))
    }

    abstract fun isValid(firstNumber: Int, secondNumber: Int, letter: Char, password: String): Boolean
}

class DayTwoPartOne : DayTwo() {
    override fun isValid(min: Int, max: Int, letter: Char, password: String): Boolean {
        val count = password.count { z -> z == letter }
        return count in min..max
    }
}

class DayTwoPartTwo : DayTwo() {
    override fun isValid(firstNumber: Int, secondNumber: Int, letter: Char, password: String): Boolean {
        return (password[firstNumber-1] == letter).xor(password[secondNumber-1] == letter)
    }
}


class TestSake {
    @Test
    fun testOnProvidedSamples() {
        // In this list, the two entries that sum to 2020 are 1721 and 299.
        // Multiplying them together produces 1721 * 299 = 514579, so the correct answer is 514579.
        val sample = "1-3 a: abcde\n1-3 b: cdefg\n2-9 c: ccccccccc"
        assertEquals(2, DayTwoPartOne().process(sample))
        assertEquals(1, DayTwoPartTwo().process(sample))
    }
}

class ResultSake {
    @Test
    fun result() {
        val lines = File("src/main/kotlin/day2/input.txt").readLines()
        assertEquals(396, DayTwoPartOne().process(lines))
        assertEquals(428, DayTwoPartTwo().process(lines))
    }
}
