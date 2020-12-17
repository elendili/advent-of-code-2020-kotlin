package day17

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

data class DaySeventeen(val string: String) {

    fun stringToSpace(string: String, dimensions: Int): Map<List<Int>, Boolean> {
        val out: MutableMap<List<Int>, Boolean> = mutableMapOf()
        string.split('\n')
            .forEachIndexed { y, line ->
                line.toCharArray()
                    .forEachIndexed { x, char ->
                        val coords = mutableListOf(x, y)
                        repeat(dimensions - coords.size) { coords.add(0) }
                        out[coords] = char == '#'
                    }
            }
        return out
    }

    fun partOne(): Int {
        return getActiveCountAfterUpdate(6, 3)
    }

    fun partTwo(): Int {
        return getActiveCountAfterUpdate(6, 4)
    }

    fun getActiveCountAfterUpdate(cycles: Int, dimension: Int): Int {
        var space: Map<List<Int>, Boolean> = stringToSpace(string, dimension)
        repeat(cycles) { i ->
            space = updateSpace(space)
        }
        val out = space.filter { it.value }.count()
        return out
    }

    fun updateSpace(space: Map<List<Int>, Boolean>): MutableMap<List<Int>, Boolean> {
        val marks: MutableMap<List<Int>, Boolean> = mutableMapOf()
        // enrich space with inactive cubes
        val expandedSpace = space.keys.flatMap { getSurroundingCubes(it) }.map { it to false }.toMap() + space

        // fill marks
        expandedSpace.forEach { (cube, active) ->
            val activeCount = getSurroundingCubes(cube).filter { expandedSpace.getOrDefault(it, false) }.count()
            val newValue = if (active) activeCount in 2..3 else activeCount == 3
            marks[cube] = newValue
        }
        return marks
    }

    fun getSurroundingCubes(cube: List<Int>): List<List<Int>> {
        var out: List<List<Int>> = mutableListOf(emptyList())
        for (i in cube) {
            out = out
                .flatMap { line ->
                    IntRange(i - 1, i + 1)
                        .map { line + it }
                }.filter { it != cube }
        }
        return out
    }

}

class TestSake {
    val input = """.#.
..#
###"""

    @Test
    fun testOnProvidedSamples1() {
        assertEquals(112, DaySeventeen(input).partOne())
        assertEquals(848, DaySeventeen(input).partTwo())
    }

}

class ResultSake {
    @Test
    fun result1() {
        val executor = DaySeventeen(File("src/main/kotlin/day17/input.txt").readText())
        assertEquals(237, executor.partOne())
        assertEquals(2448, executor.partTwo())
    }

}