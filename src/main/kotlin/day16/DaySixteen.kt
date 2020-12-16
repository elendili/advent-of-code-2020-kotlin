package day16

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

data class DaySixteen(val string: String) {
    private val yourTicket: List<Int>
    private val nearByTickets: List<List<Int>>
    private val categoryRules: Map<String, List<IntRange>>

    init {
        val data = string.split(Regex("\n+\\w+ tickets?:\n+")).map { it.split('\n') }
        categoryRules = data[0].map { line ->
            val separated = line.split(Regex(": | or "))

            val range0Raw = separated[1].split("-").map { it.toInt() }
            val range0 = IntRange(range0Raw[0], range0Raw[1])

            val range1Raw = separated[2].split("-").map { it.toInt() }
            val range1 = IntRange(range1Raw[0], range1Raw[1])

            separated[0] to listOf(range0, range1)
        }.toMap()
        yourTicket = data[1][0].split(',').map { it.toInt() }
        nearByTickets = data[2].map {
            it.split(',').map { it.toInt() }
        }
    }

    fun partOne(): Int {
        val misMatchedNumbers = nearByTickets.flatMap { ticketNumbers ->
            ticketNumbers.filter { n ->
                val anyMatched = categoryRules.values.flatten().any {
                    n in it
                }
                !anyMatched
            }
        }
        return misMatchedNumbers.sum()
    }

    fun partTwo(): Long {
        val filtered = getPersonalTicketCategoriesValues().filter { it.key.startsWith("departure") }
        assert(filtered.size == 6)
        val multiplied = filtered.values.map { it.toLong() }.reduce { acc, i -> acc * i }
        return multiplied
    }

    fun getPersonalTicketCategoriesValues(): Map<String, Int> {
        return getCategoriesWithColumnNumbers().map { e -> e.key to yourTicket[e.value] }.toMap()
    }

    fun getCategoriesWithColumnNumbers(): Map<String, Int> {
        val cleanedTickets = cleanTickets()
        val dirtyCategoriesToPossibleColumns = categoryRules.map {
            findColumnNumber(cleanedTickets, it)
        }.toMap()
        return arrangeColumnNumbers(dirtyCategoriesToPossibleColumns)
    }

    fun arrangeColumnNumbers(_dirtyCategoriesToPossibleColumns: Map<String, List<Int>>): Map<String, Int> {
        val dirtyCategoriesToPossibleColumns: MutableMap<String, MutableList<Int>> =
            _dirtyCategoriesToPossibleColumns.map { it.key to it.value.toMutableList() }.toMap().toMutableMap()
        val out: MutableMap<String, Int> = mutableMapOf()
        while (dirtyCategoriesToPossibleColumns.isNotEmpty()) {
            val categoryWith1Column = dirtyCategoriesToPossibleColumns.filter { e -> e.value.size == 1 }
            assert(categoryWith1Column.size == 1)
            val name = categoryWith1Column.keys.first()
            val column = categoryWith1Column.values.first()[0]
            out[name] = column
            dirtyCategoriesToPossibleColumns.remove(name)
            dirtyCategoriesToPossibleColumns.forEach { e -> e.value.remove(column) }
        }
        return out
    }

    private fun findColumnNumber(
        ticketsNumbers: List<List<Int>>,
        categoryEntry: Map.Entry<String, List<IntRange>>
    ): Pair<String, List<Int>> {
        val out: MutableList<Int> = mutableListOf()
        for (i in yourTicket.indices) {
            if (ticketsNumbers.all { ticket ->
                    categoryEntry.value.any { range ->
                        ticket[i] in range
                    }
                }) {
                out.add(i)
            }
        }
        return categoryEntry.key to out
    }

    fun cleanTickets(): List<List<Int>> {
        return nearByTickets.filter { ticketNumbers ->
            ticketNumbers.all { n ->
                val anyMatched = categoryRules.values.flatten().any {
                    n in it
                }
                anyMatched
            }
        }
    }
}

class TestSake {
    val input = """class: 1-3 or 5-7
row: 6-11 or 33-44
seat: 13-40 or 45-50

your ticket:
7,1,14

nearby tickets:
7,3,47
40,4,50
55,2,20
38,6,12"""

    val input2 = """class: 0-1 or 4-19
row: 0-5 or 8-19
seat: 0-13 or 16-19

your ticket:
11,12,13

nearby tickets:
3,9,18
15,1,5
5,14,9"""

    @Test
    fun testOnProvidedSamples1() {
        assertEquals(71, DaySixteen(input).partOne())
    }

    @Test
    fun testOnProvidedSamplesPartTwoSake() {
        assertEquals(listOf(listOf(7, 3, 47)), DaySixteen(input).cleanTickets())
        assertEquals(mapOf("row" to 0, "class" to 1, "seat" to 2), DaySixteen(input2).getCategoriesWithColumnNumbers())
        assertEquals(
            mapOf("row" to 11, "class" to 12, "seat" to 13), DaySixteen(input2)
                .getPersonalTicketCategoriesValues()
        )
    }

}

class ResultSake {
    @Test
    fun result1() {
        val executor = DaySixteen(File("src/main/kotlin/day16/input.txt").readText())
        assertEquals(21956, executor.partOne())
    }

    @Test
    fun result2() {
        val executor = DaySixteen(File("src/main/kotlin/day16/input.txt").readText())
        assertEquals(3709435214239, executor.partTwo())
    }
}