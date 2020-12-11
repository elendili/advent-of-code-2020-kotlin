package day11

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

fun List<CharArray>.copy() = List(size) { get(it).clone() }
fun List<CharArray>.humanString() = this.joinToString("\n") { it.joinToString("") }
fun List<CharArray>.countOf(char: Char): Int = this.map { it.count { c -> c == char } }.sum()

class DayEleven(strings: List<String>) {

    private val seats: List<CharArray> = strings.map { it.toCharArray() }

    fun partOne(): Int {
        val listCharArray = seats.copy()
        while (updateLayout(listCharArray, 4) > 0);
        return listCharArray.countOf('#')
    }

    fun partTwo(): Int {
        val listCharArray = seats.copy()
        while (updateLayout(listCharArray, 5, Helper::neighborSymbolCountPartTwo) > 0);
        return listCharArray.countOf('#')
    }

    companion object Helper {
        // bar: (m: String) -> Unit)
        fun updateLayout(
            listCharArray: List<CharArray>, toleranceLevel: Int,
            neighborSymbolCount: (listCharArray: List<CharArray>, row: Int, column: Int) -> Int =
                Helper::neighborSymbolCountPartOne
        ): Int {
            listCharArray.onEachIndexed { row, charArray ->
                charArray.onEachIndexed { column, char ->
                    if (char != '.') {
                        val occupiedNeighbors = neighborSymbolCount(listCharArray, row, column)
                        if (char == 'L' && occupiedNeighbors == 0) {
                            listCharArray[row][column] = '+'
                        }
                        if (char == '#' && occupiedNeighbors >= toleranceLevel) {
                            listCharArray[row][column] = '-'
                        }
                    }
                }
            }
            var changed = 0
            listCharArray.onEachIndexed { row, charArray ->
                charArray.onEachIndexed { column, char ->
                    if (char == '+') {
                        listCharArray[row][column] = '#'
                        changed += 1
                    }
                    if (char == '-') {
                        listCharArray[row][column] = 'L'
                        changed += 1
                    }
                }
            }
            return changed
        }

        fun neighborSymbolCountPartOne(charArray: List<CharArray>, row: Int, column: Int): Int {
            var count = 0
            val ocupiedLabels = arrayOf('#', '-')
            for (i in 0..8) {
                val r = row + (i / 3) - 1
                val c = column + i % 3 - 1
                if (r in charArray.indices // check borders
                    && c in charArray[r].indices // check borders
                    && !(r == row && c == column) // skip center
                    && charArray[r][c] in ocupiedLabels // check label
                ) {
                    count += 1
                }
            }
            return count;
        }

        fun neighborSymbolCountPartTwo(charArray: List<CharArray>, row: Int, column: Int): Int {
            var count = 0
            val occupiedLabels = arrayOf('#', '-')
            // horizontal from origin to left
            for (c in column - 1 downTo 0) {
                if(charArray[row][c]!='.'){
                    if (charArray[row][c] in occupiedLabels) {
                        count += 1
                    }
                    break
                }
            }
            // horizontal from origin to right
            for (c in column + 1 until charArray[row].size) {
                if(charArray[row][c]!='.'){
                    if (charArray[row][c] in occupiedLabels) {
                        count += 1
                    }
                    break
                }
            }
            //vertical from origin to bottom
            for (r in row + 1 until charArray.size) {
                if(charArray[r][column]!='.'){
                    if (charArray[r][column] in occupiedLabels) {
                        count += 1
                    }
                    break
                }
            }
            // vertical from origin to top
            for (r in row - 1 downTo 0) {
                if(charArray[r][column]!='.'){
                    if (charArray[r][column] in occupiedLabels) {
                        count += 1
                    }
                    break
                }
            }
            // diagonal from origin to left bottom
            for (r in row + 1 until charArray.size) {
                val c = column - (r - row)
                if (c < 0 || c >= charArray[row].size) {
                    break
                }
                if(charArray[r][c]!='.'){
                    if (charArray[r][c] in occupiedLabels) {
                        count += 1
                    }
                    break
                }
            }
            // diagonal from origin to right bottom
            for (r in row + 1 until charArray.size) {
                val c = column + (r - row)
                if (c < 0 || c >= charArray[row].size) {
                    break
                }
                if(charArray[r][c]!='.'){
                    if (charArray[r][c] in occupiedLabels) {
                        count += 1
                    }
                    break
                }
            }
            // diagonal from origin to left top
            for (r in row - 1 downTo 0) {
                val c = column - (row - r)
                if (c < 0 || c >= charArray[row].size) {
                    break
                }
                if(charArray[r][c]!='.'){
                    if (charArray[r][c] in occupiedLabels) {
                        count += 1
                    }
                    break
                }
            }
            // diagonal from origin to right top
            for (r in row - 1 downTo 0) {
                val c = column + (row - r)
                if (c < 0 || c >= charArray[row].size) {
                    break
                }
                if(charArray[r][c]!='.'){
                    if (charArray[r][c] in occupiedLabels) {
                        count += 1
                    }
                    break
                }
            }
            return count;
        }

    }

}

class TestSake {
    private val input1 = """L.LL.LL.LL
LLLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLLL
L.LLLLLL.L
L.LLLLL.LL""".split("\n")

    @Test
    fun whiteBoxTestPartOneSimple() {
        var input = """###
            |###
            |###""".trimMargin().split("\n").map { it.toCharArray() }
        assertEquals(9, input.countOf('#'))
        DayEleven.updateLayout(input, 4)
        assertEquals(
            """#L#
                |LLL
                |#L#""".trimMargin(),
            input.humanString()
        )
        assertEquals(4, input.countOf('#'))
    }

    @Test
    fun whiteBoxTestSimplePartTwo1() {
        var input =
""".......#.
...#.....
.#.......
.........
..#L....#
....#....
.........
#........
...#.....""".trimMargin().split("\n").map { it.toCharArray() }
        assertEquals(8, DayEleven.neighborSymbolCountPartTwo(input, 4, 3))
    }

    @Test
    fun whiteBoxTestSimplePartTwo2() {
        var input = """.............
.L.L.#.#.#.#.
.............""".trimMargin().split("\n").map { it.toCharArray() }
        assertEquals(0, DayEleven.neighborSymbolCountPartTwo(input, 1, 1))
    }

    @Test
    fun whiteBoxTestSimplePartTwoNoOccupied() {
        var input =
            """.##L##.
#.#L#.#
##.L.##
...L...
##.L.##
#.#L#.#
.##.##.""".trimMargin().split("\n").map { it.toCharArray() }
        assertEquals(0, DayEleven.neighborSymbolCountPartTwo(input, 3, 3))
        assertEquals(6, DayEleven.neighborSymbolCountPartTwo(input, 4, 3))
        assertEquals(6, DayEleven.neighborSymbolCountPartTwo(input, 5, 3))
        assertEquals(4, DayEleven.neighborSymbolCountPartTwo(input, 6, 3))
        assertEquals(6, DayEleven.neighborSymbolCountPartTwo(input, 2, 3))
        assertEquals(6, DayEleven.neighborSymbolCountPartTwo(input, 1, 3))
        assertEquals(4, DayEleven.neighborSymbolCountPartTwo(input, 0, 3))
    }

    @Test
    fun whiteBoxTestSimplePartTwo3() {
        var input = """##
            |##
        """.trimMargin().split("\n").map { it.toCharArray() }
        assertEquals(3, DayEleven.neighborSymbolCountPartTwo(input, 0, 0))
        assertEquals(3, DayEleven.neighborSymbolCountPartTwo(input, 1, 1))
        assertEquals(3, DayEleven.neighborSymbolCountPartTwo(input, 1, 0))
        assertEquals(3, DayEleven.neighborSymbolCountPartTwo(input, 0, 1))
    }

    @Test
    fun whiteBoxTestPartOne() {
        val a = input1.map { it.toCharArray() }

        DayEleven.updateLayout(a, 4)
        assertEquals(
            """#.##.##.##
#######.##
#.#.#..#..
####.##.##
#.##.##.##
#.#####.##
..#.#.....
##########
#.######.#
#.#####.##""",
            a.humanString()
        )

        DayEleven.updateLayout(a, 4)
        assertEquals(
            """#.LL.L#.##
#LLLLLL.L#
L.L.L..L..
#LLL.LL.L#
#.LL.LL.LL
#.LLLL#.##
..L.L.....
#LLLLLLLL#
#.LLLLLL.L
#.#LLLL.##""",
            a.humanString()
        )
        DayEleven.updateLayout(a, 4)
        assertEquals(
            """#.##.L#.##
#L###LL.L#
L.#.#..#..
#L##.##.L#
#.##.LL.LL
#.###L#.##
..#.#.....
#L######L#
#.LL###L.L
#.#L###.##""",
            a.humanString()
        )

        DayEleven.updateLayout(a, 4)
        assertEquals(
            """#.#L.L#.##
#LLL#LL.L#
L.L.L..#..
#LLL.##.L#
#.LL.LL.LL
#.LL#L#.##
..L.L.....
#L#LLLL#L#
#.LLLLLL.L
#.#L#L#.##""",
            a.humanString()
        )
        DayEleven.updateLayout(a, 4)
        assertEquals(
            """#.#L.L#.##
#LLL#LL.L#
L.#.L..#..
#L##.##.L#
#.#L.LL.LL
#.#L#L#.##
..L.L.....
#L#L##L#L#
#.LLLLLL.L
#.#L#L#.##""",
            a.humanString()
        )
    }


    @Test
    fun whiteBoxTestPartTwoSimple() {
        var input = """###
            |###
            |###""".trimMargin().split("\n").map { it.toCharArray() }

        DayEleven.updateLayout(input, 5, DayEleven.Helper::neighborSymbolCountPartTwo)
        assertEquals("""#L#
                |LLL
                |#L#""".trimMargin(),
            input.humanString()
        )
        assertEquals(4, input.countOf('#'))

        DayEleven.updateLayout(input, 5, DayEleven.Helper::neighborSymbolCountPartTwo)
        assertEquals("""#L#
                |LLL
                |#L#""".trimMargin(),
            input.humanString()
        )
        assertEquals(4, input.countOf('#'))
    }


    @Test
    fun whiteBoxTestPartTwo() {
        val a = input1.map { it.toCharArray() }

        DayEleven.updateLayout(a, 5, DayEleven.Helper::neighborSymbolCountPartTwo)
        assertEquals(
            """#.##.##.##
#######.##
#.#.#..#..
####.##.##
#.##.##.##
#.#####.##
..#.#.....
##########
#.######.#
#.#####.##""",
            a.humanString()
        )

        DayEleven.updateLayout(a, 5, DayEleven.Helper::neighborSymbolCountPartTwo)
        assertEquals(
            """#.LL.LL.L#
#LLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLL#
#.LLLLLL.L
#.LLLLL.L#""",
            a.humanString()
        )

        val o = DayEleven.updateLayout(a, 5, DayEleven.Helper::neighborSymbolCountPartTwo)
        assertEquals(
            """#.L#.##.L#
#L#####.LL
L.#.#..#..
##L#.##.##
#.##.#L.##
#.#####.#L
..#.#.....
LLL####LL#
#.L#####.L
#.L####.L#""",
            a.humanString()
        )
    }

    @Test
    fun testOnProvidedSamplesBase() {
        assertEquals(37, DayEleven(input1).partOne())
        assertEquals(26, DayEleven(input1).partTwo())
    }

}

class ResultSake {
    @Test
    fun result() {
        val executor = DayEleven(File("src/main/kotlin/day11/input.txt").readLines())
        assertEquals(2283, executor.partOne())
        assertEquals(2054, executor.partTwo())
    }
}