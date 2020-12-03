package day3

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class DayThree {
//    Starting at the top-left corner of your map and following a slope of right 3 and down 1, how many trees would you encounter?
    fun process(strings: List<String>, vararg slopes: Pair<Int,Int>): Long {
        val width = strings[0].length
        val resultList:MutableList<Int> = mutableListOf()
        slopes.forEach {
            var result = 0
            var x=0; var dx = it.first
            var y=0; var dy = it.second
            while(y<strings.size) {
                val current = strings[y][x]
                if (current=='#'){ // meet a tree
                    result += 1
                }
                x=(x+dx)%width
                y+=dy
            }
            resultList.add(result)
        }
        val out:Long = resultList.fold(1L, { acc:Long, i -> acc * i })
        return out
    }

    fun process(string: String, vararg slopes: Pair<Int,Int>): Long {
        return process(string.split("\n"),*slopes)
    }

}


class TestSake1 {
    @Test
    fun testOnProvidedSamples() {
        val input =
"""..##.......
#...#...#..
.#....#..#.
..#.#...#.#
.#...##..#.
..#.##.....
.#.#.#....#
.#........#
#.##...#...
#...##....#
.#..#...#.#"""
        assertEquals(7, DayThree().process(input,Pair(3,1)))
        assertEquals(336, DayThree().process(input,
            Pair(1,1), Pair(3,1), Pair(5,1), Pair(7,1), Pair(1,2)
        ))
    }
}

class ResultSake {
    @Test
    fun result() {
        val input = File("src/main/kotlin/day3/input.txt").readLines()
        assertEquals(278, DayThree().process(input,Pair(3,1)))
        assertEquals(9709761600, DayThree().process(input,
            Pair(1,1), Pair(3,1), Pair(5,1), Pair(7,1), Pair(1,2)
        ))
    }
}
