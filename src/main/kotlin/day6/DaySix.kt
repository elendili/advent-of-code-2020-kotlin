package day6

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

abstract class DaySix {
    fun process(string: String): Int {
        return process(string.split("\n\n"))
    }

    private fun process(strings: List<String>): Int {
        return strings.map { processGroup(it) }.sum()
    }

    abstract fun processGroup(string: String): Int
}

class DaySixPartOne : DaySix() {
    override fun processGroup(string: String): Int {
        return string.toCharArray().filter { it in 'a'..'z' }.distinct().count()
    }
}

class DaySixPartTwo : DaySix() {
    override fun processGroup(string: String): Int {
        val persons = string.split("\n")
        val count = persons.size
        var everyOneAnswered = persons.flatMap { it.toCharArray().asIterable() }
            .groupingBy { it }.eachCount().map { it.value }.filter { it==count }.count()
        return everyOneAnswered
    }
}

class TestSake {
    @Test
    fun testOnProvidedSamples() {
        val input = """abc

a
b
c

ab
ac

a
a
a
a

b"""
        assertEquals(11, DaySixPartOne().process(input))
        assertEquals(6, DaySixPartTwo().process(input))
    }
}


class ResultSake {
    @Test
    fun result() {
        val input = File("src/main/kotlin/day6/input.txt").readText()
        assertEquals(6273, DaySixPartOne().process(input))
        assertEquals(3254, DaySixPartTwo().process(input))
    }
}
