package day7

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

/**
 * get mapping of container bag name to possible content bags with their count
 */
fun convertToMap(strings: List<String>): Map<String, Map<String, Int>> {
    val out: MutableMap<String, MutableMap<String, Int>> = mutableMapOf();
    strings.forEach { line ->
        val (kS, valS) = line.replace(Regex("bags?"), "")
            .trimEnd('.').split("contain")
        val inMap: MutableMap<String, Int> = mutableMapOf()
        out[kS.trim()] = inMap
        if (!valS.contains("no")) {
            valS.split(",").forEach {
                val (num, name) = it.trim().split(" ", limit = 2)
                inMap[name] = num.toInt()
            }
        }

    }
    return out
}

/**
 * get mapping of internal bag name to possible container bags
 */
fun contentToContainerMapping(map: Map<String, Map<String, Int>>): Map<String, Set<String>> {
    val out: MutableMap<String, MutableSet<String>> = mutableMapOf();
    map.forEach { (container, content) ->
        content.forEach { (name, _) ->
            val set: MutableSet<String> = out.getOrPut(name) { mutableSetOf() }
            set.add(container)
        }
    }
    return out
}

class DaySevenPartOne {

    fun process(strings: List<String>, name: String): Int {
        return process(contentToContainerMapping(convertToMap(strings)), name).size
    }

    private fun process(contentToContainerMap: Map<String, Set<String>>, name: String): Set<String> {
        val outSet: MutableSet<String> = mutableSetOf()
        val setForName: Set<String> = contentToContainerMap.getOrDefault(name, emptySet())
        outSet.addAll(setForName)
        outSet.addAll(setForName.flatMap { process(contentToContainerMap, it) }.toSet())
        return outSet
    }
}

class DaySevenPartTwo {

    fun process(strings: List<String>, name: String): Int {
        return getCount(convertToMap(strings), name)
    }

    private fun getCount(map: Map<String, Map<String, Int>>, name: String): Int {
        val nested = map.getOrDefault(name, emptyMap())
        var out = 0
        nested.forEach { (nestedName, count) ->
            out += count
            val nestedCount = getCount(map, nestedName)
            out += count * nestedCount
        }
        return out
    }

}

class TestSake1 {
    val input = """light red bags contain 1 bright white bag, 2 muted yellow bags.
dark orange bags contain 3 bright white bags, 4 muted yellow bags.
bright white bags contain 1 shiny gold bag.
muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
dark olive bags contain 3 faded blue bags, 4 dotted black bags.
vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
faded blue bags contain no other bags.
dotted black bags contain no other bags.""".split("\n")

    @Test
    fun testOnProvidedSamplesBase() {
        var directMap = convertToMap(input)
        assertEquals(
            "{light red={bright white=1, muted yellow=2}, dark orange={bright white=3, muted yellow=4}, bright white={shiny gold=1}, muted yellow={shiny gold=2, faded blue=9}, shiny gold={dark olive=1, vibrant plum=2}, dark olive={faded blue=3, dotted black=4}, vibrant plum={faded blue=5, dotted black=6}, faded blue={}, dotted black={}}",
            directMap.toString()
        )
        assertEquals(
            "{bright white=[light red, dark orange], muted yellow=[light red, dark orange], shiny gold=[bright white, muted yellow], faded blue=[muted yellow, dark olive, vibrant plum], dark olive=[shiny gold], vibrant plum=[shiny gold], dotted black=[dark olive, vibrant plum]}",
            contentToContainerMapping(directMap).toString()
        )
    }

    @Test
    fun testOnProvidedSamplesPartOne() {
        assertEquals(
            4, DaySevenPartOne().process(input, "shiny gold")
        )
    }

    @Test
    fun testOnProvidedSamplePartTwo() {

        val input0 = """shiny gold bags contain 2 dark red bags.
dark red bags contain no other bags.""".split("\n")
        assertEquals(2, DaySevenPartTwo().process(input0, "shiny gold"))

        assertEquals(32, DaySevenPartTwo().process(input, "shiny gold"))

        val input2 = """shiny gold bags contain 2 dark red bags.
dark red bags contain 2 dark orange bags.
dark orange bags contain 2 dark yellow bags.
dark yellow bags contain 2 dark green bags.
dark green bags contain 2 dark blue bags.
dark blue bags contain 2 dark violet bags.
dark violet bags contain no other bags.""".split("\n")
        assertEquals(126, DaySevenPartTwo().process(input2, "shiny gold"))
    }
}


class ResultSake {
    @Test
    fun result() {
        val input = File("src/main/kotlin/day7/input.txt").readLines()
        assertEquals(261, DaySevenPartOne().process(input, "shiny gold"))
        assertEquals(3765, DaySevenPartTwo().process(input, "shiny gold"))
    }
}
