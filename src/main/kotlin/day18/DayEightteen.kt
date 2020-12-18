package day18

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

data class DayEightteen(val string: String) {
    private val expressions: List<List<Char>> = string.split('\n')
        .map { it.toCharArray().filter { it != ' ' } }

    fun partOne(): Long {
        return expressions.map { executeRecursivelyForPartONe(it).second }.sum()
    }

    private fun executeRecursivelyForPartONe(exp: List<Char>, start: Int = -1): Pair<Int, Long> {
        var curValue = -1L
        var curAction = 'o'
        var index = start
        while (++index < exp.size) {
            when (val c = exp[index]) {
                '(' -> {
                    val pair = executeRecursivelyForPartONe(exp, index)
                    index = pair.first
                    val v = pair.second
                    curValue = if (curValue < 0) v else {
                        if (curAction == '*') curValue * v else curValue + v
                    }
                }
                ')' -> {
                    return index to curValue
                }
                in '0'..'9' -> {
                    val v = (c - '0').toLong()
                    curValue = if (curValue < 0) v else {
                        if (curAction == '*') curValue * v else curValue + v
                    }
                }
                else -> {
                    curAction = c
                }
            }

        }
        return index to curValue
    }

    fun partTwo(): Long {
        return expressions.map { line ->
            executeRecursivelyForPartTwo(
                line.map { e -> if (e.isDigit()) e.toString().toLong() else e })
        }.sum()
    }

    fun getSubListInsideOfParenthesis(list: List<Any>): IntRange {
        val openIndex = list.indexOf('(')
        if (openIndex > -1) {
            var depth = 1
            for (i in openIndex + 1 until list.size) {
                val v = list[i]
                if (v == '(') {
                    depth++
                } else if (v == ')') {
                    depth--
                    if (depth == 0) {
                        return openIndex + 1 until i
                    }
                }
            }
        }
        return IntRange.EMPTY
    }

    private fun executeRecursivelyForPartTwo(line: List<Any>): Long {
        assert(line.isNotEmpty())
        var list = line.toList()
        while (list.size > 1) {
            val range = getSubListInsideOfParenthesis(list)
            if (!range.isEmpty()) {
                val v = executeRecursivelyForPartTwo(list.slice(range))
                list = list.slice(0 until range.first-1) + v + list.slice(range.last+2 until list.size )
            } else {
                val addIndex = list.indexOf('+')
                if (addIndex > -1) {
                    val v = (list[addIndex - 1] as Long).plus(list[addIndex + 1] as Long)
                    list = list.slice(0..addIndex-2 ) + v + list.slice(addIndex+2 until list.size )
                } else {
                    val mulIndex:Int = list.indexOf('*')
                    if (mulIndex > -1) {
                        val v = (list[mulIndex - 1] as Long).times(list[mulIndex + 1] as Long)
                        list = list.slice(0..mulIndex-2 ) + v + list.slice(mulIndex+2 until list.size )
                    }
                }
            }
        }
        return list.first() as Long
    }

}

class TestSake {
    class PartOneTest {
        @Test
        fun testSimple() {
            assertEquals(1, DayEightteen("1").partOne())
            assertEquals(3, DayEightteen("1+2").partOne())
            assertEquals(2, DayEightteen("1*2").partOne())
            assertEquals(5, DayEightteen("1*2+3").partOne())
        }

        @Test
        fun testSimple2() {
            assertEquals(2, DayEightteen("(1+1)").partOne())
            assertEquals(4, DayEightteen("(2*2)").partOne())
            assertEquals(5, DayEightteen("(2*2+1)").partOne())
        }

        @Test
        fun testSimple4() {
            assertEquals(3, DayEightteen("1+(1+1)").partOne())
            assertEquals(4, DayEightteen("1+(1+1+1)").partOne())
            assertEquals(6, DayEightteen("2*(1+1+1)").partOne())
        }

        @Test
        fun testSimple3() {
            assertEquals(4, DayEightteen("2*(1+1)").partOne())
            assertEquals(4, DayEightteen("(1+1)*2").partOne())
            assertEquals(6, DayEightteen("2*(1+1+1)").partOne())
        }

        @Test
        fun testOnProvidedSamples1() {
            assertEquals(71, DayEightteen("1 + 2 * 3 + 4 * 5 + 6").partOne())
        }

        @Test
        fun testOnProvidedSamples2() {
            assertEquals(51, DayEightteen("1 + (2 * 3) + (4 * (5 + 6))").partOne())
        }
    }

    class PartTwoTest {
        @Test
        fun testSimple() {
            assertEquals(1, DayEightteen("1").partTwo())
            assertEquals(3, DayEightteen("1+2").partTwo())
            assertEquals(2, DayEightteen("1*2").partTwo())
            assertEquals(5, DayEightteen("1*2+3").partTwo())
        }

        @Test
        fun testSimple2() {
            assertEquals(2, DayEightteen("(1+1)").partTwo())
            assertEquals(4, DayEightteen("(2*2)").partTwo())
            assertEquals(6, DayEightteen("(2*2+1)").partTwo())
        }

        @Test
        fun testSimple4() {
            assertEquals(3, DayEightteen("1+(1+1)").partTwo())
            assertEquals(4, DayEightteen("1+(1+1+1)").partTwo())
            assertEquals(6, DayEightteen("2*(1+1+1)").partTwo())
        }

        @Test
        fun testSimple5() {
            assertEquals(5, DayEightteen("(((2+3)))").partTwo())
            assertEquals(6, DayEightteen("(((2*3)))").partTwo())
            assertEquals(8, DayEightteen("(((2*3)+2))").partTwo())
            assertEquals(10, DayEightteen("((2+3*2))").partTwo())
            assertEquals(44, DayEightteen("(4 * (5 + 6))").partTwo())
        }

        @Test
        fun testSimple6() {
            assertEquals(15, DayEightteen("(2+5) + (6+2)").partTwo())
            assertEquals(22, DayEightteen("(2*5) + (6*2)").partTwo())
        }

        @Test
        fun testSimple3() {
            assertEquals(4, DayEightteen("2*(1+1)").partTwo())
            assertEquals(4, DayEightteen("(1+1)*2").partTwo())
            assertEquals(6, DayEightteen("2*(1+1+1)").partTwo())
        }

        @Test
        fun testOnProvidedSamples1() {
            assertEquals(231, DayEightteen("1 + 2 * 3 + 4 * 5 + 6").partTwo())
        }

        @Test
        fun testOnProvidedSamples2() {
            assertEquals(51, DayEightteen("1 + (2 * 3) + (4 * (5 + 6))").partTwo())
            assertEquals(46, DayEightteen("2 * 3 + (4 * 5)").partTwo())
            assertEquals(1445, DayEightteen("5 + (8 * 3 + 9 + 3 * 4 * 3)").partTwo())
            assertEquals(669060, DayEightteen("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))").partTwo())
            assertEquals(23340, DayEightteen("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2").partTwo())
        }
    }

}

class ResultSake {
    @Test
    fun result1() {
        val executor = DayEightteen(File("src/main/kotlin/day18/input.txt").readText())
        assertEquals(464478013511, executor.partOne())
        assertEquals(85660197232452, executor.partTwo())
    }

}