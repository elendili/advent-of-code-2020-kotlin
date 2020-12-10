package day10

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File


class DayTen {
    constructor(strings: List<String>) {
        this.numbers = strings.map { it.toLong() }.sorted()
    }

    constructor(vararg numbers: Int) {
        this.numbers = numbers.map { it.toLong() }.sorted()
    }

    private val numbers: List<Long>

    fun partOne(): Long {
        val diffs = getDifferences()
        return diffs.getOrDefault(1, 0) * diffs.getOrDefault(3, 0)
    }

    fun getDifferences(): Map<Long, Long> {
        val diffs: MutableMap<Long, Long> = mutableMapOf()
        diffs[3] = 1 // the difference between highest joltage adapter and embedded adapter is already known
        numbers.fold(0L) { previous, element ->
            diffs.compute(element - previous) { _, v -> if (v == null) 1 else v + 1 }
            element
        }
        return diffs
    }

    fun partTwo(): Long {
        // tribonacci numbers are involved here >_<
        val array = numbers.toMutableList()
        array.add(array.last() + 3)   // add device adapter
        array.add(0, 0) // add 0 adapter
        val count = array.size
        val memo = LongArray(count)          // mapping of index in array to paths count
        memo[count - 1] = 1                  // only 1 path for device adapter
        for (i in count - 2 downTo 0) { // iterate memo table, skip highest value
            var j = 1
            // iterate to add previous higher values
            while (i + j < count                 // no more than memo array size
                && array[i + j] <= array[i] + 3  /* higher joltage value is eligible
                 to be considered when in borders current value..current value+3)  */
            ) {
                memo[i] += memo[i + j] // add calculated value from right (higher) to current cell
                j++
            }
        }
        return memo[0]
    }


}

class TestSake {
    private val input1 = """16
10
15
5
1
11
7
19
6
12
4""".split("\n")
    private val input2 = """28
33
18
42
31
14
46
20
48
47
24
23
49
45
19
38
39
11
1
32
25
35
8
17
7
9
4
2
34
10
3""".split("\n")

    @Test
    fun testFirst() {
        assertEquals(7 * 5, DayTen(input1).partOne())
        assertEquals(22 * 10, DayTen(input2).partOne())
    }

    @Test
    fun testOnMyData() {
        assertEquals(1, DayTen(1).partTwo())                 // = 1+0
        assertEquals(2, DayTen(1, 2).partTwo())              // = 1+1+0
        assertEquals(4, DayTen(1, 2, 3).partTwo())           // = 2+1+1
        assertEquals(7, DayTen(1, 2, 3, 4).partTwo())        // = 4+2+1
        assertEquals(13, DayTen(1, 2, 3, 4, 5).partTwo())    // = 7+4+2
        assertEquals(24, DayTen(1, 2, 3, 4, 5, 6).partTwo()) // = 13+7+4


        assertEquals(2, DayTen(1, 3).partTwo())
        assertEquals(3, DayTen(1, 3, 4).partTwo())        // 1+2
        assertEquals(5, DayTen(1, 2, 4, 5).partTwo())     // 1+2+3
        assertEquals(10, DayTen(1, 3, 4, 5, 6).partTwo()) // = 13+7+4
    }

    @Test
    fun testOnProvidedSamplesBase() {
        assertEquals(8, DayTen(input1).partTwo())
        assertEquals(19208, DayTen(input2).partTwo())
    }


}


class ResultSake {
    @Test
    fun result() {
        val executor = DayTen(File("src/main/kotlin/day10/input.txt").readLines())
        assertEquals(1876, executor.partOne())
        assertEquals(14173478093824, executor.partTwo())
    }
}


/*
last always will be in place, because output is 3 joltage higher
(0) 1 4 5
=> 1 // can't be optimized

(0) 1 3 5
(0) 3 5
=> 2

(0) 1 3 4 5
(0) 1 4 5
(0) 3 5
=> 3

(0) 1 2 3 (4)
(0) 2 (4)
(0) 3 (4)
(0) 1 (4)
(0) 1 2 (4)
(0) 1 3 (4)
(0) 2 3 (4)
=> 7

(0) 1 2 4 (5)
(0) 2 (5)
(0) 2 4 (5)
(0) 1 2 (5)
(0) 1 4 (5)
=> 5


 */