package day14

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

data class DayFourteen(val strings: List<String>) {

    fun partOne(): Long {
        var setOneMaskOr = 0L
        var setZeroMaskAnd = 0L.inv()
        val map: MutableMap<Long, Long> = mutableMapOf()
        strings.forEach { line ->
            val (key, value) = line.split(" = ")
            if (key.startsWith("mask")) {
                setOneMaskOr = 0L
                setZeroMaskAnd = 0L.inv()
                value.forEachIndexed { i, e ->
                    if (e == '1') {
                        setOneMaskOr = setOneMaskOr or (1L shl (35 - i))
                    } else if (e == '0') {
                        setZeroMaskAnd = setZeroMaskAnd and (1L shl (35 - i)).inv()
                    }
                }
            } else {
                val keyLong = key.replace(Regex("\\D"), "").toLong()
                val valueLong1 = value.toLong()
                val valueLong2 = valueLong1 or setOneMaskOr
                val valueLong3 = valueLong2 and setZeroMaskAnd
                map[keyLong] = valueLong3
            }

        }
        return map.values.sum()
    }

    fun partTwo(): Long {
        var currentMask = ""
        val map: MutableMap<Long, Long> = mutableMapOf()
        strings.forEach { line ->
            val (key, value) = line.split(" = ")
            if (key.startsWith("mask")) {
                currentMask = value
            } else {
                val originAddress = key.replace(Regex("\\D"), "").toLong()
                val valueLong = value.toLong()
                // apply 1
                var addressWithApplied01 = originAddress
                currentMask.withIndex().filter { it.value == '1' }.map { 35 - it.index }.forEach {
                    addressWithApplied01 = addressWithApplied01 or (1L shl it)
                }
                // apply X
                val set = mutableSetOf(addressWithApplied01)
                currentMask.withIndex().filter { it.value == 'X' }.map { 35 - it.index }.forEach { shift ->
                    val set2 = set.flatMap {
                        listOf(
                            it or (1L shl shift),
                            it and (1L shl shift).inv()
                        )
                    }
                    set.addAll(set2)
                }
                set.forEach { map[it] = valueLong }
            }
        }
        return map.values.sum()
    }

}

class TestSake {
    private val input1 = """mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X
mem[8] = 11
mem[7] = 101
mem[8] = 0""".split('\n')

    private val input2 = """mask = 000000000000000000000000000000X1001X
mem[42] = 100
mask = 00000000000000000000000000000000X0XX
mem[26] = 1""".split('\n')

    @Test
    fun testOnProvidedSamplesBase1() {
        assertEquals(165, DayFourteen(input1).partOne())
    }

    @Test
    fun testOnProvidedSamplesBase2() {
        assertEquals(208, DayFourteen(input2).partTwo())
    }

}

class ResultSake {
    @Test
    fun result1() {
        val executor = DayFourteen(File("src/main/kotlin/day14/input.txt").readLines())
        assertEquals(7997531787333, executor.partOne())
        assertEquals(3564822193820, executor.partTwo())
    }
}