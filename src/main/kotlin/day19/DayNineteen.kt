package day19

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

const val sample1 = """0: 4 1 5
1: 2 3 | 3 2
2: 4 4 | 5 5
3: 4 5 | 5 4
4: "a"
5: "b"

ababbb
bababa
abbbab
aaabbb
aaaabbb"""

const val sample2 = """42: 9 14 | 10 1
9: 14 27 | 1 26
10: 23 14 | 28 1
1: "a"
11: 42 31
5: 1 14 | 15 1
19: 14 1 | 14 14
12: 24 14 | 19 1
16: 15 1 | 14 14
31: 14 17 | 1 13
6: 14 14 | 1 14
2: 1 24 | 14 4
0: 8 11
13: 14 3 | 1 12
15: 1 | 14
17: 14 2 | 1 7
23: 25 1 | 22 14
28: 16 1
4: 1 1
20: 14 14 | 1 15
3: 5 14 | 16 1
27: 1 6 | 14 18
14: "b"
21: 14 1 | 1 14
25: 1 1 | 1 14
22: 14 14
8: 42
26: 14 22 | 1 20
18: 15 15
7: 14 5 | 1 21
24: 14 1

abbbbbabbbaaaababbaabbbbabababbbabbbbbbabaaaa
bbabbbbaabaabba
babbbbaabbbbbabbbbbbaabaaabaaa
aaabbbbbbaaaabaababaabababbabaaabbababababaaa
bbbbbbbaaaabbbbaaabbabaaa
bbbababbbbaaaaaaaabbababaaababaabab
ababaaaaaabaaab
ababaaaaabbbaba
baabbaaaabbaaaababbaababb
abbbbabbbbaaaababbbbbbaaaababb
aaaaabbaabaaaaababaa
aaaabbaaaabbaaa
aaaabbaabbaaaaaaabbbabbbaaabbaabaaa
babaaabbbaaabaababbaabababaaab
aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba"""

fun cartesianMultiplication(input: List<Set<String>>): Sequence<String> {
    var out: Sequence<String> = sequenceOf("")
    for (list in input) {
        out = out.flatMap { fromOut ->
            list.map { s ->
                fromOut + s
            }
        }
    }
    return out
}

open class Rule
class Terminal(var char: Char) : Rule()
class Choice(var choices: List<List<Int>>) : Rule()

open class DayNineteen(val string: String) {
    private val receivedMessages: List<String>
    internal val refRules: MutableMap<Int, Rule>

    init {
        val (rulesS, receivedMS) = string.split("\n\n")
        val rulesLines = rulesS.split('\n').map { line ->
            val (k, v) = line.split(':')
            k.toInt() to v
        }

        refRules = rulesLines.map { p ->
            if (p.second.contains("\"")) {
                val v = p.second.replace("\"", "")
                    .replace(" ", "")[0]
                p.first to Terminal(v)
            } else {
                val v = p.second.split('|').map { sublist ->
                    sublist.split(' ').filter { it.isNotEmpty() }.map { num ->
                        num.toInt()
                    }
                }
                p.first to Choice(v)
            }
        }.toMap().toMutableMap()

        receivedMessages = receivedMS.split('\n').sortedBy { it.length }

        println("Rules ${refRules.size}")
        println("Input Messages ${receivedMessages.size}")

    }

    fun process(): Int {
        return receivedMessages.filter { recursiveMatch(it, listOf(0)) }.count()
    }

    private fun recursiveMatch(message: String, pda: List<Int>): Boolean {
        if (pda.isEmpty()) {
            return message.isEmpty()
        }
        return when (val rule = refRules[pda.first()]!!) {
            is Terminal -> message.startsWith(rule.char)
                    && recursiveMatch(message.drop(1), pda.drop(1))
            is Choice -> rule.choices.indexOfFirst { choice ->
                recursiveMatch(message, choice + pda.drop(1))
            } > -1
            else -> TODO("NOOOO")
        }
    }

}

class DayNineteenPartTwo(string: String) : DayNineteen(string) {
    init {
        refRules[8] = Choice(
            listOf(
                listOf(42),
                listOf(42, 8)
            )
        )
        refRules[11] = Choice(
            listOf(
                listOf(42, 31),
                listOf(42, 11, 31)
            )
        )
    }
}

class TestSakePartOne {

    val sut = DayNineteen(sample1)

    @Test
    fun test() {
        assertEquals(2, sut.process())
    }
}

class TestSakePartTwo {

    @Test
    fun test1() {
        assertEquals(3, DayNineteen(sample2).process())
    }

    @Test
    fun test2() {
        assertEquals(12, DayNineteenPartTwo(sample2).process())
    }

}

class ResultSake {
    @Test
    fun result1() {
        val executor = DayNineteen(File("src/main/kotlin/day19/input.txt").readText())
        assertEquals(208, executor.process())
    }

    @Test
    fun result2() {
        val executor = DayNineteenPartTwo(File("src/main/kotlin/day19/input.txt").readText())
        assertEquals(316, executor.process())
    }

}