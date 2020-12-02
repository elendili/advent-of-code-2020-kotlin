package day1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

/*
how: iterate over list of strings and add to hashmap int values
 */

private const val goal = 2020

class DayOnePartOne {
    fun find2020InNumbers(numbers: List<Int>): Int {
        val set: MutableSet<Int> = mutableSetOf()
        numbers.forEach {
            val v = goal - it
            if (set.contains(v)) {
                return it * v
            } else {
                set.add(it)
            }
        }
        return 0
    }

    fun find2020(strings: List<String>): Int {
        return find2020InNumbers(strings.map { Integer.valueOf(it) })
    }

}

class DayOnePartTwo {
    fun find2020InNumbers(numbers: List<Int>): Int {
        val set: MutableSet<Int> = mutableSetOf()
        set.addAll(numbers)
        for ((index, a) in numbers.withIndex()) {
            for (j in index + 1..numbers.lastIndex) {
                val b = numbers[j]
                val c = 2020 - (a + b)
                if (set.contains(c)) {
                    return a * b * c
                }
            }
        }
        return 0
    }

    fun find2020(strings: List<String>): Int {
        return find2020InNumbers(strings.map { Integer.valueOf(it) })
    }

}

class ForTest {
    @Test
    fun testOnProvidedSamples() {
        // In this list, the two entries that sum to 2020 are 1721 and 299.
        // Multiplying them together produces 1721 * 299 = 514579, so the correct answer is 514579.
        assertEquals(514579, DayOnePartOne().find2020InNumbers(listOf(1721, 979, 366, 299, 675, 1456)))
        // Using the above example again, the three entries that sum to 2020 are 979, 366, and 675.
        // Multiplying them together produces the answer, 241861950.
        assertEquals(241861950, DayOnePartTwo().find2020InNumbers(listOf(1721, 979, 366, 299, 675, 1456)))
    }
}

class Result {
    @Test
    fun result() {
        assertEquals(955584,DayOnePartOne().find2020(File("src/main/kotlin/day1/input.txt").readLines()))
        assertEquals(287503934, DayOnePartTwo().find2020(File("src/main/kotlin/day1/input.txt").readLines()))
    }
}

/*fun main() {
    // {}::class.java.getResource("/day1/Day1Kt.class")  // <== returns file, to get text file resource need to add it in classpath
}*/
