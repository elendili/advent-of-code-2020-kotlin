package day9

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File


class DayNine(strings: List<String>, preamble: Int) {

    private val numbers: List<Long> = strings.map { it.toLong() }
    private val preamble = preamble

    fun partOne(): Long {
        // numbers and their count in window
        val windowMap: MutableMap<Long, Int> = hashMapOf()
        numbers.forEachIndexed { index, num ->
            if (index >= preamble) {
                if (!isSumOfNumbers(windowMap, num)) {
                    return num
                }
                val firstInWindow = numbers[index - preamble]
                // shift window
                windowMap.compute(firstInWindow) { _, v -> if (v != null && v > 1) v - 1 else null }
            }
            windowMap.compute(num) { _, v -> if (v == null) 1 else v + 1 }
        }
        return -1
    }

    fun isSumOfNumbers(nums: Map<Long, Int>, num: Long): Boolean {
        return nums.any {
            val n = num - it.key
            n != it.key && nums.containsKey(n)
        }
    }

    fun partTwo(targetSum: Long): Long {
        var slidingSum = targetSum
        numbers.forEachIndexed { index, num ->
            var rightIndex = index
            while (slidingSum > 0 && rightIndex < numbers.size) {
                val subtrahend = numbers[rightIndex++]
                slidingSum -= subtrahend
            }
            if (slidingSum < 0L) {
                slidingSum = targetSum
            } else if (slidingSum == 0L) {
                // contiguous set of numbers is found
                return sumOfMinMax(numbers.subList(index, rightIndex))
            }
        }
        return -1
    }

    fun sumOfMinMax(nums: List<Long>): Long {
        var min = Long.MAX_VALUE
        var max = Long.MIN_VALUE
        nums.forEach {
            min = minOf(min, it)
            max = maxOf(max, it)
        }
        return min + max
    }

}

class TestSake {
    val input = """35
20
15
25
47
40
62
55
65
95
102
117
150
182
127
219
299
277
309
576""".split("\n")

    @Test
    fun testOnProvidedSamplesBase() {
        val executor = DayNine(input, 5);
        assertEquals(127, executor.partOne())
        assertEquals(62, executor.partTwo(127))
    }

}


class ResultSake {
    @Test
    fun result() {
        val executor = DayNine(File("src/main/kotlin/day9/input.txt").readLines(), 25)
        assertEquals(29221323, executor.partOne())
        assertEquals(4389369, executor.partTwo(29221323))
    }
}
// 200 + 200 = 400
// 400 + 400 = 800
// 800 + 800 =